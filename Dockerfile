# 1. 빌드 환경 (Java 21 & Gradle 8.5)
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
# 테스트는 건너뛰고 빌드만 실행해서 jar 파일 생성
RUN gradle clean build -x test

# 2. 실행 환경 (가벼운 JRE 21 버전)
FROM eclipse-temurin:21-jre
WORKDIR /app
# 빌드된 jar 파일을 복사
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar
# 컨테이너가 켜질 때 실행할 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]