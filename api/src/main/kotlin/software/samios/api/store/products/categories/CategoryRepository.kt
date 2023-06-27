package software.samios.api.store.products.categories

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : MongoRepository<Category, String>