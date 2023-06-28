package software.samios.api.store.products

import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    fun getProducts(): List<Product> {
        return productRepository.findAll().map { it.toCachedProduct() }
    }
}