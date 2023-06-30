package software.samios.api.store.customers

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import software.samios.api.store.orders.Order
import software.samios.api.store.orders.OrderRepository
import software.samios.api.user.CustomerAccount

/**
 * Customer Controller
 * This controller is for customers and not admins/staff.
 * Customer backend administration is handled by the admin module.
 */
@RestController
@RequestMapping("/api/customer")
class CustomerController(
    private val customerRepository: CustomerAccountRepository,
    private val orderRepository: OrderRepository
) {

    @GetMapping
    fun getCustomer(): CustomerAccount? {
        return getDetails()
    }

    @GetMapping("/orders")
    fun getCustomerOrders(): List<Order> {
        val customer = getDetails() ?: return emptyList()
        return orderRepository.findByCustomerId(customer.id)
    }

    private fun getDetails(): CustomerAccount? {
        val authentication = SecurityContextHolder.getContext().authentication
        val userDetails = authentication.principal as UserDetails
        return customerRepository.findByEmail(userDetails.username)
    }

}