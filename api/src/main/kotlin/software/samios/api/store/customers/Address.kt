package software.samios.api.store.customers

data class Address(
    val name: String,
    val street: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String
)