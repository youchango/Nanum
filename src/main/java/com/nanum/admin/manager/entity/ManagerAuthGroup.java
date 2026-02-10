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
@Table(name = "manager_auth_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class ManagerAuthGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_group_seq")
    private Long authGroupSeq;

    @Column(name = "auth_group_name", length = 100, nullable = false)
    private String authGroupName;

    @Column(name = "use_yn", length = 1, nullable = false)
    @ColumnDefault("'Y'")
    @Builder.Default
    private String useYn = "Y";

    @OneToMany(mappedBy = "authGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ManagerMenuGroup> menuGroups = new ArrayList<>();

    public void update(String authGroupName, String useYn) {
        this.authGroupName = authGroupName;
        this.useYn = useYn;
    }
}
