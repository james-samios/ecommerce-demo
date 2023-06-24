package software.samios.api.admin.staff

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.security.crypto.password.PasswordEncoder

@Document(collection = "staff_accounts")
data class StaffAccount(

    @Id val id: String,
    @Field("first_name") val firstName: String,
    @Field("last_name") val lastName: String,
    @Field("email") val email: String,
    @Field("email_verified") val emailVerified: Boolean = false,
    @Field("password") @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) val password: String, // Hashed password
    @Field("access") val access: StaffAccess = StaffAccess.GUEST,
    @Field("last_login") val lastLogin: Long = System.currentTimeMillis()

) {
    fun verifyPassword(passwordToCheck: String, passwordEncoder: PasswordEncoder): Boolean {
        return passwordEncoder.matches(passwordToCheck, password)
    }
}

enum class StaffAccess {
    SUPER_ADMIN,
    ADMIN,
    STAFF,
    GUEST;

    fun hasAccessTo(access: StaffAccess): Boolean {
        return this.ordinal <= access.ordinal
    }
}