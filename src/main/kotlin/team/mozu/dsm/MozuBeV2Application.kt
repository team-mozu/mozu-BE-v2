package team.mozu.dsm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class MozuBeV2Application

fun main(args: Array<String>) {
    runApplication<MozuBeV2Application>(*args)
}
