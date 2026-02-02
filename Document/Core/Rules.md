# 개발 규칙 (Coding Rules)

## 1. 명명 규칙 (Naming Convention)
- **Database**: `snake_case` (ex: `user_member`)
- **Java Class**: `PascalCase` (ex: `UserMember`)
- **Java Method/Variable**: `camelCase` (ex: `getUserList`)
- **JSP/File**: `snake_case` (ex: `user_list.jsp`)

## 2. API 표준 (API Standard)
- **URL**: Kebab-case 권장 (ex: `/api/user-list`)
- **Method**: RESTful (GET, POST, PUT, DELETE)
- **Response Format**:
    - 모든 Controller의 응답은 `ResponseEntity<ApiResponse<T>>` 형태를 준수한다.
    - **Header/Status Control**: `ResponseEntity`를 사용하여 HTTP Status Code(200, 201, 400 etc)와 Header를 명확하게 제어한다.
    - **Body Structure**: 실제 데이터는 `ApiResponse` 객체에 담아 균일한 구조(`status`, `message`, `data`) 로 반환한다.
    - **Example**:
      ```java
      public ResponseEntity<ApiResponse<Member>> getMember() {
          return ResponseEntity.ok(ApiResponse.success(member));
      }
      ```

## 3. 공통 컬럼 (Audit Columns)
- 모든 주요 테이블은 데이터 추적을 위해 아래 컬럼을 포함해야 한다.
- `reg_date`: 생성일시 (DEFAULT CURRENT_TIMESTAMP)
- `mod_date`: 수정일시 (ON UPDATE)
- `mod_id`: 수정자 ID

## 4. 파일 관리 정책 (File Management)
- **저장 방식**: 모든 파일은 `file_store` 테이블에 저장하며, **File Group ID** 방식으로 관리한다.
- **참조**: 업무 테이블은 `file_group_id` (UUID) 컬럼을 통해 여러 개의 파일을 묶음으로 참조한다.
- **삭제**: 원본 게시물 삭제 시 파일 데이터는 유지되므로, 별도의 배치 작업으로 정리한다.
