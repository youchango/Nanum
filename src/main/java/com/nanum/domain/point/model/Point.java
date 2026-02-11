package com.nanum.domain.point.model;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.nanum.domain.member.model.Member;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "point")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long pointId;

    @Column(name = "site_cd", length = 20)
    private String siteCd;

    @Column(name = "point_use")
    private Integer pointUse;

    @Column(name = "point_bigo")
    private String pointBigo;

    @Column(name = "point_gubun", length = 20, nullable = false)
    private String pointGubun; // SAVE:적립, USE:사용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", referencedColumnName = "member_code")
    private Member member;

    @Column(name = "order_no", length = 50)
    private String orderNo;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Builder
    public Point(Integer pointUse, String pointBigo, String pointGubun, Member member, String orderNo) {
        this.pointUse = pointUse;
        this.pointBigo = pointBigo;
        this.pointGubun = pointGubun;
        this.member = member;
        this.orderNo = orderNo;
    }
}
