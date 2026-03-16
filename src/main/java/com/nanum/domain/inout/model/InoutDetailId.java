package com.nanum.domain.inout.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * InoutDetail 엔티티의 복합키 클래스
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InoutDetailId implements Serializable {
    private String ioCode;
    private Integer no;
}
