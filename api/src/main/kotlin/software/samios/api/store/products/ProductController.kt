package software.samios.api.store.products

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    companion object {
        const val productCacheKey = "products"
    }

    /**
     * GET /api/products
     * Get all products or products within a specific category.
     * @param categoryId (Optional) The ID of the category to filter products.
     * @param enabled (Optional) Whether to filter products by enabled status. Default true.
     * @param includeSubcategories (Optional) Whether to include products from subcategories. Default true.
     * @return List of products.
     *
     * Caching is done in the ProductService class.
     */
    @GetMapping
    fun getProducts(
        @RequestParam(required = false) categoryId: String?,
        @RequestParam(required = false, defaultValue = "true") enabled: Boolean,
        @RequestParam(required = false, defaultValue = "true") includeSubcategories: Boolean
    ): List<Product> {
        return if (categoryId != null) {
            if (includeSubcategories) {
                productService.getProductsByCategoriesAndSubCategories(categoryId, enabled)
            } else {
                productService.getProductsByCategory(categoryId, enabled)
            }
        } else {
            productService.getProducts(enabled)
        }
    }

    /**
     * GET /api/products/{id}
     * Get a product by ID.
     * @param id The ID of the product.
     * @return The product (if found)
     *
     * This method will search both enabled and disabled products.
     */
    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: String): ResponseEntity<Product> {
        val product = productService.getProductById(id)
        return if (product != null) {
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}