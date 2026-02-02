# 🤖 Loki Mode 전문 스킬(Skills) 상세 가이드

Loki Mode는 단순한 코딩 보조를 넘어, **100개 이상의 전문화된 스킬(Skills)**을 지능적으로 결합하여 최적의 아키텍처와 비즈니스 로직을 설계하는 고도화된 에이전트 모드입니다.

---

## 1. 스킬 카테고리별 역할 분담

Loki Mode의 스킬은 크게 5가지 핵심 도메인으로 분류되어 상호작용합니다.

### 🏗️ Architecture (설계 지능)
시스템의 뼈대를 구성하며, 기술 부채를 최소화하고 확장성을 극대화합니다.
- **Modern Web App Pattern**: 최신 웹 프레임워크의 베스트 프랙티스 적용.
- **Microservices Orchestration**: 서비스 간의 느슨한 결합(Loose Coupling) 설계.
- **Clean Architecture (Java/Spring)**: 계층화된 설계를 통해 유지보수성 확보.

### 💼 Business (비즈니스 전략)
사용자의 요구사항을 기술적인 명세로 정교하게 번역합니다.
- **Product Requirements Document (PRD)**: 비즈니스 비전과 사용자 가치를 정의.
- **User Story Mapping**: 사용자 경험 중심의 흐름 설계.
- **Market Standard Analysis**: 시장 경쟁력이 있는 표준 기능 구성.

### 📊 Data & AI (데이터 지형도)
데이터의 무결성과 성능을 보장하며 분석적 통찰을 제공합니다.
- **SQL Optimization Patterns**: 인덱싱, 쿼리 튜닝 등 성능 최적화.
- **Data Integrity Audit**: 데이터 간의 정합성 및 감사(Audit) 기록 설계.
- **Information Architecture**: 복잡한 데이터 관계를 ERD 등으로 시각화.

### 🛡️ Security & SRE (안정성 가드레일)
시스템의 보안 위협을 탐지하고 무중단 운영을 지향합니다.
- **Static Analysis (SAST)**: 코드 레벨의 보안 취약점 사전 차단.
- **Authentication Patterns (JWT/OAuth2)**: 표준화된 인증 메커니즘 구축.
- **Observability**: 로그, 메트릭 추적을 통한 문제 원인 파악.

---

## 2. Loki Mode의 스킬 활용 메커니즘

로키 모드가 이 스킬들을 사용하는 방식은 다음의 **4단계 프로세스**를 따릅니다.

1.  **지능적 선택 (Orchestration)**: 사용자의 요청(예: "쇼핑몰 구축")이 들어오면, 관련도가 높은 스킬(PRD 작성, Clean Architecture 등)들을 에이전트 그룹에 할당합니다.
2.  **가상 시뮬레이션 (Simulation)**: 작성된 코드나 설계안을 `sql-optimization`이나 `security-review` 스킬 에이전트에게 보내어 잠재적 결함을 사전 체크합니다.
3.  **동기화 (Synchronization)**: PRD(문서) - SQL(DB) - Java(코드)가 서로 일치하는지 `sync-checker` 스킬이 실시간으로 감시합니다.
4.  **피드백 루프 (Feedback Loop)**: 사용자의 수정 요청(예: "Biz는 기업회원이야")이 들어오면 연관된 모든 스킬을 재가동하여 전체 시스템의 일관성을 유지합니다.

---

## 3. 핵심 스킬 활용 예시 (현재 프로젝트 적용)

| 사용된 스킬 ID | 적용 내용 | 효과 |
| :--- | :--- | :--- |
| `business-prd-standard` | `PRD_ShoppingMall.md` 작성 | 비즈니스 로직의 명확화 |
| `data-db-architect` | `product_biz_mapping.sql` 설계 | 기업 전용 혜택 데이터 정합성 확보 |
| `dev-clean-java-springboot` | 기존 컨트롤러 및 서비스 분석 | 코드 재사용성 극대화 및 계층 분리 |
| `security-auth-patterns` | JWT 인증 연동 검토 | 안전한 기업/개인 회원 분리 |

---

> [!IMPORTANT]
> Loki Mode의 스킬은 정적이지 않습니다. 프로젝트가 진행됨에 따라 에이전트들은 사용자의 의도를 학습하여 각 스킬의 적용 강도를 조절하는 **"적응형 엔지니어링(Adaptive Engineering)"**을 수행합니다.
