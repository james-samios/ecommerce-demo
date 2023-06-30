package software.samios.api.user

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import software.samios.api.store.customers.Address

@Document(collection = "customerAccounts")
data class CustomerAccount(
    @Id val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val emailVerified: Boolean = false,
    @JsonIgnore @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) val password: String,
    val joinDate: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis(),
    val ipAddress: String = "",
    @JsonIgnore val accountActive: Boolean = false,
    @JsonIgnore val shippingAddress: Address? = null,
    @JsonIgnore val billingAddress: Address? = null
    // todo: add payment methods

): UserAccount(
    userEmail = email,
    userPassword = password,
    userAccountActive = accountActive,
    accountType = AccountType.CUSTOMER
)