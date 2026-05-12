package team.mozu.dsm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class MozuBeV2Application

fun main(args: Array<String>) {
    runApplication<MozuBeV2Application>(*args)
}
