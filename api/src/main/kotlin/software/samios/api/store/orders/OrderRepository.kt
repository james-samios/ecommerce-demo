package software.samios.api.store.orders

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : MongoRepository<Order, String> {

    fun findByCustomerId(customerId: String): List<Order>
}