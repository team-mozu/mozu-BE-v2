package team.mozu.dsm.application.port.out.s3

import org.springframework.web.multipart.MultipartFile

interface S3Port {

    fun upload(file: MultipartFile): String

    fun delete(s3Url: String)
}
