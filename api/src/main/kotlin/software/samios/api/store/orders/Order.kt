package software.samios.api.store.orders

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import software.samios.api.store.customers.Address

@Document(collection = "orders")
data class Order(
    @Id val id: String? = null,
    val customerId: String? = null,
    val products: List<OrderProduct> = emptyList(),
    val status: OrderStatus = OrderStatus.PENDING,
    val total: Double = 0.0,
    val shippingAddress: Address? = null,
    val billingAddress: Address? = null,
    val paymentMethod: String? = null,
    val paymentIntentId: String? = null,
    val paymentStatus: String? = null,
    val trackingNumber: String? = null,
    val couponCode: String? = null,
    val discount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class OrderProduct(
    val productId: String? = null,
    val quantity: Int = 0,
    val price: Double = 0.0,
    val name: String? = null,
    val brand: String? = null,
    val sku: String? = null,
    val image: String? = null,
)

enum class OrderStatus {
    PENDING,
    PROCESSING,
    ON_HOLD,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED,
    COMPLETED;
}
