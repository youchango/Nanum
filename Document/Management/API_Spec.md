# Nanum API Specification

본 문서는 `Nanum` 프로젝트의 Spring Boot Application에서 제공하는 REST API 명세서입니다.

## 1. Dashboard
### 1.1. Summary
- **URL**: `/api/v1/admin/dashboard/summary`
- **Method**: `GET`
- **Description**: 쇼핑몰 통합 대시보드 요약 정보 조회 (최근 주문, 반품, 결제, 포인트, 문의, 배송 - 각 5건)
- **Parameters**:
    - `siteCd` (String, Optional): 사이트 코드 (관리자 권한일 경우 필수 아님, 빈 값일 경우 전체 조회)
- **Response**: `ApiResponse<DashboardDTO>`
    ```json
    {
      "success": true,
      "data": {
        "recentOrders": [...],
        "recentClaims": [...],
        "recentPayments": [...],
        "recentPoints": [...],
        "recentInquiries": [...],
        "recentDeliveries": []
      },
      "message": null
    }
    ```

## 2. Authentication (인증/인가)
Base URL: `/api/v1/auth`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/signup` | 회원가입 | 신규 회원을 등록합니다. |
| `POST` | `/login` | 로그인 | Login ID/PW로 인증하여 Access/Refresh Token을 발급합니다. (Cookie) |
| `POST` | `/refresh` | 토큰 갱신 | Refresh Token을 사용하여 Access Token을 재발급합니다. |
| `POST` | `/admin/auth/login` | 관리자 로그인 | 관리자 ID/PW로 인증하여 Access/Refresh Token을 발급합니다. |
| `POST` | `/admin/auth/refresh` | 관리자 토큰 갱신 | Refresh Token을 사용하여 Access Token을 재발급합니다. |
| `POST` | `/admin/auth/signup/scm` | SCM(입점사) 가입 | SCM 입점 파트너 회원가입을 요청합니다. (사업자등록증 첨부 필수) |

## 1.1 Shop (상점 관리) [Master Only]
Base URL: `/api/v1/admin/shops`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 상점 목록 조회 | 전체 상점 목록을 조회합니다. |
| `POST` | `/` | 상점 등록 | 신규 상점을 등록합니다. (사이트 코드, 상점명 등) |
| `GET` | `/{shopKey}` | 상점 상세 조회 | 상점 상세 정보를 조회합니다. |
| `PUT` | `/{shopKey}` | 상점 수정 | 상점 정보를 수정합니다. |

## 1. 개요
본 문서는 나눔 쇼핑몰 관리자 페이지의 API 명세를 기술한다.
Backend Server: `http://localhost:8080/api`
Current Version: v1.0

## 2. 서비스 구조 (Service Structure)
Frontend `src/services/admin` 디렉토리 하위에 도메인별로 구분하여 관리한다.

- **Member**: `admin/member/` (Member, Manager, Point)
- **Shop**: `admin/shop/` (Shop, ShopCategory)
- **Product**: `admin/product/` (Product, Category, Review)
- **Order**: `admin/order/` (Order, Payment, Delivery)
- **Display**: `admin/display/` (Banner, Popup, Recommendation)
- **Support**: `admin/support/` (Notice, FAQ, Inquiry)
- **System**: `admin/system/` (Code, Menu, Authority, Terms)
- **Dashboard**: `admin/dashboard/` (Statistics, Monitoring)
## 1.2 Manager (관리자 관리) [Master Only]
Base URL: `/api/v1/admin/managers`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 관리자 목록 조회 | 전체 관리자 목록을 조회합니다. (`applyYn` 필터 가능) |
| `POST` | `/` | 관리자 생성 | 신규 관리자 계정을 생성합니다. (승인 대기 상태로 생성됨) |
| `POST` | `/{id}/approve` | 관리자 승인 | 승인 대기 중인 관리자 계정을 승인합니다. |

### 1.2.1 Manager Auth Group (권한 그룹 관리)
Base URL: `/api/admin/manager/auth-group`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 권한 그룹 목록 조회 | 전체 권한 그룹 목록을 조회합니다. |
| `GET` | `/{seq}` | 권한 그룹 상세 조회 | 권한 그룹 상세 정보를 조회합니다. |
| `POST` | `/` | 권한 그룹 생성 | 신규 권한 그룹을 생성합니다. |
| `PUT` | `/` | 권한 그룹 수정 | 권한 그룹 정보를 수정합니다. |
| `DELETE` | `/{seq}` | 권한 그룹 삭제 | 권한 그룹을 삭제합니다. |

### 1.2.2 Manager Menu (메뉴 관리)
Base URL: `/api/admin/manager/menu`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 메뉴 목록 조회 | 전체 메뉴 목록을 계층형으로 조회합니다. |
| `GET` | `/{seq}` | 메뉴 상세 조회 | 메뉴 상세 정보를 조회합니다. |
| `POST` | `/` | 메뉴 생성 | 신규 메뉴를 생성합니다. |
| `PUT` | `/` | 메뉴 수정 | 메뉴 정보를 수정합니다. |
| `DELETE` | `/{seq}` | 메뉴 삭제 | 메뉴를 삭제합니다. |

## 2. Member (회원)

### 2.1 Admin (관리자용)
Base URL: `/api/v1/admin/members`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 회원 목록 조회 | 검색 조건(SearchDTO)에 맞는 회원 목록을 조회합니다. |
| `POST` | `/` | 회원 생성 | 관리자가 회원을 직접 생성합니다. (Master, Biz, User) |
| `GET` | `/{memberCode}` | 회원 상세 조회 | Member Code로 회원 상세 정보를 조회합니다. |
| `PUT` | `/{memberCode}` | 회원 정보 수정 | 회원 정보를 수정합니다. |
| `PATCH` | `/{memberCode}/apply-yn` | 회원 승인 상태 수정 | 회원의 승인 여부(`apply_yn`)를 수정합니다. (`applyYn` 필수) |

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
Base URL: `/api/v1/admin/categories`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 전체 카테고리 조회 | 모든 카테고리를 계층형(Tree) 구조로 조회합니다. (`parentId`, `displayOrder` 포함) |
| `POST` | `/` | 카테고리 생성 | 신규 카테고리를 생성합니다. |
| `PUT` | `/{id}` | 카테고리 수정 | 카테고리 정보를 수정합니다. |
| `DELETE` | `/{id}` | 카테고리 삭제 | 카테고리를 삭제합니다. (하위 카테고리 존재 시 불가) |
| `PATCH` | `/{id}/use-yn` | 사용 여부 변경 | 카테고리의 사용 여부(Y/N)를 토글합니다. |

### 4.2 Admin Product (상품 관리)
Base URL: `/api/v1/admin/products`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 상품 목록 조회 | 조건(카테고리, 상태, 검색어)에 맞는 상품 목록을 페이징 조회합니다. |
| `POST` | `/` | 상품 생성 | 신규 상품(기본정보, 옵션, 이미지)을 등록합니다. |
| `GET` | `/{id}` | 상품 상세 조회 | 상품 ID로 기본정보, 옵션 목록, 이미지 목록을 상세 조회합니다. |
| `PUT` | `/{id}` | 상품 수정 | 상품 정보를 수정합니다. (옵션/이미지 포함) |
| `PUT` | `/{id}/sites/{siteCd}/price` | 사이트별 가격 일괄 업데이트 | 특정 사이트의 상품 기본가, 노출 여부, 옵션 추가금을 수정합니다. |
| `DELETE` | `/{id}` | 상품 사이트별 삭제 | 특정 사이트에서만 상품을 제외합니다. (`siteCd` 필수) |
| `DELETE` | `/{id}/master` | 상품 원본 완전 삭제 | 모든 사이트에서 상품을 영구적으로 삭제합니다. (Soft Delete) |
| `PATCH` | `/{id}/status` | 상품 상태 변경 | 상품의 상태(`SALE`, `STOP`, `SOLD_OUT`)를 변경합니다. |

**Request Schema (`ProductSiteBulkCreateDTO` - 사이트별 가격 일괄 업데이트 시)**
```json
{
  "sitePrices": [
    {
      "siteCd": "A01",
      "aPrice": 15000,
      "bPrice": 16000,
      "cPrice": 17000
    },
    {
      "siteCd": "B01",
      "aPrice": 15000,
      "bPrice": 16000,
      "cPrice": 17000
    }
  ]
}
```

**Request Schema (`ProductSitePriceUpdateDTO` - 개별 사이트 업데이트 시)**
```json
{
  "viewYn": "Y",
  "aPrice": 15000,
  "bPrice": 16000,
  "cPrice": 17000
}
```


### 4.3 User Product (쇼핑몰 상품)
Base URL: `/api/v1/products`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 상품 목록 조회 | 사이트별(`siteCd`) 노출 상품 목록을 조회합니다. (`categoryId`, `keyword` 필터 지원) |
| `GET` | `/{id}` | 상품 상세 조회 | 특정 사이트의 상품 상세 정보 및 옵션을 조회합니다. (`siteCd` 필수) |

**User Pricing Policy**
- 로그인 한 사용자의 `MemberType`과 `ROLE`에 따라 가격이 상이하게 노출됩니다.
- 기업회원(BIZ): `price` = a_price, `extraPrice` = a_extra_price
- 일반회원(USER): `price` = b_price, `extraPrice` = b_extra_price
- 보훈회원(VETERAN): `price` = c_price, `extraPrice` = c_extra_price
- 비로그인: 일반회원가 기본 적용

**Response Schema (`MallProductResponse`)**
```json
{
  "status": "SUCCESS",
  "data": [
    {
      "productId": 1,
      "categoryIds": [10, 12],
      "categoryName": "의류",
      "name": "나눔 티셔츠",
      "price": 15000,
      "status": "SALE",
      "optionYn": "Y",
      "options": [
        {
          "optionId": 101,
          "name1": "Black",
          "extraPrice": 1000,
          "stockQuantity": 100
        }
      ],
      "images": []
    }
  ]
}
```

### 4.4 User Product Review (상품 리뷰)
Base URL: `/api/v1/products/{productId}/reviews`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 리뷰 목록 조회 | 특정 상품의 리뷰 목록을 페이징 조회합니다. (로그인 시 본인 `isLiked` 표기) |
| `POST` | `/` | 리뷰 작성 | 특정 상품에 리뷰를 작성합니다. |
| `PUT` | `/{reviewId}` | 리뷰 수정 | 본인이 작성한 리뷰를 수정합니다. |
| `DELETE` | `/{reviewId}` | 리뷰 삭제 | 본인이 작성한 리뷰를 삭제합니다. (Soft Delete) |
| `POST` | `/{reviewId}/like` | 리뷰 좋아요 | 특정 리뷰에 좋아요를 등록합니다. |
| `DELETE` | `/{reviewId}/like` | 리뷰 좋아요 취소 | 특정 리뷰에 등록한 좋아요를 취소합니다. |

### 4.5 Admin Product Review (상품 리뷰 관리)
Base URL: `/api/v1/admin/products/reviews`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 전체 리뷰 조회 | 전체 상품의 리뷰를 모니터링하기 위해 페이징/검색 조회합니다. |
| `DELETE` | `/{reviewId}` | 리뷰 강제 삭제 | 부적절한 리뷰를 관리자 권한으로 삭제합니다. (Soft Delete) |

**Response Schema (`ProductDTO.Response`)**
```json
{
  "status": "SUCCESS",
  "data": {
    "productId": 1,
    "categoryId": [10, 12],
    "categoryName": "의류",
    "name": "나눔 티셔츠",
    "mapPrice": 20000,
    "standardPrice": 15000,
    "status": "SALE",

    "files": [
      {
        "fileId": "uuid-...",
        "orgName": "detail_image.jpg",
        "path": "/uploads/PRODUCT/...",
        "isMain": "Y",
        "displayOrder": 0,
        "createdAt": "2026-02-09T10:00:00"
      }
    ],
    "options": []
  }
}
```

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

## 7. Management (운영 관리)

### 7.1 Inquiry (1:1 문의)
Base URL: `/api/v1`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/admin/inquiries` | [Admin] 문의 목록 | 검색 조건(작성자, 상태, 기간)에 따른 문의 목록 조회 |
| `POST` | `/admin/inquiries/{id}/reply` | [Admin] 답변 등록 | 문의에 대한 답변 등록/수정 (자동 상태 변경) |
| `GET` | `/inquiries` | [User] 내 문의 목록 | 본인의 1:1 문의 이력 조회 |
| `GET` | `/inquiries/{id}` | [User] 문의 상세 | 문의 상세 내용 및 답변 조회 |
| `POST` | `/inquiries` | [User] 문의 등록 | 새로운 1:1 문의 등록 |

### 7.2 Content (게시판: 공지/FAQ)
Base URL: `/api/v1`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/admin/contents` | [Admin] 게시글 목록 | 공지사항/FAQ 목록 관리 |
| `POST` | `/admin/contents` | [Admin] 게시글 등록 | 신규 게시글 작성 |
| `PUT` | `/admin/contents/{id}` | [Admin] 게시글 수정 | 게시글 내용 수정 |
| `DELETE` | `/admin/contents/{id}` | [Admin] 게시글 삭제 | 게시글 삭제 (Soft Delete) |
| `GET` | `/contents` | [User] 게시글 목록 | 타입별(공지/FAQ) 게시글 조회 |
| `GET` | `/contents/{id}` | [User] 게시글 상세 | 게시글 상세 조회 |

### 7.3 Banner (배너)
Base URL: `/api/v1`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/admin/banners` | [Admin] 배너 목록 | 배너 관리 목록 조회 (Files 포함) |
| `POST` | `/admin/banners` | [Admin] 배너 등록 | 신규 배너 및 이미지 등록 (Multipart/form-data) |
| `PUT` | `/admin/banners/{id}` | [Admin] 배너 수정 | 배너 정보 및 이미지 수정 (Multipart/form-data) |
| `DELETE` | `/admin/banners/{id}` | [Admin] 배너 삭제 | 배너 삭제 |
| `GET` | `/banners` | [User] 배너 목록 | 현재 노출 가능한 배너 리스트 조회 (Files 포함) |

### 7.4 Popup (팝업)
Base URL: `/api/v1`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/admin/popups` | [Admin] 팝업 목록 | 팝업 관리 목록 조회 (Files 포함) |
| `POST` | `/admin/popups` | [Admin] 팝업 등록 | 신규 팝업 등록 (Multipart/form-data) |
| `PUT` | `/admin/popups/{id}` | [Admin] 팝업 수정 | 팝업 정보 및 이미지 수정 (Multipart/form-data) |
| `DELETE` | `/admin/popups/{id}` | [Admin] 팝업 삭제 | 팝업 삭제 |
| `GET` | `/popups` | [User] 팝업 목록 | 현재 활성화된 팝업 목록 조회 (Files 포함) |

## 8. Wishlist (관심 상품)
Base URL: `/api/v1/wishlist`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | 찜하기 | 상품을 찜 목록에 추가하거나(Toggle) 등록합니다. |
| `DELETE` | `/{productId}` | 찜 취소 | 상품을 찜 목록에서 제거합니다. |
| `GET` | `/` | 찜 목록 조회 | 사용자의 찜 상품 목록을 상세 정보(`ProductDTO`)와 함께 페이징 조회합니다. |

## 5.4 Claim Management (클레임 관리)
Base URL: `/api/v1/admin/claims`

| Method | Endpoint | Summary | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | 클레임 목록 조회 | 클레임 목록을 조회합니다. |
| `GET` | `/{id}` | 클레임 상세 조회 | 클레임 상세 정보를 조회합니다. |
| `PUT` | `/{id}/status` | 클레임 상태 변경 | 클레임 상태(승인/반려/환불 등)를 변경합니다. |
