package com.nanum.admin.manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "manager_menu_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@IdClass(ManagerMenuGroupId.class)
public class ManagerMenuGroup {

    @Id
    @Column(name = "auth_group_seq")
    private Integer authGroupSeq;

    @Id
    @Column(name = "menu_seq")
    private Integer menuSeq;

    @Column(name = "regist_by", length = 200, nullable = false)
    private String registBy;

    @Column(name = "regist_date", nullable = false, updatable = false)
    private LocalDateTime registDate;

    @PrePersist
    public void prePersist() {
        this.registDate = LocalDateTime.now();
    }
}

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
class ManagerMenuGroupId implements Serializable {
    private Integer authGroupSeq;
    private Integer menuSeq;
}
