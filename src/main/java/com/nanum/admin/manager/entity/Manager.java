package com.nanum.admin.manager.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "manager")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class Manager extends com.nanum.global.common.dto.BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manager_seq")
    private Long managerSeq;

    @Column(name = "manager_code", nullable = false, length = 30, unique = true)
    private String managerCode;

    @Column(name = "site_cd", length = 20)
    private String siteCd;

    @Column(name = "manager_id", length = 20, nullable = false, unique = true)
    private String managerId;

    @Column(name = "password", length = 200, nullable = false)
    private String password;

    @Column(name = "login_fail_count")
    @ColumnDefault("0")
    private Integer loginFailCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_group_seq")
    private ManagerAuthGroup authGroup;

    @OneToOne(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ManagerScm managerScm;

    @Column(name = "manager_name", length = 50, nullable = false)
    private String managerName;

    @Column(name = "manager_email", length = 50, nullable = false)
    private String managerEmail;

    @Column(name = "use_yn", length = 1, nullable = false)
    @ColumnDefault("'Y'")
    @Builder.Default
    private String useYn = "Y";

    @Column(name = "apply_yn", length = 1, nullable = false)
    @ColumnDefault("'N'")
    @Builder.Default
    private String applyYn = "N";

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "login_date")
    private LocalDateTime loginDate;

    @Column(name = "mb_type", length = 20, nullable = false)
    @ColumnDefault("''")
    private String mbType; // MASTER, SCM, ADMIN

    public void approve() {
        this.applyYn = "Y";
        this.useYn = "Y";
    }
}
