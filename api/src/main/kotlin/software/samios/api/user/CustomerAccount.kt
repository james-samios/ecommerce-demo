package software.samios.api.user

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "customerAccounts")
data class CustomerAccount(
    @Id val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val emailVerified: Boolean = false,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) val password: String,
    val joinDate: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis(),
    val ipAddress: String = "",
    val accountActive: Boolean = false,
    // todo: add shipping addresses and payment methods

): UserAccount(
    userEmail = email,
    userPassword = password,
    userAccountActive = accountActive,
    accountType = AccountType.CUSTOMER
)