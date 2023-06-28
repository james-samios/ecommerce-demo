package software.samios.api.store.products

import org.springframework.cache.annotation.Cacheable
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    companion object {
        const val productCacheKey = "products"
    }

    @GetMapping
    @Cacheable(value = [productCacheKey])
    fun getProducts(): List<Product> {
        return productService.getProducts()
    }


}