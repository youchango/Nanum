package com.nanum.domain.file.dto;

import com.nanum.domain.file.model.FileStore;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FileResponseDTO {
    private String fileId;
    private String referenceType;
    private String referenceId;
    private String orgName;
    private String saveName;
    private String path;
    private String ext;
    private long size;
    private String isMain;
    private int displayOrder;
    private LocalDateTime regDate;

    public static FileResponseDTO from(FileStore fileStore) {
        return FileResponseDTO.builder()
                .fileId(fileStore.getFileId())
                .referenceType(fileStore.getReferenceType().name())
                .referenceId(fileStore.getReferenceId())
                .orgName(fileStore.getOrgName())
                .saveName(fileStore.getSaveName())
                .path(fileStore.getPath())
                .ext(fileStore.getExt())
                .size(fileStore.getSize())
                .isMain(fileStore.getIsMain())
                .displayOrder(fileStore.getDisplayOrder())
                .regDate(fileStore.getRegDate())
                .build();
    }
}
