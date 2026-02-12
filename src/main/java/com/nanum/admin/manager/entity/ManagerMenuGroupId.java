package com.nanum.admin.manager.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ManagerMenuGroupId implements Serializable {
    private Long authGroupSeq;
    private Long menuSeq;
}
