package com.khanh.timekeeping.services.user;

import com.khanh.timekeeping.dtos.UserLoginDTO;
import com.khanh.timekeeping.entities.Principal;
import com.khanh.timekeeping.entities.Role;
import com.khanh.timekeeping.entities.Token;
import com.khanh.timekeeping.entities.User;
import com.khanh.timekeeping.exceptions.DataNotFoundException;
import com.khanh.timekeeping.exceptions.ERPRuntimeException;
import com.khanh.timekeeping.exceptions.ExpiredTokenException;
import com.khanh.timekeeping.repositories.RoleRepository;
import com.khanh.timekeeping.repositories.TokenRepository;
import com.khanh.timekeeping.repositories.UserRepository;
import com.khanh.timekeeping.requests.UserRequest;
import com.khanh.timekeeping.requests.UserSearchRequest;
import com.khanh.timekeeping.responses.DefaultResponse;
import com.khanh.timekeeping.responses.UserResponse;
import com.khanh.timekeeping.utils.JwtTokenUtils;
import com.khanh.timekeeping.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtil;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final ValidationUtils validationUtils;
    private final PasswordEncoder passwordEncoder;

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
        if (!request.getUsername().isBlank() && userRepository.existsByUsername(request.getUsername())) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        User user = User.of(request);
        user.setCreatedBy(principal.getUserId());
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);
        if (!request.isSocialLogin()) {
            String password = request.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
        }
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



    @Override
    public String login(UserLoginDTO userLoginDTO) throws Exception {
        Optional<User> optionalUser = Optional.empty();
        String subject = null;
        Role roleUser =roleRepository.findByCode("USER")
                .orElseThrow(() -> new DataNotFoundException("Role user not found"));
        // Check by Google Account ID
        if (userLoginDTO.getGoogleAccountId() != null && userLoginDTO.isGoogleAccountIdValid()) {
            optionalUser = userRepository.findByGoogleAccountId(userLoginDTO.getGoogleAccountId());
            subject = "Google:" + userLoginDTO.getGoogleAccountId();
            // Nếu không tìm thấy người dùng, tạo bản ghi mới
            if (optionalUser.isEmpty()) {
                User newUser = User.builder()
                        .fullName(userLoginDTO.getFullname() != null ? userLoginDTO.getFullname() : "")
                        .username(userLoginDTO.getEmail() != null ? userLoginDTO.getEmail() : "")
                        .profileImage(userLoginDTO.getProfileImage() != null ? userLoginDTO.getProfileImage(): "")
                        .roleId(roleUser.getId())
                        .googleAccountId(userLoginDTO.getGoogleAccountId())
                        .password("")
                        .build();

                // Lưu người dùng mới vào cơ sở dữ liệu
                newUser = userRepository.save(newUser);
                // Optional trở thành có giá trị với người dùng mới
                optionalUser = Optional.of(newUser);
            }

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("email", userLoginDTO.getEmail());
            return jwtTokenUtil.generateToken(optionalUser.get());
        }
        // Check by Facebook Account ID
        if (optionalUser.isEmpty() && userLoginDTO.isFacebookAccountIdValid()) {
            optionalUser = userRepository.findByFacebookAccountId(userLoginDTO.getFacebookAccountId());
            subject = "Facebook:" + userLoginDTO.getFacebookAccountId();

            // Nếu không tìm thấy người dùng, tạo bản ghi mới
            if (optionalUser.isEmpty()) {
                User newUser = User.builder()
                        .fullName(userLoginDTO.getFullname() != null ? userLoginDTO.getFullname() : "")
                        .username(userLoginDTO.getEmail() != null ? userLoginDTO.getEmail() : "")
                        .facebookAccountId(userLoginDTO.getFacebookAccountId())
                        .roleId(roleUser.getId())
                        .password("") // Thiết lập mật khẩu là chuỗi rỗng
                        .build();

                // Lưu người dùng mới vào cơ sở dữ liệu
                newUser = userRepository.save(newUser);

                // Optional trở thành có giá trị với người dùng mới
                optionalUser = Optional.of(newUser);
            }
        }
        User existingUser = optionalUser.get();
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String subject = jwtTokenUtil.getSubject(token);
        Optional<User> user;
        user = userRepository.findByPhoneNumber(subject);
        if (user.isEmpty() && validationUtils.isValidEmail(subject)) {
            user = userRepository.findByEmail(subject);
        }
        return user.orElseThrow(() -> new Exception("User not found"));
    }
    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }

}
