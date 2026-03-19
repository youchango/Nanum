package com.nanum.admin.management.service;

import com.nanum.domain.content.dto.ContentDTO;
import com.nanum.domain.content.model.Content;
import com.nanum.domain.content.model.ContentType;
import com.nanum.domain.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.service.CustomManagerDetails;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminContentService {

    private final ContentRepository contentRepository;

    public List<ContentDTO.Response> getContents(String siteCd, ContentType type) {
        Manager manager = getCurrentManager();
        // MASTER 권한이 아니면 자신의 사이트 코드 강정 설정
        String filterSiteCd = siteCd;
        if (!"MASTER".equals(manager.getMbType())) {
            filterSiteCd = manager.getSiteCd();
        }

        List<Content> contents;
        if (filterSiteCd != null && type != null) {
            contents = contentRepository.findByTypeAndSiteCd(type, filterSiteCd);
        } else if (filterSiteCd != null) {
            contents = contentRepository.findBySiteCd(filterSiteCd);
        } else if (type != null) {
            contents = contentRepository.findByType(type);
        } else {
            contents = contentRepository.findAll();
        }

        return contents.stream()
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
                .siteCd(request.getSiteCd())
                .type(request.getType())
                .subject(request.getSubject())
                .contentBody(request.getContentBody())
                .urlInfo(request.getUrlInfo())
                .build();
        contentRepository.save(content);
        return content.getId();
    }

    @Transactional
    public void updateContent(Long id, ContentDTO.Request request) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        content.update(request.getSiteCd(), request.getType(), request.getSubject(), request.getContentBody(),
                request.getUrlInfo());
    }

    @Transactional
    public void deleteContent(Long id) {
        Manager manager = getCurrentManager();
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        content.delete(manager.getManagerCode());
    }

    private Manager getCurrentManager() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomManagerDetails) {
            return ((CustomManagerDetails) principal).getManager();
        }
        throw new IllegalStateException("인증된 관리자 정보가 없습니다.");
    }
}
