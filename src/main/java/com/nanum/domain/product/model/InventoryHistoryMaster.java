package com.nanum.domain.product.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventory_history_master")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
public class InventoryHistoryMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @Column(name = "history_date", nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime historyDate;

    @Column(name = "memo")
    private String memo;

    @Column(name = "created_by")
    private Long createdBy;

    @OneToMany(mappedBy = "master", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InventoryHistoryDetail> details = new ArrayList<>();

    public void addDetail(InventoryHistoryDetail detail) {
        this.details.add(detail);
        detail.setMaster(this);
    }
}
