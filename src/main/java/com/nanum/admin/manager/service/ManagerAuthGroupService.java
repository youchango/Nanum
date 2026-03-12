package com.nanum.admin.manager.service;

import com.nanum.admin.manager.dto.ManagerAuthGroupDTO;
import com.nanum.admin.manager.entity.ManagerAuthGroup;
import com.nanum.admin.manager.repository.ManagerAuthGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerAuthGroupService {

    private final ManagerAuthGroupRepository managerAuthGroupRepository;

    @Transactional(readOnly = true)
    public List<ManagerAuthGroupDTO.Info> getAllAuthGroups() {
        return managerAuthGroupRepository.findAll().stream()
                .map(ManagerAuthGroupDTO.Info::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ManagerAuthGroupDTO.Info getAuthGroup(Long seq) {
        ManagerAuthGroup authGroup = managerAuthGroupRepository.findById(seq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한 그룹입니다."));
        return ManagerAuthGroupDTO.Info.from(authGroup);
    }

    @Transactional
    public Long createAuthGroup(ManagerAuthGroupDTO.CreateRequest request) {
        ManagerAuthGroup authGroup = ManagerAuthGroup.builder()
                .authGroupName(request.getAuthGroupName())
                .useYn(request.getUseYn() != null ? request.getUseYn() : "Y")
                .build();
        return managerAuthGroupRepository.save(authGroup).getAuthGroupSeq();
    }

    @Transactional
    public void updateAuthGroup(ManagerAuthGroupDTO.UpdateRequest request) {
        ManagerAuthGroup authGroup = managerAuthGroupRepository.findById(request.getAuthGroupSeq())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한 그룹입니다."));
        authGroup.update(request.getAuthGroupName(), request.getUseYn());
    }

    @Transactional
    public void deleteAuthGroup(Long seq) {
        managerAuthGroupRepository.deleteById(seq);
    }

    private final com.nanum.admin.manager.repository.ManagerMenuGroupRepository managerMenuGroupRepository;

    @Transactional(readOnly = true)
    public List<Long> getAssignedMenuSeqs(Long authGroupSeq) {
        return managerMenuGroupRepository.findAll().stream()
                .filter(mg -> mg.getAuthGroupSeq().equals(authGroupSeq))
                .map(com.nanum.admin.manager.entity.ManagerMenuGroup::getMenuSeq)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateAuthGroupMenus(Long authGroupSeq, List<Long> menuSeqs) {
        // 기존 매핑 삭제 (auth_group_seq 기준)
        List<com.nanum.admin.manager.entity.ManagerMenuGroup> existing = managerMenuGroupRepository.findAll().stream()
                .filter(mg -> mg.getAuthGroupSeq().equals(authGroupSeq))
                .collect(Collectors.toList());
        managerMenuGroupRepository.deleteAll(existing);

        // 새로운 매핑 등록
        if (menuSeqs != null && !menuSeqs.isEmpty()) {
            List<com.nanum.admin.manager.entity.ManagerMenuGroup> newMappings = menuSeqs.stream()
                    .map(menuSeq -> com.nanum.admin.manager.entity.ManagerMenuGroup.builder()
                            .authGroupSeq(authGroupSeq)
                            .menuSeq(menuSeq)
                            .build())
                    .collect(Collectors.toList());
            managerMenuGroupRepository.saveAll(newMappings);
        }
    }
}
