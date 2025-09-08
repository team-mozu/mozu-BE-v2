package team.mozu.dsm.global.config

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationPropertiesScan(basePackages = ["team.mozu.dsm"])
class ConfigurationPropertiesConfig
