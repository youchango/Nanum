package com.nanum.domain.content.dto;

import com.nanum.domain.content.model.ContentType;
import com.nanum.global.common.dto.SearchDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ContentSearchDTO extends SearchDTO {
    private ContentType type;
    private String siteCd;
}
