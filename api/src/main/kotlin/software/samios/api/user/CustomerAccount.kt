package software.samios.api.user

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "customer_accounts")
data class CustomerAccount(
    @Id val id: String,
    @Field("first_name") val firstName: String,
    @Field("last_name") val lastName: String,
    @Field("email") val email: String,
    @Field("email_verified") val emailVerified: Boolean = false,
    @Field("password") @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) val password: String,
    @Field("join_date") val joinDate: Long = System.currentTimeMillis(),
    @Field("last_login") val lastLogin: Long = System.currentTimeMillis(),
    @Field("ip_address") val ipAddress: String = "",
    @Field("account_active") val accountActive: Boolean = false,
    // todo: add shipping addresses and payment methods

): UserAccount(
    userEmail = email,
    userPassword = password,
    userAccountActive = accountActive,
    accountType = AccountType.CUSTOMER
)