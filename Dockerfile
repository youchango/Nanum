# 1. 자바 실행 환경 설정 (프로젝트 자바 버전에 맞춰 17 또는 21 등으로 수정)
FROM openjdk:21-jdk-slim

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 타임존 설정 (로그 시간이 한국 시간으로 나오게 함 - 선택사항)
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 4. 빌드된 JAR 파일 복사
# Jenkins가 'Build Backend' 단계에서 생성한 jar 파일을 컨테이너 내부로 가져옵니다.
COPY build/libs/*.jar app.jar

# 5. 업로드 폴더 생성 (Jenkinsfile의 CONTAINER_UPLOAD_PATH와 동일하게)
# 이 폴더는 실행 시 호스트의 /home/ttcc/nanum/upload와 연결됩니다.
RUN mkdir -p /app/upload

# 6. 포트 노출 (백엔드 기본 8080)
EXPOSE 8080

# 7. 어플리케이션 실행
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "app.jar"]