# 📊 기능 개발 진척도 (Feature Progress)

## 📌 개요
**나눔 쇼핑몰 플랫폼** 프로젝트의 핵심 기능별 개발 현황을 관리하는 문서입니다.

---

## 🏗️ 구성원별 역할 (Role)
- **Master**: 시스템 총괄 관리자 (사용자, 주문, 상품, 운영 정책 관리)
- **Biz**: 기업 회원 (대량 구매, 기업 전용 상품 접근)
- **User**: 일반 개인 회원 (일반 쇼핑 서비스 이용)

---

## 🛠️ 기능별 현황

### 1. 인프라 및 핵심 모듈 (Core)
- [x] 프로젝트 초기화 (Spring Boot, Gradle)
- [x] DB Schema 설계 및 구축 (PostgreSQL)
- [x] 멀티 모듈 구조 설계
- [ ] CI/CD 파이프라인 구축
- [x] SCM 회원가입 (비즈니스 로직 및 통합 테스트)
- [x] 쇼핑몰 통합 대시보드 고도화 (Backend)
    - [x] DashboardDTO 및 Service/Controller 구현
    - [x] 각 도메인 Repository 연결 (Top 5 조회)
    - [x] API Spec 문서 현행화로직
    - [x] OrderMaster 엔티티 DB 컬럼 불일치(delivery_msg) 버그 수정
    - [x] OrderMaster 누락 파라미터(DB order_name, 자바 order_no) 일괄 추가 최적화

### 2. 인증 및 권한 (Auth)
- [x] JWT 기반 통합 인증 시스템
- [x] 권한별 접근 제어 (Master, Biz, User)
- [x] 회원가입 및 로그인 기본 로직

### 3. 회원 시스템 (Member)
- **Common**
    - [x] 아이디 중복 확인 API (MemberType별 구분)
- **Master (관리자)**
    - [x] 사용자 관리 (회원 조회, 상태 변경, 제재 등) - 백/프론트 명세 일치화 및 분리 완료
    - [x] 사용자 승인 (회원 가입 이후 관리자 승인 필요 - Admin UI 토글, 로그인 제한 및 전용 PATCH API 구현 완료)
    - [x] 권한 분리 및 리팩토링 (`MemberType` 분리 및 `MemberRole` 정리 완료 - ROLE_VETERAN 정합성 수정)
    - [x] 매니저 할당 및 조회 기능 제거 (DB 미존재로 인한 500 에러 해결)
    - [x] 회원 등록 시 사업자 등록번호 필드 제거 및 유형별 권한(BIZ/USER/VETERAN) 최적화
    - [x] **회원 관리 고도화 (ROLE_MASTER/ADMIN 제외 및 기업 회원 상세 정보(MemberBiz) 연동 완료)**
    - [x] 클레임 관리 (Claim)
    - [x] 공지사항 관리 (Content)
    - [x] 1:1문의 관리 (Inquiry)
    - [x] 배너 및 팝업 관리 (Banner, Popup)
    - [x] 관리자 로그인 및 권한 관리 (Back-end)
- **Biz (기업회원)**
    - [x] 기업 정보 등록 및 가입 프로세스 고도화 (member_biz 연동 및 스키마 정규화 완료)
- **User (일반회원)**
    - [ ] 프로필 관리 및 배송지 관리
    - [ ] 1:1문의 작성 및 내역 확인

### 4. 상품 및 쇼핑 (Product & Shop)
- [x] 상품 카테고리 관리 (다층 구조, 트리 API)
- [x] 상품 관리 (Admin CRUD)
    - [x] 상품 등록 (옵션/이미지 처리 및 UI 검토 완료)
    - [x] 상품 수정 및 상태 변경 (판매중/품절/중지)
    - [x] 상품 삭제 (무결성 보장)
    - [x] 상품 및 옵션 연결 무결성 강화 및 추가금액 컬럼 로직 동기화 (`a_extra_price` 등)
    - [x] 상품 목록 페이징(0-based Offset) 연동 버그 수정 및 상태 변경 UI 재배치
    - [x] 상품 API 페이징 파라미터 컨벤션 마이그레이션 (SearchDTO 기반 1-based Pagination 완벽 호환)
    - [x] **상품 검색 및 필터링 통합 (SearchDTO 기반 카테고리/키워드 검색 구현 완료)**
    - [x] 상품 목록 내 렌더링 방식 고도화: 자세히 보기(`expandable`) 데이터 출력 시, `siteCd` 기준으로 Javascript 그룹화(reduce) 및 단가표 분할 렌더링.
    - [x] **상품 가격 상세표 UI 개편**: 기존 확장(Expandable)뷰 방식 제거 후, `Modal` 팝업 및 사이트별 탭(`Tabs`)으로 컴포넌트 재구성 완료.
    - [x] **상품 가격 관리(ProductSiteList) 분리 및 고도화**: `ROLE_MASTER` 전용 메뉴 신설. 체크박스로 선택한 사이트에 대한 동적 단가(A/B/C) 입력 표 제공 및 개별 Insert API 연동 완료, 상세 팝업 탭 명칭 ShopName 전환.
    - [x] **백엔드/프론트엔드 빌드 오류 및 프록시 에러 해결**: 에러 코드 정합성 수리 완료 및 `application-local.yml` 8090 포트 지정으로 로그인(ECONNREFUSED) 해결.
- [x] **관심상품(Wishlist) 기능 최적화 및 리팩토링**
    - [x] DB 엔티티 `BaseEntity` 상속을 통한 논리적 삭제(Soft Delete) 도입 완료
    - [x] `JOIN FETCH` 및 `IN` 절 쿼리 방식 튜닝을 통해 N+1 성능 현안 해결
    - [x] `sale_price` 필드를 완전 퇴출하고 Cart와 동일한 구조의 권한별 다중 가격 계산 로직(`unitPrice`, `retailPrice`) 이식 완료
- [ ] 일반/기업 전용 상품 등록 및 노출 제어 (Site Code 적용 완료)
- [ ] 상품 옵션 및 재고 관리 시스템
- [ ] **상품 리뷰 및 평가 (Product Review & Like)**
    - [ ] DB 스키마 (`product_review`, `product_review_like`) 구축 완료
    - [ ] 사용자(User) - 상품 리뷰 및 별점 등록/수정/논리삭제(Soft Delete) 기능 구현
    - [ ] 사용자(User) - 리뷰에 대한 좋아요(Like) 기능 (Hard Delete 토글 방식) 구현
    - [ ] 관리자(Admin) - 상품별 리뷰 내역 조회 및 권한 제어

### 5. 주문 및 결제 (Order & Pay)
- [x] **스마트 장바구니 구현 (Smart Cart)**
  - [x] `Cart` 엔티티 및 리포지토리 생성
  - [x] `CartDTO` 생성 (`forceUpdate` 포함)
  - [x] `CartService` 구현 (중복 상품 체크 로직)
  - [x] `CartController` 구현 (409 Conflict 처리)
- [ ] **주문 (Order)**
- [ ] 결제 시스템 연동 (PG/가상계좌)
- [ ] 입출고 및 재고 동기화

### 6. 관리자 시스템 (Admin Back-Office)
- [x] 관리자 인증 (로그인/토큰 갱신)
- [x] **매니저 계정 관리 (완료)**
  - [x] DB 스키마 (`manager` 테이블 `apply_yn` 추가)
  - [x] 백엔드 API (생성, 조회, 승인)
  - [x] 프론트엔드 (목록, 등록, 승인 UI)
  - [x] SCM(입점사) 회원가입 및 승인 프로세스 구현 (Backend/Frontend)
  - [x] **최고관리자/SCM/쇼핑몰관리자(ADMIN) 조회 API 응답 포장 및 패치 에러 수정 (렌더링 정상화)**
  - [x] **관리자 정보 갱신(수정) 및 논리 삭제(`UseYn='N'`) UI / API 구현 및 양방향 연동 완료**
  - [x] **내 정보 관리 (마이페이지) 기능 구현 (완료)**
    - [x] 본인 정보 조회 및 수정 전용 API (`/api/v1/admin/me`)
    - [x] `ProfilePage.tsx` 신설 및 메뉴 연결
    - [x] 비밀번호 변경 유효성 검사 및 SCM 상세 정보 수정 연동
- [ ] 회원 관리
- [ ] 상품 관리
- [ ] 주문 관리
- [x] **클레임(취소/교환/반품) 관리 (완료)**
  - [x] DB 스키마 (Claim 테이블)
  - [x] 백엔드 API (목록, 상세, 상태 변경)
  - [x] 프론트엔드 구현

### 7. 멀티 테넌시 (Multi-Tenancy) & 상점 (Shop)
- [x] `ShopInfo` 도메인 구축 (Entity, Repository)
- [x] 관리자 `siteCd` 기반 데이터 격리 (Product, Order, Member 등)
- [x] **Master 관리자 기능**
  - [x] 상점 관리 (등록/수정/조회/삭제 API 완료)
  - [x] 상점별 데이터 조회 필터링 (Header Tab UI)
- [x] 프론트엔드 상점 관리 페이지 (`Nanum_Master`)

## 통합 파일 저장소 (Unified File Store)
- [x] DB 스키마 (`file_store`)
- [x] `FileService` (공통 모듈)
- [x] 상품 도메인 연동
- [ ] 배너/팝업 등 기타 도메인 연동

## 논리적 삭제 구현 (Soft Delete)
- [x] DB 스키마 업데이트 (`delete_yn`, `deleted_at`, `deleted_by`)
- [x] 엔티티 리팩토링 (상품, 게시글, 문의, 배너, 팝업, 파일 등)
- [x] 서비스 리팩토링 (Soft Delete 로직 및 조회 시 필터링)
- [x] 관리자/사용자 뷰 분리
- [x] 연관 파일 Cascading Delete 처리

## 시스템 안정화 및 표준화 (System Stabilization)
- [x] **Audit Column 표준화 (Standardization)**
  - [x] DB 스키마 (`created_at`, `created_by`, `updated_at`, `updated_by` 등) 통일
  - [x] Entity 리팩토링 (`BaseEntity` 적용 및 중복 제거)
  - [x] `merge_sql.py` 스크립트 수정 및 `init_db.sql` 재생성
  - [x] SQL 파일 UTF-8 인코딩 점검 및 BOM 제거
   - [x] **전사 API 응답 규격 표준화 (ResponseEntity<ApiResponse<T>> 통일 완료)**
- [x] **관리자 권한 및 메뉴 관리 (완료)**
  - [x] DB 스키마 (`manager_menu`, `manager_auth_group`, `manager_menu_group`)
  - [x] 백엔드 API (권한 CRUD, 메뉴 트리 조회, 권한별 메뉴 매핑 로직)
  - [x] 프론트엔드 (권한 관리, 트리 구조 메뉴 권한 설정 모달, 계층형 메뉴 관리 UI)
  - [x] `ROLE_MASTER` 전용 상시 노출 메뉴 적용
- [x] **사이트 설정 및 약관 관리 통합 (완료)**
  - [x] 중복 `basic` 테이블 및 `basic.sql` 제거
  - [x] 프론트엔드 `SiteSettingsPage` 및 `siteService` 통합 이전 (`system` 메뉴 하위)
  - [x] `TermsManagePage` 및 `termService` 삭제
  - [x] 사이트 정책 관리 UI 개선 (저장 버튼 하단 이동)


## 프론트엔드 리팩토링 (Nanum_Master)
- [x] 서비스 계층 분리 (`memberService.ts`)
- [x] 페이지 리팩토링 (`MemberListPage.tsx`)
- [x] UI/UX 최적화 (상태 태그, 필터 기능)
- [x] ~~**기업 회원가입 구현 (프론트/백)**~~ (완료)
- [x] ~~**메인 상품 조회 API 및 Swagger UI 개선**~~ (완료)
- [x] **Manager 컨트롤러 명칭 일괄 변경 (`Manager*` -> `AdminManager*`)** (완료)

---

## 📅 주간 일정 계획 (예시)Timeline)
- 2026.02.02: 쇼핑몰 플랫폼 전환 및 PRD 작성 시작
- 2026.02.03: 관리자/사용자 상세 기능 명시 및 배송 로직 일반화 반영
