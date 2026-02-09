package com.nanum.admin.manager.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(name = "manager_menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class ManagerMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_seq")
    private Integer menuSeq;

    @Column(name = "parent_menu_seq")
    private Integer parentMenuSeq;

    @Column(name = "menu_name", length = 100, nullable = false)
    private String menuName;

    @Column(name = "program_url", length = 100)
    private String programUrl;

    @Column(name = "display_yn", length = 1, nullable = false)
    private String displayYn;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "regist_by", length = 200, nullable = false)
    private String registBy;

    @Column(name = "regist_date", nullable = false, updatable = false)
    private LocalDateTime registDate;

    @Column(name = "update_by", length = 200, nullable = false)
    private String updateBy;

    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;

    @Column(name = "program_parameter", length = 100, nullable = false)
    @ColumnDefault("''")
    private String programParameter;

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
