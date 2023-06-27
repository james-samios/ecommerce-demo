package software.samios.api.user

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Document(collection = "staff_accounts")
data class StaffAccount(
    @Id val id: String,
    @Field("first_name") val firstName: String,
    @Field("last_name") val lastName: String,
    @Field("email") val email: String,
    @Field("email_verified") val emailVerified: Boolean = false,
    @Field("password") @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) val password: String,
    @Field("access") val access: StaffAccess = StaffAccess.GUEST,
    @Field("last_login") val lastLogin: Long = System.currentTimeMillis(),
    @Field("ip_address") val ipAddress: String = "",
    @Field("account_active") val accountActive: Boolean = false

): UserAccount(
    userEmail = email,
    userPassword = password,
    userAccountActive = accountActive,
    accountType = AccountType.STAFF)

enum class StaffAccess {
    SUPER_ADMIN,
    ADMIN,
    STAFF,
    GUEST,
    ROLE_ANONYMOUS; // Anonymous request (no token), we reject all of these...

    fun hasAccessTo(access: StaffAccess): Boolean {
        return this.ordinal <= access.ordinal
    }
}

@Component("staffAuth")
class StaffAccountAuth {
    fun hasRole(auth: Authentication, role: StaffAccess): Boolean {
        val firstRole = auth.authorities.firstOrNull()?.authority
        return firstRole != null && StaffAccess.valueOf(firstRole).hasAccessTo(role)
    }
}