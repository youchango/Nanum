package com.nanum.admin.manager.service;

import com.nanum.admin.manager.entity.Manager;
import com.nanum.admin.manager.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomManagerDetailsService implements UserDetailsService {

    private final ManagerRepository managerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Manager manager = managerRepository.findByManagerId(username)
                .orElseThrow(() -> new UsernameNotFoundException("Manager not found with id: " + username));
        return new CustomManagerDetails(manager);
    }
}
