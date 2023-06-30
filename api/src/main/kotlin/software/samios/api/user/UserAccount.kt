package software.samios.api.user

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Transient
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Base object for User Accounts.
 * This class is inherited by StaffAccount and CustomerAccount.
 * This class is only used for internal UserDetails Implementation.
 * It only has the basic needed fields such as email and password.
 */
open class UserAccount(
    @JsonIgnore @Transient var userEmail: String,
    @JsonIgnore @Transient private var userPassword: String, // Hashed password
    @JsonIgnore @Transient var userAccountActive: Boolean = false,
    var accountType: AccountType = AccountType.CUSTOMER
) {
    @JsonIgnore
    fun verifyPassword(passwordToCheck: String, passwordEncoder: PasswordEncoder): Boolean {
        return passwordEncoder.matches(passwordToCheck, userPassword)
    }
}

enum class AccountType {
    STAFF,
    CUSTOMER
}