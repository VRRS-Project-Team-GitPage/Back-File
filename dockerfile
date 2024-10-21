# 사용할 기본 이미지 설정
FROM openjdk:17-jdk-slim

# 애플리케이션 JAR 파일의 경로를 설정
ARG JAR_FILE=build/libs/VRRS-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]