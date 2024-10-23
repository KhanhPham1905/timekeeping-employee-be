package com.khanh.timekeeping.services;

import com.khanh.timekeeping.entities.Principal;
import com.khanh.timekeeping.entities.Role;
import com.khanh.timekeeping.entities.User;
import com.khanh.timekeeping.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PrincipalServiceImpl implements PrincipalService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findFirstByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException(String.format("Không tìm thấy User tương ứng với Username %s", username)));
        Long roleId = user.getRoleId();
        Role role = roleRepository.findById(roleId)
                .orElseThrow(
                        () -> new IllegalArgumentException(String.format("Không tìm thấy Role tương ứng với ID %s", roleId)));
        return new Principal(user, List.of(role));
    }
}
