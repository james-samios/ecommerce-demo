package software.samios.api.utility

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class PhotosService : WebMvcConfigurer {

    /**
     * Allows the /photos directory to be accessed from the browser.
     * We do not currently store any sensitive photos in this directory.
     * If we do in the future, authentication will be implemented.
     */
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/photos/**")
            .addResourceLocations("file:/photos/")
    }
}