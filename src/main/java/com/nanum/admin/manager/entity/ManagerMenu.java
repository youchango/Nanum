package com.nanum.admin.manager.entity;

import com.nanum.global.common.dto.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "manager_menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class ManagerMenu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_seq")
    private Long menuSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_menu_seq")
    private ManagerMenu parent;

    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<ManagerMenu> children = new ArrayList<>();

    @Column(name = "menu_name", length = 100, nullable = false)
    private String menuName;

    @Column(name = "menu_url", length = 100)
    private String menuUrl;

    @Column(name = "display_yn", length = 1, nullable = false)
    @ColumnDefault("'Y'")
    @Builder.Default
    private String displayYn = "Y";

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "menu_parameter", length = 100, nullable = false)
    @ColumnDefault("''")
    @Builder.Default
    private String menuParameter = "";

    public void update(String menuName, String menuUrl, String displayYn, Integer displayOrder,
            String menuParameter, ManagerMenu parent) {
        this.menuName = menuName;
        this.menuUrl = menuUrl;
        this.displayYn = displayYn;
        this.displayOrder = displayOrder;
        this.menuParameter = menuParameter;
        this.parent = parent;
    }
}
