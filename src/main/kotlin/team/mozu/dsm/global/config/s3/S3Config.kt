package team.mozu.dsm.global.config.s3

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Configuration
class S3Config(
    @Value("\${cloud.aws.credentials.access-key}") private val accessKey: String,
    @Value("\${cloud.aws.credentials.secret-key}") private val secretKey: String,
    @Value("\${cloud.aws.region.static}") private val region: String,
    @Value("\${cloud.aws.s3.endpoint:}") private val endpoint: String
) {

    @Bean
    fun s3Client(): S3Client {
        val creds = AwsBasicCredentials.create(accessKey, secretKey)
        val builder = S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(creds))

        // S3 호환 스토리지(MinIO 등) 사용 시에만 설정. 비어있으면 기존 AWS S3 동작 그대로.
        if (endpoint.isNotBlank()) {
            builder.endpointOverride(URI.create(endpoint)).forcePathStyle(true)
        }

        return builder.build()
    }
}
