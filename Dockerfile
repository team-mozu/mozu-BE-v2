# ==============================
# 1단계: Gradle로 Spring Boot 빌드
# ==============================
FROM gradle:8.5-jdk17 AS builder

# 작업 디렉토리 생성
WORKDIR /app

# Gradle 캐싱을 위해 먼저 build.gradle.kts, settings.gradle.kts, buildSrc 복사
COPY build.gradle.kts settings.gradle.kts ./
COPY buildSrc ./buildSrc

# src 디렉토리 복사
COPY src ./src

# JAR 빌드 (테스트 및 코드 스타일 검사 제외)
RUN gradle clean build -x test -x ktlintMainSourceSetCheck -x ktlintTestSourceSetCheck -x ktlintKotlinScriptCheck


# ==============================
# 2단계: 실행 전용 이미지
# ==============================
FROM eclipse-temurin:17-jre

# 빌드된 JAR 파일 위치
COPY --from=builder /app/build/libs/*.jar /app/mozu-server.jar

WORKDIR /app

# Spring Boot 실행
ENTRYPOINT ["java", "-jar", "/app/mozu-server.jar"]
