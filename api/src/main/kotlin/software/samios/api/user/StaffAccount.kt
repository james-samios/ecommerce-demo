package software.samios.api.user

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Document(collection = "staffAccounts")
data class StaffAccount(
    @Id val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val emailVerified: Boolean = false,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) val password: String,
    val access: StaffAccess = StaffAccess.GUEST,
    val lastLogin: Long = System.currentTimeMillis(),
    val ipAddress: String = "",
    val accountActive: Boolean = false

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