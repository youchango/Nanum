package com.nanum.domain.code.model;

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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.nanum.global.common.dto.BaseEntity;

import java.time.LocalDateTime;

/**
 * 시스템 코드 엔티티
 * 2Depth 계층 구조로 관리되는 시스템 코드
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "code")
public class Code extends BaseEntity {

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

}
