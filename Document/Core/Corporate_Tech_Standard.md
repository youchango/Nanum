# 🏢 Corporate Technical Standard (전사 기술 표준)

본 문서는 회사 내 시스템 구축 시 준수해야 할 핵심 기술 표준을 정의한다. 모든 신규 프로젝트는 본 표준을 기반으로 설계 및 구현되어야 한다.

---

## 1. 🛠️ 기술 스택 표준 (Tech Stack)

### 1.1 Backend
| 구분 | 표준 기술 | 버전 / 비고 |
| :--- | :--- | :--- |
| **Language** | Java | **21 (LTS)** |
| **Framework** | Spring Boot | 3.2 이상 |
| **Build Tool** | Gradle | Kotlin DSL (권장) or Groovy |
| **ORM** | **Spring Data JPA** | Hibernate 6.x |
| **SQL Mapper** | **MyBatis** | 3.5.x (복잡한 조회 쿼리용) |
| **DB** | MariaDB | **11.4** |

### 1.2 Frontend (Web)
| 구분 | 표준 기술 | 버전 / 비고 |
| :--- | :--- | :--- |
| **Framework** | **React** | 18.x (Vite Bundler recommended) |
| **Language** | TypeScript / JavaScript | ES6+ |
| **State Mngt** | Recoil / Zustand | Global State Management |
| **Styling** | TailwindCSS / Styled-Components | Component Styling |

---

## 2. 🔐 보안 및 인증 표준 (Security & Auth)

### 2.1 인증 (Authentication)
- **Standard**: **JWT (Json Web Token)** 기반의 Stateless 인증.
- **Login Flow**: API Login 요청 -> 성공 시 **Access Token** & **Refresh Token** 발급.

### 2.2 토큰 보관 및 전송 (Storage & Transmission)
- **Access Token**: **HttpOnly Cookie** 권장 (XSS 보호).
- **Refresh Token**: **HttpOnly, Secure Cookie** 필수 (XSS 보호).
- **Transmission**: API 요청 시 쿠키 자동 전송 방식 지향.

### 2.3 권한 (Authorization)
- **RBAC (Role Based Access Control)** 적용.
- **Config**: Spring Security를 통한 URL 패턴별 권한 제어.

---

## 3. 📜 로그 관리 정책 (Log Management Policy)

### 3.1 로그 구조 및 레벨
- **Structure**: 도메인(Package) 단위로 로그 파일 분리 (`logs/system`, `logs/member` 등).
- **Level Strategy**:
    - **DEV**: `DEBUG`
    - **PROD**: `INFO` (StackTrace 포함 `ERROR` 필수)

### 3.2 보관 및 아카이빙 (Retention & Archiving)
- **Local Retention**: 30일 보관 후 자동 삭제 (매일 자정 Rolling).
- **Archiving Target**: **Backup Server (IP: ...238)**
- **Policy**: 30일 경과 로그는 삭제 전 백업 서버로 이관(rsync/scp).
- **Archiving Period**: 백업 서버에서 **최소 1년** 보관.
