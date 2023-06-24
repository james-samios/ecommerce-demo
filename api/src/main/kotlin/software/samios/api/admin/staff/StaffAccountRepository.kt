package software.samios.api.admin.staff

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface StaffAccountRepository : MongoRepository<StaffAccount, String> {
    fun findByEmail(email: String): StaffAccount?
}