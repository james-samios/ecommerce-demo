package software.samios.api.store.products

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import software.samios.api.store.products.ProductController.Companion.productCacheKey
import software.samios.api.store.products.categories.Category
import software.samios.api.store.products.categories.CategoryRepository

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) {

    @Cacheable(value = [productCacheKey], key = "'allProducts:' + #enabled")
    fun getProducts(enabled: Boolean = true): List<Product> {
        return productRepository.findByEnabled(enabled).map { it.toCachedProduct() }
    }

    @Cacheable(value = [productCacheKey], key = "'productsByCategory:' + #categoryId + ':enabled:' + #enabled + ':subcategories:false'")
    fun getProductsByCategory(categoryId: String, enabled: Boolean = true): List<Product> {
        return productRepository.findByCategoryIdAndEnabled(categoryId, enabled).map { it.toCachedProduct() }
    }

    @Cacheable(value = [productCacheKey], key = "'productsByCategory:' + #categoryId + ':enabled:' + #enabled + ':subcategories:true'")
    fun getProductsByCategoriesAndSubCategories(categoryId: String, enabled: Boolean = true): List<Product> {
        val category = categoryRepository.findById(categoryId)
        val categoryIds = mutableListOf<String>()

        if (category.isPresent) {
            collectCategoryIds(category.get(), categoryIds)
        }
        val products = productRepository.findByCategoryIdInAndEnabled(categoryIds, enabled)
        return products.map { it.toCachedProduct() }
    }

    fun getProductById(id: String): Product? {
        var product = getProducts(true).find { it.id == id }
        if (product == null) product = getProducts(false).find { it.id == id }
        return product
    }

    private fun collectCategoryIds(category: Category, categoryIds: MutableList<String>) {
        categoryIds.add(category.id!!)

        val subcategories = categoryRepository.findByParentId(category.id!!)
        subcategories.forEach { subcategory ->
            collectCategoryIds(subcategory, categoryIds)
        }
    }
}