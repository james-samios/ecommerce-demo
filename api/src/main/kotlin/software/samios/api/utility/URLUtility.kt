package software.samios.api.utility

import org.springframework.web.servlet.support.ServletUriComponentsBuilder

class URLUtility {

    companion object {
        fun getCurrentUrl(): String {
            val servletUriComponentsBuilder = ServletUriComponentsBuilder.fromCurrentContextPath()
            return servletUriComponentsBuilder.build().toUriString()
        }
    }
}