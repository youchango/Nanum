package com.nanum.domain.file.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "file_store", indexes = {
        @Index(name = "idx_file_ref", columnList = "reference_type, reference_id"),
        @Index(name = "idx_file_reg", columnList = "reg_date")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FileStore {

    @Id
    @Column(name = "file_id", length = 36)
    private String fileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", nullable = false, length = 50)
    private ReferenceType referenceType;

    @Column(name = "reference_id", nullable = false, length = 50)
    private String referenceId;

    @Column(name = "org_name", nullable = false)
    private String orgName;

    @Column(name = "save_name", nullable = false)
    private String saveName;

    @Column(nullable = false, length = 500)
    private String path;

    @Column(nullable = false, length = 10)
    private String ext;

    @Column(nullable = false)
    @ColumnDefault("0")
    private long size;

    @Column(name = "is_main", nullable = false, length = 1)
    @ColumnDefault("'N'")
    private String isMain;

    @Column(name = "display_order", nullable = false)
    @ColumnDefault("0")
    private int displayOrder;

    @Column(name = "reg_date", nullable = false, updatable = false)
    private LocalDateTime regDate;

    public void updateReference(ReferenceType referenceType, String referenceId) {
        this.referenceType = referenceType;
        this.referenceId = referenceId;
    }

    @Column(name = "delete_yn", nullable = false)
    @ColumnDefault("'N'")
    @Builder.Default
    private String deleteYn = "N";

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    public void delete(String memberCode) {
        this.deleteYn = "Y";
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = memberCode;
    }

    @PrePersist
    public void prePersist() {
        if (this.fileId == null) {
            this.fileId = UUID.randomUUID().toString();
        }
        if (this.regDate == null) {
            this.regDate = LocalDateTime.now();
        }
    }
}
