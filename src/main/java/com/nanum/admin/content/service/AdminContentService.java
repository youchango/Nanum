package com.nanum.admin.content.service;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.content.model.Content;
import com.nanum.user.content.model.ContentDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminContentService {

    // private final ContentMapper contentMapper; // QueryDSL 전환으로 제거
    private final com.nanum.user.content.repository.ContentRepository contentRepository;

    /**
     * 컨텐츠 목록 조회
     */
    public List<Content> findContentList(SearchDTO searchDTO) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                .of(searchDTO.getPage() - 1, searchDTO.getRecordSize());
        return contentRepository.searchContents(searchDTO, pageable).getContent();
    }

    /**
     * 컨텐츠 개수 조회
     */
    public int findContentCount(SearchDTO searchDTO) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                .of(searchDTO.getPage() - 1, searchDTO.getRecordSize());
        return (int) contentRepository.searchContents(searchDTO, pageable).getTotalElements();
    }

    /**
     * 컨텐츠 상세 조회
     */
    public Content findContentById(int contentId) {
        return contentRepository.findById(contentId).orElse(null);
    }

    /**
     * 컨텐츠 등록
     */
    public void registerContent(ContentDTO contentDTO, String memberCode) {
        Content content = Content.builder()
                .contentTypeCode(contentDTO.getContentType())
                .subject(contentDTO.getSubject())
                .contentBody(contentDTO.getContentBody())
                .urlInfo(contentDTO.getUrlInfo())
                .createdBy(memberCode)
                .build();

        contentRepository.save(content);
    }

    /**
     * 컨텐츠 수정
     */
    @Transactional
    public void updateContent(ContentDTO contentDTO, String memberCode) {
        Content content = contentRepository.findById(contentDTO.getContentId())
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        content.setContentTypeCode(contentDTO.getContentType());
        content.setSubject(contentDTO.getSubject());
        content.setContentBody(contentDTO.getContentBody());
        content.setUrlInfo(contentDTO.getUrlInfo());
        content.setUpdatedBy(memberCode);
        // @PreUpdate will handle updatedAt
    }

    /**
     * 컨텐츠 삭제
     */
    @Transactional
    public void deleteContent(int contentId, String deletedBy) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found"));

        content.setDeletedYn("Y");
        content.setDeletedBy(deletedBy);
        content.setDeletedAt(java.time.LocalDateTime.now());
    }
}
