package software.samios.api.store.products.categories

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import software.samios.api.utility.URLUtility

@Document(collection = "categories")
data class Category(

    @Id
    @JsonProperty("id")
    var id: String? = null,

    @JsonProperty("name")
    var name: String,

    @Field("parent_id")
    @JsonProperty("parent_id")
    var parentId: String? = null,

    @Field("image_path")
    @JsonProperty("image_path")
    var imagePath: String? = null
) {
    fun getImageUrl(): String? {
        return if (imagePath.isNullOrEmpty()) {
            null
        } else {
            "${URLUtility.getCurrentUrl()}/photos/$id/$imagePath"
        }
    }
}