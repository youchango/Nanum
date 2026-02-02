# CTSO (C-Project Construction Technical Service Operation)

## 🛠️ 개발 환경 (Environment)
- **Java**: 21 (LTS)
- **Framework**: Spring Boot 3.x
- **DB**: MariaDB 10.6+
- **Build**: Gradle
- **Container**: Docker & Docker Compose (WAR Deployment)

## 🚀 실행 방법 (How to Run)

### 1. 사전 준비 (Prerequisites)
- Docker & Docker Compose 설치
- Java 21 SDK (로컬 실행 시)

### 2. Docker로 실행 (권장)
웹 애플리케이션(WAS)과 데이터베이스(DB)를 모두 컨테이너로 실행합니다.
```bash
# 빌드 및 실행 (백그라운드 모드)
docker compose -f docker-compose.dev.yml up --build -d

# 중지
docker compose -f docker-compose.dev.yml down

# 로그 확인
docker logs -f ctso-app-1
```
- **접속 주소**: `http://localhost:8080`
- **DB 포트**: `33306` (Host Mapping)

### 3. 로컬 실행 (DB만 Docker 사용)
IDE에서 Java 앱을 실행하고 싶을 때 사용합니다.

1. DB만 실행:
   ```bash
   docker compose -f docker-compose.dev.yml up db -d
   ```
1-1. 앱만 실행:
   ```bash
   docker compose -f docker-compose.dev.yml up app -d
   ```
2. Spring Boot 앱 실행:
   ```bash
   ./gradlew bootRun
   ```

### 4. 환경 설정 (Configuration)
`src/main/resources/`의 YAML 파일로 각 환경을 설정합니다.
- `application-local.yml`: 로컬 개발 (IDE 실행 시 사용)
- `application-dev.yml`: 개발 서버 (Docker 실행 시 사용)
- `application-prod.yml`: 운영 서버

**환경 변경 방법**:
`docker-compose.dev.yml`의 `SPRING_PROFILES_ACTIVE` 값을 변경하거나 Java 실행 시 인자를 전달합니다.
```bash
java -jar app.war --spring.profiles.active=prod
```


## 📂 프로젝트 구조 (Structure)

- `src/main/java`: 백엔드 코드 (Controller, Service, Mapper)
- `src/main/webapp`: 프론트엔드 (JSP)
- `Document/`: 기획 명세서, DB 스키마, 산출물

## 💾 데이터 영속성 (Persistence)
로컬 DB 데이터는 `./Local/DB` 디렉토리에 저장되어 컨테이너 재시작 시에도 데이터가 유지됩니다.
**주의**: `Local/` 디렉토리는 `.gitignore`에 등록되어 있어 Git에 포함되지 않습니다.
