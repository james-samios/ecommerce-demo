package software.samios.api.store.products.categories

import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.types.ObjectId
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/category")
class CategoryController(
    private val categoryService: CategoryService,
    private val categoryRepository: CategoryRepository
) {

    companion object {
        const val categoryTreeCacheKey = "categories"
    }

    /**
     * GET /api/category
     * @return List of categories in a tree structure.
     * All child categories will be parsed under their parent.
     */
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


    /**
     * Responsible for building a category tree.
     * @param parentNode The parent node of a category.
     * @param categoryMap A map of categories with the ID as their key.
     */
    private fun buildCategoryTree(parentNode: CategoryTreeNode, categoryMap: Map<String, Category>) {
        val childCategories = categoryMap.filterValues { it.parentId == parentNode.id }
            .map { CategoryTreeNode(it.value.id!!, it.value.name, it.value.getImageUrl(), emptyList()) }

        for (child in childCategories) {
            buildCategoryTree(child, categoryMap)
        }
        parentNode.children = childCategories
    }

    /**
     * POST /api/category
     * Create a category.
     * @param categoryString The Category object to create (send as a JSON String)
     * @param file The image file associated with the category.
     */
    @CacheEvict(value = [categoryTreeCacheKey], allEntries = true)
    @PostMapping(consumes = ["multipart/form-data"])
    @PreAuthorize("@staffAuth.hasRole(authentication, T(software.samios.api.user.StaffAccess).STAFF)")
    fun createCategory(
        @RequestPart("category") categoryString: String,
        @RequestPart("file", required = false) file: MultipartFile?
    ): ResponseEntity<String> {
        // Parse category JSON
        val objectMapper = ObjectMapper()
        val category = objectMapper.readValue(categoryString, Category::class.java)

        if (category.id != null) {
            return ResponseEntity.badRequest().body("Cannot specify ID for a new category")
        }

        // We need to create the ID here in order to save the photo.
        category.id = ObjectId().toString()

        if (file != null) {
            categoryService.saveCategoryWithImage(category, file)
        } else {
            // Set a default image path if no file is provided
            category.imagePath = "default.jpg"
            categoryRepository.save(category)
        }
        return ResponseEntity.ok("Category created successfully")
    }

    /**
     * PUT /api/category/id
     * Update a category.
     * @param id The ID of the category to be updated.
     * @param updatedCategory The updated category object.
     */
    @CachePut(value = [categoryTreeCacheKey], key = "#id")
    @PutMapping("/{id}")
    @PreAuthorize("@staffAuth.hasRole(authentication, T(software.samios.api.user.StaffAccess).STAFF)")
    fun updateCategory(
        @PathVariable id: String,
        @RequestBody updatedCategory: Category,
    ): ResponseEntity<String> {
        val existingCategory = categoryRepository.findById(id)
        if (existingCategory.isPresent) {
            val categoryToUpdate = existingCategory.get()

            // Update category properties
            categoryToUpdate.name = updatedCategory.name

            if (updatedCategory.parentId != null) {
                val parentCategory = categoryRepository.findById(updatedCategory.parentId!!)
                if (parentCategory.isPresent) {
                    categoryToUpdate.parentId = parentCategory.get().id
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

    /**
     * DELETE /api/category/id
     * Delete a category.
     * @param id The ID of the category to be deleted.
     */
    @CacheEvict(value = [categoryTreeCacheKey], key = "#id")
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

/**
 * Category tree node structure for GET /api/categories
 */
data class CategoryTreeNode(
    var id: String? = null,
    var name: String? = null,
    var imagePath: String? = null,
    var children: List<CategoryTreeNode>? = null
)