package software.samios.api.store.products.categories

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import software.samios.api.utility.URLUtility

@Document(collection = "categories")
data class Category(

    @Id
    var id: String? = null,
    var name: String? = null,
    var parentId: String? = null,
    var imagePath: String? = null
) {
    fun getImageUrl(): String? {
        return if (imagePath.isNullOrEmpty()) {
            null
        } else {
            "${URLUtility.getCurrentUrl()}/photos/category/$id/$imagePath"
        }
    }
}