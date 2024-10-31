package com.khanh.timekeeping.services.user;

import com.khanh.timekeeping.entities.Principal;
import com.khanh.timekeeping.entities.User;
import com.khanh.timekeeping.exceptions.ERPRuntimeException;
import com.khanh.timekeeping.repositories.UserRepository;
import com.khanh.timekeeping.requests.UserRequest;
import com.khanh.timekeeping.requests.UserSearchRequest;
import com.khanh.timekeeping.responses.DefaultResponse;
import com.khanh.timekeeping.responses.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public DefaultResponse<List<UserResponse>> list(Pageable pageable, UserSearchRequest request) {
        List<Specification<User>> specifications = new ArrayList<>();
        if (StringUtils.hasText(request.getUsername())) {
            specifications.add(
                    (root, query, builder) ->
                            builder.like(root.get("username"), request.getUsername().strip() + "%"));
        }
        if (StringUtils.hasText(request.getFullName())) {
            specifications.add(
                    (root, query, builder) ->
                            builder.like(root.get("fullName"), request.getFullName().strip() + "%"));
        }
        if (Objects.nonNull(request.getGender())) {
            specifications.add(
                    (root, query, builder) -> builder.equal(root.get("gender"), request.getGender()));
        }
        Page<User> page = userRepository.findAll(Specification.allOf(specifications), pageable);
        return DefaultResponse.success(
                page,
                page.hasContent() ? page.get().map(UserResponse::of).toList() : Collections.emptyList());
    }

    @Override
    @Transactional(rollbackFor = Throwable.class, noRollbackFor = Exception.class)
    public DefaultResponse<UserResponse> create(Principal principal, UserRequest request) {
        User user = User.of(request);
        user.setCreatedBy(principal.getUserId());
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        return DefaultResponse.success(UserResponse.of(user));
    }

    @Override
    public DefaultResponse<UserResponse> update(Principal principal, UserRequest request) {
        User user =
                userRepository
                        .findById(request.getId())
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                String.format("Không tìm thấy User với ID %s!", request.getId())));
        // TODO: set các thông tin khác
        user.setRoleId(request.getRoleId());
        user.setModifiedBy(principal.getUserId());
        user.setModifiedAt(LocalDateTime.now());
        return DefaultResponse.success(UserResponse.of(user));
    }

    @Override
    public User getUser(String username) {
        return userRepository
                .findFirstByUsername(username)
                .orElseThrow(() -> new ERPRuntimeException("Không có thông tin người dùng"));
    }
}
