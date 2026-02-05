package com.nanum.admin.management.service;

import com.nanum.domain.content.dto.ContentDTO;
import com.nanum.domain.content.model.Content;
import com.nanum.domain.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminContentService {

    private final ContentRepository contentRepository;

    public List<ContentDTO.Response> getContents() {
        return contentRepository.findAll().stream()
                .map(ContentDTO.Response::from)
                .collect(Collectors.toList());
    }

    public ContentDTO.Response getContent(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return ContentDTO.Response.from(content);
    }

    @Transactional
    public Long createContent(ContentDTO.Request request) {
        Content content = Content.builder()
                .type(request.getType())
                .subject(request.getSubject())
                .contentBody(request.getContentBody())
                .urlInfo(request.getUrlInfo())
                .deletedYn("N")
                .build();
        contentRepository.save(content);
        return content.getId();
    }

    @Transactional
    public void updateContent(Long id, ContentDTO.Request request) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        content.update(request.getType(), request.getSubject(), request.getContentBody(), request.getUrlInfo());
    }

    @Transactional
    public void deleteContent(Long id) {
        // SQLDelete annotation handles soft delete
        contentRepository.deleteById(id);
    }
}
