package team.mozu.dsm.global.config.s3

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region

@Configuration
class S3Config(
    @Value("\${cloud.aws.credentials.accessKey}") private val accessKey: String,
    @Value("\${cloud.aws.credentials.secretKey}") private val secretKey: String,
    @Value("\${cloud.aws.region.static}") private val region: String
) {

    @Bean
    fun s3Client(): S3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
        )
        .build()
}
