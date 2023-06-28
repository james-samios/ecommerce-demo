package software.samios.api.store.products

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import software.samios.api.utility.URLUtility

@Document(collection = "products")
data class Product(

    @Id
    val id: String? = null,
    var name: String? = null,
    var brand: String? = null,
    var description: String? = null,
    var price: Double = 0.0,
    var categoryId: String? = null,
    var images: List<ProductImage> = emptyList(),
    var enabled: Boolean = true,
    var featured: Boolean = false,
    var stock: Int = 0,
    var specifications: List<ProductSpecification> = emptyList(),
    var discount: Double = 0.0,
    var couponCodes: List<String> = emptyList(),
    var customerLimit: Int = 0,
    var sku: String? = null,
) {
    fun toCachedProduct(): Product {
        return this.copy(
            images = this.images.map {
                it.copy(imagePath = it.getImageUrl(this.id!!))
            }
        )
    }
}

data class ProductImage(
    var imagePath: String? = null,
    var primary: Boolean = false
) {
    fun getImageUrl(id: String): String? {
        return if (imagePath.isNullOrEmpty()) {
            null
        } else {
            "${URLUtility.getCurrentUrl()}/photos/product/$id/$imagePath"
        }
    }
}

data class ProductSpecification(
    val identifier: String? = null,
    var displayName: String? = null,
    var value: String? = null
)