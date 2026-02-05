package com.nanum.user.management.service;

import com.nanum.domain.content.dto.ContentDTO;
import com.nanum.domain.content.model.Content;
import com.nanum.domain.content.model.ContentType;
import com.nanum.domain.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;

    public List<ContentDTO.Response> getContents(ContentType type) {
        return contentRepository.findByType(type).stream()
                .map(ContentDTO.Response::from)
                .collect(Collectors.toList());
    }

    public ContentDTO.Response getContent(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return ContentDTO.Response.from(content);
    }
}
