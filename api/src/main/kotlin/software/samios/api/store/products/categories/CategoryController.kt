package software.samios.api.store.products.categories

import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.types.ObjectId
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryRepository: CategoryRepository,
    private val categoryService: CategoryService
) {

    companion object {
        const val categoryTreeCacheKey = "category_tree"
    }

    @GetMapping
    @Cacheable(value = [categoryTreeCacheKey])
    fun getCategoryTree(): List<CategoryTreeNode> {
        val categories = categoryRepository.findAll()
        val categoryMap = categories.associateBy { it.id!! }
        val rootCategories = mutableListOf<CategoryTreeNode>()

        for (category in categories) {
            if (category.parentId.isNullOrEmpty()) {
                val rootNode = CategoryTreeNode(category.id!!, category.name, category.getImageUrl(), emptyList())
                buildCategoryTree(rootNode, categoryMap)
                rootCategories.add(rootNode)
            }
        }

        return rootCategories
    }

    private fun buildCategoryTree(parentNode: CategoryTreeNode, categoryMap: Map<String, Category>) {
        val childCategories = categoryMap.filterValues { it.parentId == parentNode.id }
            .map { CategoryTreeNode(it.value.id!!, it.value.name, it.value.getImageUrl(), emptyList()) }

        for (child in childCategories) {
            buildCategoryTree(child, categoryMap)
        }
        parentNode.children = childCategories
    }

    @CacheEvict(value = [categoryTreeCacheKey], allEntries = true)
    @PostMapping(consumes = ["multipart/form-data"])
    @PreAuthorize("@staffAuth.hasRole(authentication, T(software.samios.api.user.StaffAccess).STAFF)")
    fun createCategory(
        @RequestPart("category") categoryString: String,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<String> {
        // Parse category JSON
        val objectMapper = ObjectMapper()
        val category = objectMapper.readValue(categoryString, Category::class.java)

        if (category.id != null) {
            return ResponseEntity.badRequest().body("Cannot specify ID for a new category")
        }

        // We need to create the ID here in order to save the photo.
        category.id = ObjectId().toString()
        categoryService.saveCategoryWithImage(category, file)

        return ResponseEntity.ok("Category created successfully")
    }

    @CacheEvict(value = [categoryTreeCacheKey], allEntries = true)
    @PutMapping("/{id}")
    @PreAuthorize("@staffAuth.hasRole(authentication, T(software.samios.api.user.StaffAccess).STAFF)")
    fun updateCategory(
        @PathVariable id: String,
        @RequestBody updatedCategory: Category,
        @RequestParam(required = false) parentId: String?
    ): ResponseEntity<String> {
        val existingCategory = categoryRepository.findById(id)
        if (existingCategory.isPresent) {
            val categoryToUpdate = existingCategory.get()

            // Update category properties
            categoryToUpdate.name = updatedCategory.name

            if (parentId != null) {
                val parentCategory = categoryRepository.findById(parentId)
                if (parentCategory.isPresent) {
                    categoryToUpdate.parentId = parentCategory.get().toString()
                } else {
                    return ResponseEntity.badRequest().body("Invalid parent category ID")
                }
            } else {
                categoryToUpdate.parentId = null
            }
            categoryRepository.save(categoryToUpdate)
            return ResponseEntity.ok("Category updated successfully")
        }
        return ResponseEntity.notFound().build()
    }

    @CacheEvict(value = [categoryTreeCacheKey], allEntries = true)
    @DeleteMapping("/{id}")
    @PreAuthorize("@staffAuth.hasRole(authentication, T(software.samios.api.user.StaffAccess).STAFF)")
    fun deleteCategory(@PathVariable id: String): ResponseEntity<String> {
        val existingCategory = categoryRepository.findById(id)
        if (existingCategory.isPresent) {
            categoryRepository.delete(existingCategory.get())
            return ResponseEntity.ok("Category deleted successfully")
        }
        return ResponseEntity.notFound().build()
    }
}

data class CategoryTreeNode(
    var id: String? = null,
    var name: String? = null,
    var imagePath: String? = null,
    var children: List<CategoryTreeNode>? = null
)