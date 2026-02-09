package com.nanum.admin.manager.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "manager_auth_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class ManagerAuthGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_group_seq")
    private Integer authGroupSeq;

    @Column(name = "auth_group_name", length = 100, nullable = false)
    private String authGroupName;

    @Column(name = "use_yn", length = 1, nullable = false)
    private String useYn;

    @Column(name = "regist_by", length = 200, nullable = false)
    private String registBy;

    @Column(name = "regist_date", nullable = false, updatable = false)
    private LocalDateTime registDate;

    @Column(name = "update_by", length = 200, nullable = false)
    private String updateBy;

    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;

    @PrePersist
    public void prePersist() {
        this.registDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}
