package software.samios.api.store.customers

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import software.samios.api.user.CustomerAccount

@Repository
interface CustomerAccountRepository : MongoRepository<CustomerAccount, String> {
    fun findByEmail(email: String): CustomerAccount?
}