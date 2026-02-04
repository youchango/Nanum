# Nanum API Specification

본 문서는 `Nanum` 프로젝트의 Spring Boot Application에서 제공하는 REST API 명세서입니다.

## 1. Auth (인증/인가)
Base URL: `/api/v1/auth`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/signup` | 회원가입 | 신규 회원을 등록합니다. |
| `POST` | `/login` | 로그인 | Login ID/PW로 인증하여 Access/Refresh Token을 발급합니다. (Cookie) |
| `POST` | `/refresh` | 토큰 갱신 | Refresh Token을 사용하여 Access Token을 재발급합니다. |

## 2. Member (회원)

### 2.1 Admin (관리자용)
Base URL: `/api/v1/admin/members`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 회원 목록 조회 | 검색 조건(SearchDTO)에 맞는 회원 목록을 조회합니다. |
| `POST` | `/` | 회원 생성 | 관리자가 회원을 직접 생성합니다. (Master, Biz, User) |
| `GET` | `/{memberCode}` | 회원 상세 조회 | Member Code로 회원 상세 정보를 조회합니다. |
| `PUT` | `/{memberCode}` | 회원 정보 수정 | 회원 정보를 수정합니다. |

### 2.2 User (일반용)
Base URL: `/api/v1/members`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/check-id` | 아이디 중복 확인 | 회원가입 시 아이디 중복 여부를 확인합니다. (`true`: 중복, `false`: 사용가능) |

## 3. System Code (공통 코드)
Base URL: `/api/v1/admin/codes`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 코드 목록 조회 | 검색 조건에 맞는 공통 코드 목록을 조회합니다. |
| `POST` | `/` | 코드 등록 | 새로운 공통 코드를 등록합니다. |
| `GET` | `/upper` | 상위 코드 목록 조회 | 코드 등록/수정 시 참조할 상위 코드를 조회합니다. |
| `GET` | `/{codeId}` | 코드 상세 조회 | ID로 코드 상세 정보를 조회합니다. |
| `PUT` | `/{codeId}` | 코드 수정 | 코드를 수정합니다. |
| `DELETE` | `/{codeId}` | 코드 삭제 | 코드를 삭제합니다. |
| `GET` | `/api/types/{codeType}` | 코드 타입별 조회 | 특정 Type의 코드 목록을 반환합니다. |

## 4. Product (상품)

### 4.1 Admin Category (카테고리 관리)
Base URL: `/api/v1/admin/category`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 전체 카테고리 조회 | 모든 카테고리를 계층형(Tree) 구조로 조회합니다. |
| `POST` | `/` | 카테고리 생성 | 신규 카테고리를 생성합니다. |
| `PUT` | `/{id}` | 카테고리 수정 | 카테고리 정보를 수정합니다. |
| `DELETE` | `/{id}` | 카테고리 삭제 | 카테고리를 삭제합니다. (하위 카테고리 존재 시 불가) |
| `PATCH` | `/{id}/use-yn` | 사용 여부 변경 | 카테고리의 사용 여부(Y/N)를 토글합니다. |

### 4.2 Admin Product (상품 관리)
Base URL: `/api/v1/admin/products`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | 상품 생성 | 신규 상품을 등록합니다. |
| `PUT` | `/{id}` | 상품 수정 | 상품 정보를 수정합니다. |
| `DELETE` | `/{id}` | 상품 삭제 | 상품을 삭제합니다. |

### 4.3 User Product (쇼핑몰 상품)
Base URL: `/api/products` (Note: V1 prefix 미적용)

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 상품 목록 조회 | 카테고리 필터링 등 상품 목록을 조회합니다. |
| `GET` | `/{id}` | 상품 상세 조회 | 상품 상세 정보를 조회합니다. |

## 5. Order (주문)

### 5.1 Admin Order (주문 관리)
Base URL: `/api/v1/admin/orders`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 전체 주문 목록 | 관리자용 전체 주문 목록을 조회합니다. |
| `PATCH` | `/{id}/status` | 주문 상태 변경 | 주문의 상태(결제완료, 배송중 등)를 변경합니다. |

### 5.2 User Order (주문 하기)
Base URL: `/api/v1/orders`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | 주문 생성 | 상품을 주문합니다. |
| `GET` | `/{id}` | 주문 상세 조회 | 본인의 주문 내역을 상세 조회합니다. |

## 6. Delivery (배송)
Base URL: `/api/deliveries`
- *Current Implementation Empty*
