plugins {
    id(Plugin.KOTLIN_JVM) version PluginVersions.KOTLIN_VERSION
    id(Plugin.KOTLIN_SPRING) version PluginVersions.KOTLIN_VERSION
    id(Plugin.KOTLIN_KAPT) version PluginVersions.KOTLIN_VERSION
    id(Plugin.JPA_PLUGIN) version PluginVersions.JPA_PLUGIN_VERSION
    id(Plugin.SPRING_BOOT) version PluginVersions.SPRING_BOOT_VERSION
    id(Plugin.SPRING_DEPENDENCY_MANAGEMENT) version PluginVersions.SPRING_DEPENDENCY_MANAGEMENT_VERSION
    id(Plugin.KTLINT) version PluginVersions.KTLINT_VERSION
}

group = "mozu"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // 스프링 부트 기본 기능
    implementation(Dependencies.SPRING_BOOT_STARTER)

    // 코틀린 리플렉션
    implementation(Dependencies.KOTLIN_REFLECT)

    // 스프링 부트 테스트 도구
    testImplementation(Dependencies.SPRING_BOOT_STARTER_TEST)

    // 코틀린 + JUnit5 테스트
    testImplementation(Dependencies.KOTLIN_TEST_JUNIT5)

    // JUnit5 실행 런처
    testRuntimeOnly(Dependencies.JUNIT_PLATFORM_LAUNCHER)

    // 웹 관련
    implementation(Dependencies.SPRING_BOOT_STARTER_WEB)

    // 데이터베이스
    implementation(Dependencies.SPRING_BOOT_STARTER_DATA_JPA)
    implementation(Dependencies.SPRING_BOOT_STARTER_DATA_REDIS)
    runtimeOnly(Dependencies.MYSQL_CONNECTOR)

    // 보안
    implementation(Dependencies.SPRING_BOOT_STARTER_SECURITY)

    // 검증
    implementation(Dependencies.SPRING_BOOT_STARTER_VALIDATION)

    // JSON 처리
    implementation(Dependencies.JACKSON_MODULE_KOTLIN)

    // JWT
    implementation(Dependencies.JWT_API)
    implementation(Dependencies.JWT_IMPL)
    runtimeOnly(Dependencies.JWT_JACKSON)

    // MapStruct
    implementation(Dependencies.MAPSTRUCT)
    kapt(Dependencies.MAPSTRUCT_PROCESSOR)

    // AWS S3
    implementation(Dependencies.AWS_S3)

    // Querydsl
    implementation(Dependencies.QUERYDSL_JPA)
    kapt(Dependencies.QUERYDSL_APT)
    kapt(Dependencies.JAKARTA_PERSISTENCE_API)
    kapt(Dependencies.JAKARTA_ANNOTATION_API)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kapt {
    correctErrorTypes = true
}

sourceSets {
    named("main") {
        java.srcDirs("build/generated/source/kapt/main")
    }
}
