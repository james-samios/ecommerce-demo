package software.samios.api.store.products

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : MongoRepository<Product, String> {
    fun findByEnabled(enabled: Boolean): List<Product>

    fun findByCategoryIdInAndEnabled(categoryIds: List<String>, enabled: Boolean): List<Product>

    fun findByCategoryIdAndEnabled(categoryId: String, enabled: Boolean): List<Product>
}