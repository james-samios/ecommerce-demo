package software.samios.api.auth

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import software.samios.api.admin.staff.StaffAccount
import software.samios.api.admin.staff.StaffAccountRepository

@Service
class AuthService(
    private val staffAccountRepository: StaffAccountRepository,
    private val passwordEncoder: PasswordEncoder) {

    fun authenticate(email: String, password: String): StaffAccount? {
        val user = staffAccountRepository.findByEmail(email)
        return if (user != null && user.verifyPassword(password, passwordEncoder)) user else null
    }
}