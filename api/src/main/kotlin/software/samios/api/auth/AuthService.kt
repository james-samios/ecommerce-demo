package software.samios.api.auth

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import software.samios.api.user.StaffAccount
import software.samios.api.admin.staff.StaffAccountRepository
import software.samios.api.store.customers.CustomerAccountRepository
import software.samios.api.user.CustomerAccount

@Service
class AuthService(
    private val staffAccountRepository: StaffAccountRepository,
    private val customerAccountRepository: CustomerAccountRepository,
    private val passwordEncoder: PasswordEncoder) {

    fun authenticateStaffAccount(email: String, password: String): StaffAccount? {
        val user = staffAccountRepository.findByEmail(email)
        return if (user != null && user.verifyPassword(password, passwordEncoder)) user else null
    }

    fun authenticateCustomerAccount(email: String, password: String): CustomerAccount? {
        val user = customerAccountRepository.findByEmail(email)
        return if (user != null && user.verifyPassword(password, passwordEncoder)) user else null
    }
}