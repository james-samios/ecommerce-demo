package software.samios.api.data

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import software.samios.api.utility.EnvLoader

/**
 * MongoDB Cloud Configuration
 * If you are using a local MongoDB instance, you will need to add in the port number to the connection string.
 */
@Configuration
class MongoConfig : AbstractMongoClientConfiguration() {

    override fun mongoClient(): MongoClient {
        val host = EnvLoader.getEnvVariable("MONGODB_HOST") ?: "localhost"
        val username = EnvLoader.getEnvVariable("MONGODB_USERNAME")
        val password = EnvLoader.getEnvVariable("MONGODB_PASSWORD")

        val connectionString = "mongodb+srv://$username:$password@$host/${databaseName}?retryWrites=true&w=majority"
        val clientSettings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(connectionString))
            .build()

        return MongoClients.create(clientSettings)
    }

    override fun getDatabaseName(): String {
        return EnvLoader.getEnvVariable("MONGODB_DATABASE") ?: "ecommerce-demo"
    }
}