package com.nanum.domain.inout.model;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * 입출고 Master 엔티티
 * 입출고의 공통 정보를 관리합니다.
 */
@Entity
@Table(name = "inout_master")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE inout_master SET delete_yn = 'Y', deleted_at = NOW() WHERE io_seq = ?")
@Where(clause = "delete_yn = 'N'")
public class InoutMaster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "io_seq")
    private Long id;

    @Column(name = "io_code", nullable = false, unique = true, length = 45)
    private String ioCode;

    @Column(name = "io_type", nullable = false, length = 10)
    private String ioType; // IN: 입고, OUT: 출고

    @Column(name = "io_category", nullable = false, length = 20)
    private String ioCategory;

    @Column(name = "io_date")
    private LocalDateTime ioDate;

    @Column(name = "order_no", length = 45)
    private String orderNo;

    @Column(name = "manager_code", length = 30)
    private String managerCode;

    /**
     * 입출고 코드 생성 (IO + 8자리 숫자)
     * 실제 구현 시에는 시퀀스나 별도의 로직을 통해 생성해야 합니다.
     * 여기서는 외부에서 주입받는 것으로 가정합니다.
     */
}
