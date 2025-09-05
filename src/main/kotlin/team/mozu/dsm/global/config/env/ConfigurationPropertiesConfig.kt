package team.mozu.dsm.global.config.env

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationPropertiesScan(basePackages = ["team.mozu.dsm"])
class ConfigurationPropertiesConfig
