package com.nanum.domain.delivery.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.time.LocalDateTime;

@Entity
@Table(name = "address_book")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AddressBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @Column(name = "member_code", nullable = false)
    private String memberCode;

    @Column(name = "address_name")
    private String addressName;

    @Column(name = "dept_name")
    private String deptName; // 부서/프로젝트명 (기업회원용)

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "receiver_phone")
    private String receiverPhone;

    @Column(nullable = false)
    private String zipcode;

    @Column(nullable = false)
    private String address;

    @Column(name = "address_detail", nullable = false)
    private String addressDetail;

    @Column(name = "is_default", nullable = false)
    @ColumnDefault("'N'")
    private String isDefault;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void update(String addressName, String receiverName, String receiverPhone,
                       String zipcode, String address, String addressDetail, String isDefault) {
        this.addressName = addressName;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.zipcode = zipcode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.isDefault = isDefault;
    }

    public void setDefaultAddress(String isDefault) {
        this.isDefault = isDefault;
    }
}
