package team.mozu.dsm.adapter.out.s3

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import team.mozu.dsm.application.port.out.s3.S3Port
import team.mozu.dsm.global.exception.s3.FailedDeleteException
import team.mozu.dsm.global.exception.s3.FailedUploadException
import team.mozu.dsm.global.exception.s3.ImageNotFoundException
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.UUID

@Component
class S3Adapter(
    private val s3Client: S3Client,
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String,
    @Value("\${cloud.aws.s3.url-prefix}") private val urlPrefix: String
) : S3Port {

    companion object {
        private val ALLOWED_EXTENSIONS: Set<String> = setOf("jpg", "jpeg", "png", "webp")
    }

    override fun upload(file: MultipartFile): String {
        val fileName = file.originalFilename ?: throw ImageNotFoundException
        validateExtension(fileName)

        val key = "${UUID.randomUUID()}.${getExtension(fileName)}"

        try {
            val putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.contentType)
                .contentLength(file.size)
                .build()

            file.inputStream.use { input ->
                s3Client.putObject(putReq, RequestBody.fromInputStream(input, file.size))
            }

            return urlPrefix.removeSuffix("/") + "/$key"
        } catch (e: Exception) {
            throw FailedUploadException
        }
    }

    override fun delete(s3Url: String) {
        try {
            val uri = URI(s3Url)
            val decodedPath = URLDecoder.decode(uri.path, StandardCharsets.UTF_8)
            val key = decodedPath.removePrefix("/")

            val delReq = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()

            s3Client.deleteObject(delReq)
        } catch (e: Exception) {
            throw FailedDeleteException
        }
    }

    private fun validateExtension(fileName: String) {
        val ext = getExtension(fileName)
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw FailedUploadException
        }
    }

    private fun getExtension(fileName: String): String =
        fileName.substringAfterLast('.', missingDelimiterValue = "").lowercase()
}
