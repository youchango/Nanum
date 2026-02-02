package com.nanum.admin.code.service;

import com.nanum.global.common.dto.SearchDTO;
import com.nanum.user.code.model.Code;
import com.nanum.user.code.model.CodeDTO;
import com.nanum.user.code.repository.CodeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 코드 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeService {

    // private final CodeMapper codeMapper; // QueryDSL 전환으로 제거
    private final CodeRepository codeRepository;

    /**
     * 코드 목록을 조회합니다.
     *
     * @param searchDTO 검색 및 페이징 파라미터
     * @return 코드 목록
     */
    public List<Code> getCodeList(SearchDTO searchDTO) {
        log.info("코드 목록 조회 - 검색조건: {}", searchDTO);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                .of(searchDTO.getPage() - 1, searchDTO.getRecordSize());
        return codeRepository.searchCodes(searchDTO, pageable).getContent();
    }

    /**
     * 코드 전체 개수를 조회합니다.
     *
     * @param searchDTO 검색 파라미터
     * @return 코드 개수
     */
    public int getCodeCount(SearchDTO searchDTO) {
        log.info("코드 전체 개수 조회 - 검색조건: {}", searchDTO);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                .of(searchDTO.getPage() - 1, searchDTO.getRecordSize());
        return (int) codeRepository.searchCodes(searchDTO, pageable).getTotalElements();
    }

    /**
     * 코드 상세 정보를 조회합니다.
     *
     * @param codeId 코드 ID
     * @return 코드 정보
     */
    public Code getCodeDetail(int codeId) {
        log.info("코드 상세 조회 - codeId: {}", codeId);
        return codeRepository.findById(codeId)
                .orElse(null);
    }

    /**
     * 코드 타입별로 코드를 조회합니다.
     *
     * @param codeType 코드 타입
     * @return 코드 목록
     */
    public List<Code> getCodesByType(String codeType) {
        log.info("코드 타입별 조회 - codeType: {}", codeType);
        return codeRepository.findAllByCodeTypeAndDeleteYn(codeType, "N");
    }

    /**
     * 코드 ID로 코드명을 조회합니다.
     *
     * @param codeId 코드 ID (String)
     * @return 코드명
     */
    public String getCodeName(String codeId) {
        if (codeId == null) {
            return null;
        }
        try {
            int id = Integer.parseInt(codeId);
            return codeRepository.findById(id)
                    .map(Code::getCodeName)
                    .orElse(codeId); // 코드를 찾지 못하면 코드 ID 그대로 반환
        } catch (NumberFormatException e) {
            log.warn("유효하지 않은 코드 ID - codeId: {}", codeId);
            return codeId;
        }
    }

    /**
     * 상위 코드 목록을 조회합니다.
     *
     * @return 상위 코드 목록
     */
    public List<Code> getUpperCodes() {
        log.info("상위 코드 목록 조회");
        return codeRepository.findAllByDepthAndDeleteYn(1, "N");
    }

    /**
     * 계층 구조로 코드 목록을 조회합니다.
     *
     * @param searchDTO 검색 파라미터
     * @return 계층 구조 코드 목록
     */
    public List<CodeDTO> getHierarchicalCodeList(SearchDTO searchDTO) {
        log.info("계층 구조 코드 목록 조회 - 검색조건: {}", searchDTO);

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                .of(searchDTO.getPage() - 1, searchDTO.getRecordSize());
        List<Code> allCodes = codeRepository.searchCodes(searchDTO, pageable).getContent();

        // Code를 CodeDTO로 변환
        List<CodeDTO> codeDTOList = allCodes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 계층 구조 생성
        Map<Integer, CodeDTO> codeMap = new HashMap<>();
        List<CodeDTO> rootCodes = new ArrayList<>();

        for (CodeDTO codeDTO : codeDTOList) {
            codeMap.put(codeDTO.getCodeId(), codeDTO);
            if (codeDTO.getDepth() == 1) {
                rootCodes.add(codeDTO);
            }
        }

        // 하위 코드 연결
        for (CodeDTO codeDTO : codeDTOList) {
            if (codeDTO.getDepth() == 2 && codeDTO.getUpper() != null) {
                CodeDTO parent = codeMap.get(codeDTO.getUpper());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(codeDTO);
                    codeDTO.setUpperCodeName(parent.getCodeName());
                }
            }
        }

        return rootCodes;
    }

    /**
     * 코드를 등록합니다.
     *
     * @param codeDTO   코드 정보
     * @param createdBy 생성자 ID
     * @return 등록된 코드 ID
     */
    @Transactional
    public int createCode(CodeDTO codeDTO, int createdBy) {
        log.info("코드 등록 - codeDTO: {}, createdBy: {}", codeDTO, createdBy);

        // depth 검증
        if (!codeDTO.isValidDepth()) {
            throw new IllegalArgumentException("depth=2인 경우 상위 코드는 필수입니다.");
        }

        Code code = convertToEntity(codeDTO);
        code.setCreatedBy(createdBy);
        code.setUseYn("Y");
        // deleteYn is handled by @PrePersist

        codeRepository.save(code);

        log.info("코드 등록 완료 - codeId: {}", code.getCodeId());
        return code.getCodeId();
    }

    /**
     * 코드를 수정합니다.
     *
     * @param codeDTO   코드 정보
     * @param updatedBy 수정자 ID
     */
    @Transactional
    public void updateCode(CodeDTO codeDTO, int updatedBy) {
        log.info("코드 수정 - codeDTO: {}, updatedBy: {}", codeDTO, updatedBy);

        Code code = codeRepository.findById(codeDTO.getCodeId())
                .orElseThrow(() -> new IllegalArgumentException("코드를 찾을 수 없습니다."));

        // Update fields
        code.setCodeName(codeDTO.getCodeName());
        code.setUseYn(codeDTO.getUseYn());
        code.setUpdatedBy(updatedBy);
        // updatedAt is handled by @PreUpdate

        log.info("코드 수정 완료 - codeId: {}", code.getCodeId());
    }

    /**
     * 코드를 삭제합니다 (논리 삭제).
     *
     * @param codeId    코드 ID
     * @param deletedBy 삭제자 ID
     */
    @Transactional
    public void deleteCode(int codeId, int deletedBy) {
        log.info("코드 삭제 - codeId: {}, deletedBy: {}", codeId, deletedBy);

        // 하위 코드 존재 여부 확인
        int childCount = codeRepository.countByUpperAndDeleteYn(codeId, "N");
        if (childCount > 0) {
            throw new IllegalArgumentException("하위 코드가 존재하여 삭제할 수 없습니다.");
        }

        Code code = codeRepository.findById(codeId)
                .orElseThrow(() -> new IllegalArgumentException("코드를 찾을 수 없습니다."));

        code.setDeleteYn("Y");
        code.setDeletedBy(deletedBy);
        code.setDeletedAt(java.time.LocalDateTime.now());

        log.info("코드 삭제 완료 - codeId: {}", codeId);
    }

    /**
     * Code 엔티티를 CodeDTO로 변환합니다.
     *
     * @param code Code 엔티티
     * @return CodeDTO
     */
    private CodeDTO convertToDTO(Code code) {
        CodeDTO dto = new CodeDTO();
        dto.setCodeId(code.getCodeId());
        dto.setCodeType(code.getCodeType());
        dto.setDepth(code.getDepth());
        dto.setUpper(code.getUpper());
        dto.setCodeName(code.getCodeName());
        dto.setUseYn(code.getUseYn());
        dto.setDeleteYn(code.getDeleteYn());
        return dto;
    }

    /**
     * CodeDTO를 Code 엔티티로 변환합니다.
     *
     * @param dto CodeDTO
     * @return Code 엔티티
     */
    private Code convertToEntity(CodeDTO dto) {
        Code code = new Code();
        code.setCodeId(dto.getCodeId());
        code.setCodeType(dto.getCodeType());
        code.setDepth(dto.getDepth());
        code.setUpper(dto.getUpper());
        code.setCodeName(dto.getCodeName());
        code.setUseYn(dto.getUseYn());
        return code;
    }
}
