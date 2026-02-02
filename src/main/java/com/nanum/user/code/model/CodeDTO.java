package com.nanum.user.code.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 코드 DTO
 * 코드 등록/수정 및 조회 시 사용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeDTO {

    /**
     * 코드 ID
     */
    private Integer codeId;

    /**
     * 코드 타입
     */
    @NotBlank(message = "코드 타입은 필수입니다.")
    private String codeType;

    /**
     * 계층 깊이 (1 또는 2)
     */
    @NotNull(message = "계층 깊이는 필수입니다.")
    private Integer depth;

    /**
     * 상위 코드 ID (depth=2인 경우 필수)
     */
    private Integer upper;

    /**
     * 코드명
     */
    @NotBlank(message = "코드명은 필수입니다.")
    private String codeName;

    /**
     * 사용 여부 (Y/N)
     */
    private String useYn;

    /**
     * 삭제 여부 (Y/N)
     */
    private String deleteYn;

    // === 조회용 추가 필드 ===

    /**
     * 상위 코드명 (조회 시 사용)
     */
    private String upperCodeName;

    /**
     * 하위 코드 리스트 (계층 표시용)
     */
    private List<CodeDTO> children;

    /**
     * depth=2인 경우 상위 코드 필수 검증
     */
    public boolean isValidDepth() {
        if (depth == 2 && upper == null) {
            return false;
        }
        if (depth == 1 && upper != null) {
            return false;
        }
        return true;
    }
}
