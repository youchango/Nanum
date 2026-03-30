# 1. 안정적인 JDK 21 이미지 사용
FROM eclipse-temurin:21-jdk-alpine

# 2. 작업 디렉토리
WORKDIR /app

# 3. 빌드된 JAR 파일 복사
# (주의) 프로젝트 구조에 따라 build/libs/ 아래 jar 파일이 여러 개일 수 있으니
# 확실하게 plain이 붙지 않은 실행 가능한 jar만 복사합니다.
COPY build/libs/*.jar app.jar

# 4. 파일 업로드 폴더 생성
RUN mkdir -p /app/upload

# 5. 실행 (타임존 설정 포함)
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "app.jar"]