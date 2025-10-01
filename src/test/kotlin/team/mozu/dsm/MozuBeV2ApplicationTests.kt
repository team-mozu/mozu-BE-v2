package team.mozu.dsm

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.context.annotation.Import
import team.mozu.dsm.config.TestRedisConfig

@ActiveProfiles("test")
@SpringBootTest
@Import(TestRedisConfig::class)
class MozuBeV2ApplicationTests {

    @Test
    fun contextLoads() {
    }
}
