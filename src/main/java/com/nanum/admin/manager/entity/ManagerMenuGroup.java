package com.nanum.admin.manager.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "manager_menu_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@IdClass(ManagerMenuGroupId.class)
@EntityListeners(AuditingEntityListener.class)
public class ManagerMenuGroup {

    @Id
    @Column(name = "auth_group_seq")
    private Long authGroupSeq;

    @Id
    @Column(name = "menu_seq")
    private Long menuSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_group_seq", insertable = false, updatable = false)
    private ManagerAuthGroup authGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_seq", insertable = false, updatable = false)
    private ManagerMenu menu;

    @CreatedBy
    @Column(name = "regist_by", length = 200, updatable = false)
    private String registBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
