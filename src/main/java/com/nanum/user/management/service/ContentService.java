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

    public List<ContentDTO.Response> getContents(ContentType type, String siteCd) {
        return contentRepository.findByTypeAndSiteCd(type, siteCd).stream()
                .filter(c -> "N".equals(c.getDeleteYn()))
                .map(ContentDTO.Response::from)
                .collect(Collectors.toList());
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
