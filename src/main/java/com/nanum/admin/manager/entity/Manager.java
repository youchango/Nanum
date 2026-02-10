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
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manager_seq")
    private Long managerSeq;

    @Column(name = "manager_code", nullable = false, length = 30, unique = true)
    private String managerCode;

    @Column(name = "site_cd", length = 20)
    @ColumnDefault("'SITECD000001'")
    private String siteCd;

    @Column(name = "manager_id", length = 20, nullable = false, unique = true)
    private String managerId;

    @Column(name = "password", length = 200, nullable = false)
    private String password;

    @Column(name = "login_fail_count")
    @ColumnDefault("0")
    private Integer loginFailCount;

    @Column(name = "auth_group_seq", nullable = false)
    private Integer authGroupSeq;

    @Column(name = "manager_name", length = 50, nullable = false)
    private String managerName;

    @Column(name = "manager_email", length = 50, nullable = false)
    private String managerEmail;

    @Column(name = "use_yn", length = 1, nullable = false)
    @ColumnDefault("'Y'")
    private String useYn;

    @Column(name = "apply_yn", length = 1, nullable = false)
    @ColumnDefault("'N'")
    private String applyYn;

    @Column(name = "delete_yn", length = 1, nullable = false)
    @ColumnDefault("'N'")
    private String deleteYn;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "regist_by", length = 20, nullable = false)
    private String registBy;

    @Column(name = "regist_date", nullable = false, updatable = false)
    private LocalDateTime registDate;

    @Column(name = "update_by", length = 20, nullable = false)
    private String updateBy;

    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;

    @Column(name = "login_date")
    private LocalDateTime loginDate;

    @Column(name = "mb_type", length = 20, nullable = false)
    @ColumnDefault("''")
    private String mbType; // MASTER, SCM, ADMIN

    @PrePersist
    public void prePersist() {
        this.registDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateDate = LocalDateTime.now();
    }

    public void approve() {
        this.applyYn = "Y";
        this.useYn = "Y";
    }
}
