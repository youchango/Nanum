package com.nanum.user.code.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 시스템 코드 엔티티
 * 2Depth 계층 구조로 관리되는 시스템 코드
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "code")
public class Code {

    /**
     * 코드 ID (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    private Integer codeId;

    /**
     * 코드 타입 (예: SERVICE_TYPE, BUILDING_TYPE)
     */
    @Column(name = "code_type")
    private String codeType;

    /**
     * 계층 깊이 (1: 상위 코드, 2: 하위 코드)
     */
    private Integer depth;

    /**
     * 상위 코드 ID (depth=2인 경우)
     */
    private Integer upper;

    /**
     * 코드명
     */
    @Column(name = "code_name")
    private String codeName;

    /**
     * 사용 여부 (Y/N)
     */
    @Column(name = "use_yn")
    private String useYn;

    /**
     * 삭제 여부 (Y/N)
     */
    @Column(name = "delete_yn", insertable = false)
    private String deleteYn;

    /**
     * 생성일시
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 생성자 ID
     */
    @Column(name = "created_by", updatable = false)
    private Integer createdBy;

    /**
     * 수정일시
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 수정자 ID
     */
    @Column(name = "updated_by")
    private Integer updatedBy;

    /**
     * 삭제일시
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 삭제자 ID
     */
    @Column(name = "deleted_by")
    private Integer deletedBy;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.deleteYn == null) {
            this.deleteYn = "N";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
