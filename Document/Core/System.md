# 🏛️ System Architecture (Global Standard)

## 1. 🎯 아키텍처 개요 (Architectural Overview)
본 문서는 Nanum 통합쇼핑몰의 기술적 토대가 되는 아키텍처를 정의한다. 이 프로젝트는 향후 회사의 **Base Project(C-Project)**로서, 모든 신규 프로젝트의 표준 모델이 되도록 설계한다.

### 1.1 핵심 원칙 (Core Principles)
1.  **MVC Pattern**: Model 2 아키텍처(Spring MVC)를 준수하여 로직과 뷰를 분리한다.
2.  **Scalability (확장성)**: 세션 클러스터링을 통해 Scale-out 환경에서도 정합성을 보장한다.
3.  **AI-Friendly (AI 친화적)**: 명확한 네이밍과 구조를 통해 AI Agent가 코드를 분석하고 생성하기 쉽도록 표준을 준수한다.
4.  **Standardization (표준화)**: 모든 네이밍 및 코드 패턴은 정의된 규칙을 따른다.

---

## 2. 🛠️ 기술 스택 (Tech Stack)

### 2.1 Backend
| 구분 | 기술 | 버전 / 비고 |
| :--- | :--- | :--- |
| **Language** | Java | **21 (LTS)** |
| **Framework** | Spring Boot | 3.2 이상 |
| **Build Tool** | Gradle | Kotlin DSL (권장) or Groovy |
| **ORM** | **Spring Data JPA** | Hibernate 6.x |
| **SQL Mapper** | **MyBatis** | 3.5.x (복잡한 조회 쿼리용) |
| **DB** | MariaDB | **11.4** |


### 2.2 Frontend (Web)
| 구분 | 기술 | 버전 / 비고 |
| :--- | :--- | :--- |
| **Framework** | **React** | 18.x (Vite Bundler recommended) |
| **Language** | TypeScript / JavaScript | ES6+ |
| **State Mngt** | Recoil / Zustand | Global State Management |
| **Styling** | TailwindCSS / Styled-Components | Component Styling |

### 2.3 Infra & DevOps
- **Server**: Linux (Ubuntu 22.04 LTS)
- **Container**: Docker, Docker Compose (로컬 개발용)
- **CI/CD**: Github Actions / Jenkins
- **Web Server**: **Apache HTTP Server** (Reverse Proxy / Static Content)
- **WAS**: Apache Tomcat (Spring Boot Embedded)

---

## 3. 🏗️ 시스템 구성도 (System Landscape)

```mermaid
graph TD
    Browser[Web Browser (React SPA)] -->|REST API (JSON)| Apache[Apache / Nginx]
    Apache -->|Reverse Proxy| Tomcat[Spring Boot WAS]
    
    subgraph "Backend Application"
        Tomcat --> Controller[API Controller]
        Controller --> Service[Service Layer]
        Service --> Repository[JPA Repository]
        Service --> Mapper[MyBatis Mapper]
    end
    
    subgraph "Data Storage"
        Repository --> MainDB[(MariaDB 11.4)]
        Mapper --> MainDB
        Tomcat --> FileStore[File Server / NAS]
    end
```

---

## 4. 📂 패키지 및 폴더 구조 (Package Structure)
표준 Spring Boot (API) + React (Frontend) 구조를 따른다.

```text
src/main/resources/mapper/   # MyBatis XML
└── domain/
    ├── MemberMapper.xml
    └── ...

src/main/java/com/company/project/
├── global/
│   ├── config/             # Security, WebMvc, MyBatis 등 설정
│   ├── error/              # Global Exception Handler
│   ├── util/               # 공통 Utils
│   └── security/           # UserDetails, AuthProvider
│
├── domain/                 # 비즈니스별 패키지 (API 중심)

│   ├── member/
│   │   ├── controller/     # MemberController (API)
│   │   ├── service/
│   │   ├── repository/     # JPA Repository Interface
│   │   ├── mapper/         # MemberMapper Interface (MyBatis)
│   │   └── model/
│   │       ├── entity/     # JPA Entity (@Entity)
│   │       └── dto/        # DTO, VO
│   └── ...
│
└── ProjectApplication.java

# Frontend (Separate Project or Sub-module)
frontend/
├── public/
├── src/
│   ├── assets/
│   ├── components/
│   ├── pages/
│   ├── hooks/
│   ├── services/           # Axios API Calls
│   └── App.jsx
├── package.json
└── vite.config.js
```

---

## 5. 🔐 보안 및 인증 (Security Strategy)

### 5.1 인증 (Authentication)
- **Standard**: **JWT (Json Web Token)**. Stateless 인증 방식을 채택한다.
- **Login Flow**: API Login 요청 -> 성공 시 **Access Token** & **Refresh Token** 발급.
- **Token Storage**:
    - **Access Token**: 보안을 위해 **HttpOnly Cookie**에 저장하는 것을 권장한다. (Authorization Header 사용 시 LocalStorage 저장 주의).
    - **Refresh Token**: 반드시 **HttpOnly, Secure Cookie**에 저장하여 XSS로부터 보호한다.
    - **Transmission**: API 요청 시 쿠키가 자동으로 전송되거나, Frontend에서 쿠키 값을 읽어 Header(`Authorization: Bearer ...`)에 포함(HttpOnly 해제 필요 시). 본 프로젝트는 **All HttpOnly Cookie** 방식을 지향한다.

### 5.2 권한 (Authorization)
- **RBAC (Role Based Access Control)** 적용.
- **Roles**: `ROLE_MASTER`, `ROLE_BIZ`, `ROLE_USER`.
- **Security Config**: URL 패턴별 권한 제어 (`/admin/**`, `/biz/**` 등).
- **Security Config**: URL 패턴별 권한 제어 (`/admin/**`, `/biz/**` 등).
- **Frontend**: React 내에서 Token Decoding(Payload)을 통해 권한별 메뉴/기능 제어.

---

## 6. 💾 데이터 모델링 전략 (Data Modeling)
구체적인 Naming Rule은 `Document/Core/Rules.md`를 참조한다.

### 6.1 핵심 전략 (Hybrid Approach)
- **Command (Create, Update, Delete)**: **JPA**를 사용하여 도메인 객체 중심의 데이터 변경을 처리하고 생산성을 높인다.
- **Query (Read)**:
    - 단순 조회 (PK 조회, 간단한 조건): **JPA (Spring Data JPA)** 사용.
    - 복잡한 조회 (통계, 동적 쿼리, 대량 데이터): **MyBatis**를 사용하여 SQL 최적화.
- 정규화 원칙 준수.

---

## 7. 🌐 인터페이스 전략 (Interface Strategy)

### 7.1 Rest API (Backend)
- 모든 요청은 RESTful API 형식을 따르거나, 규칙에 맞는 JSON 기반 API로 설계한다.
- URL 패턴: `/api/v1/...`
- **Response Format**: `ApiResponse` (Rules.md 참조).

### 7.2 Client (Frontend)
- React Router를 사용하여 SPA(Single Page Application) 라우팅을 처리한다.
- Axios/Fetch를 통해 Backend API와 통신한다.
- 화면은 Backend에서 렌더링하지 않고, JSON 데이터를 받아 클라이언트 브라우저에서 렌더링한다.

---

## 8. 🚀 배포 전략 (Deployment)
- **Containerization**: **Docker**를 사용하여 애플리케이션과 환경을 컨테이너화한다.
- **Orchestration**: `Docker Compose`를 사용하여 Web(Apache), WAS(Tomcat), DB(MariaDB) 컨테이너를 통합 관리한다.
- **Environment**:
    - **Dev**: Local Docker Compose (`docker-compose.dev.yml`)
    - **Prod**: 운영 서버 Docker Deploy (`docker-compose.prod.yml`)

---

## 9. 📜 로그 관리 정책 (Log Management Policy)

### 9.1 도메인별 로그 분리 (Domain-based Logging)
- **Framework**: Logback 또는 Log4j2 (MDC 활용).
- **Structure**: 도메인(Package) 단위로 로그 파일을 분리하여 관리.
    - `logs/system/system.log` (Global/Framework level)
    - `logs/member/member.log`
    - `logs/service/service.log`
    - `logs/billing/billing.log`
- **Level Strategy**:
    - **DEV**: `DEBUG`
    - **PROD**: `INFO` (에러 발생 시 StackTrace 포함 `ERROR` 로깅 필수)

### 9.2 보관 및 아카이빙 (Retention & Archiving)
- **Local Retention**: 30일 (서버 디스크 용량 고려).
- **Deletion Cycle**: 매일 자정(`00:00`)에 30일 경과된 로그 삭제 (Logback RollingPolicy 활용).
- **Archiving**:
    - **Target**: **Backup Server (Ends with .238)**
    - **Policy**: 30일이 지난 로그는 삭제 전 238 서버로 이관(rsync/scp) 후 로컬 삭제.
    - **Archiving Period**: 238 서버에서 최소 1년 보관.
