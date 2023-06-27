package software.samios.api.store.products.categories

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Paths

@Service
class CategoryService(private val categoryRepository: CategoryRepository) {

    fun saveCategoryWithImage(category: Category, file: MultipartFile) {
        val directoryPath = Paths.get("/photos/${category.id}").toString()
        val directory = File(directoryPath)

        if (!directory.exists()) {
            directory.mkdirs()  // ensure the directory exists
        }

        val filePath = Paths.get(directoryPath, file.originalFilename ?: "default.jpg")
        val fileOnDisk = File(filePath.toString())
        file.transferTo(fileOnDisk)
        category.imagePath = file.originalFilename ?: "default.jpg"
        categoryRepository.save(category)
    }
}