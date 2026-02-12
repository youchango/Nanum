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
}
