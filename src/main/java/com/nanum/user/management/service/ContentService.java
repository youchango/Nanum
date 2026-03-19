package com.nanum.user.management.service;

import com.nanum.domain.content.dto.ContentDTO;
import com.nanum.domain.content.model.Content;
import com.nanum.domain.content.model.ContentType;
import com.nanum.domain.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;

    public List<ContentDTO.Response> getContents(ContentType type, String siteCd, String keyword) {
        List<Content> contents;
        if (keyword != null && !keyword.trim().isEmpty()) {
            contents = contentRepository.findBySearch(type, siteCd, keyword);
        } else {
            contents = contentRepository.findByTypeAndSiteCd(type, siteCd);
        }

        return contents.stream()
                .map(ContentDTO.Response::from)
                .collect(Collectors.toList());
    }

    public Page<ContentDTO.Response> getContents(ContentType type, String siteCd, Pageable pageable) {
        return contentRepository.findByTypeAndSiteCdAndDeleteYnOrderByCreatedAtDesc(type, siteCd, "N", pageable)
                .map(ContentDTO.Response::from);
    }

    public ContentDTO.Response getContent(Long id, String siteCd) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if ("Y".equals(content.getDeleteYn())) {
            throw new IllegalArgumentException("삭제된 게시글입니다.");
        }

        if (siteCd == null || !siteCd.equals(content.getSiteCd())) {
            throw new IllegalArgumentException("해당 사이트의 게시글이 아닙니다.");
        }

        return ContentDTO.Response.from(content);
    }
}
