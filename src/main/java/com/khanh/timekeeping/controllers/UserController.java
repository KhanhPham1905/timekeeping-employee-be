package com.khanh.timekeeping.controllers;


import com.github.javafaker.Faker;
import com.khanh.timekeeping.dtos.UserLoginDTO;
import com.khanh.timekeeping.entities.Principal;
import com.khanh.timekeeping.entities.Token;
import com.khanh.timekeeping.entities.User;
import com.khanh.timekeeping.requests.UserRequest;
import com.khanh.timekeeping.requests.UserSearchRequest;
import com.khanh.timekeeping.responses.DefaultResponse;
import com.khanh.timekeeping.responses.LoginResponse;
import com.khanh.timekeeping.responses.UserResponse;
import com.khanh.timekeeping.services.auth.AuthService;
import com.khanh.timekeeping.services.token.TokenService;
import com.khanh.timekeeping.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User")
public class UserController {

    private final AuthService authService;
    private final UserService userService;
    private final TokenService tokenService;

    @GetMapping
    public ResponseEntity<DefaultResponse<List<UserResponse>>> list(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "limit", defaultValue = "100") Integer limit,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "full_name", required = false) String fullName,
            @RequestParam(value = "gender", required = false) Integer gender
    ) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("id").descending());
        UserSearchRequest request = UserSearchRequest.builder()
                .fullName(fullName)
                .username(username)
                .gender(gender)
                .build();
        return ResponseEntity.ok(userService.list(pageable, request));
    }

//  Xử lý lỗi validation = tay
//  @PostMapping
//  @Operation(summary = "Tạo User")
//  public ResponseEntity<DefaultResponse<UserResponse>> create(
//    @Valid @RequestBody UserRequest request,
//    BindingResult bindingResult
//  ) {
//    if (bindingResult.hasErrors()) {
//      return ResponseEntity.badRequest()
//        .body(DefaultResponse.error(bindingResult.getAllErrors().get(0).getDefaultMessage()));
//    }
//    return ResponseEntity.ok(userService.create(request));
//  }

    @PostMapping
    @Operation(summary = "Tạo User")
    @PreAuthorize("hasAuthority(T(com.khanh.timekeeping.constants.RoleConst).CEO)")
    public ResponseEntity<DefaultResponse<UserResponse>> create(
            @AuthenticationPrincipal Principal principal,
            @Valid @RequestBody UserRequest request
    ) {
        return ResponseEntity.ok(userService.create(principal, request));
    }

    @PutMapping
    @Operation(summary = "Cập nhật User")
    @PreAuthorize("hasAnyAuthority(T(com.khanh.timekeeping.constants.RoleConst).CEO, T(com.khanh.timekeeping.constants.RoleConst).TL)")
    public ResponseEntity<DefaultResponse<UserResponse>> update(
            @AuthenticationPrincipal Principal principal,
            @Valid @RequestBody UserRequest request
    ) {
        return ResponseEntity.ok(userService.update(principal, request));
    }

    @PostMapping("/generateFakeUsers")
    @Operation(summary = "fake User")
    public void generateFakeUsers( @AuthenticationPrincipal Principal principal) {
        Faker faker = new Faker();
        for (int i = 0; i < 1_000_000; i++) {
            // Tạo UserRequest giả
            UserRequest userRequest = new UserRequest();
            userRequest.setFullName(faker.name().fullName());
            userRequest.setGender(faker.number().numberBetween(0, 2)); // 0: Nữ, 1: Nam, 2: Khác
            userRequest.setUsername(faker.name().username() + i); // Đảm bảo unique username bằng cách thêm 'i'
            userRequest.setStatus(1); // Giả sử là "đang hoạt động"
            userRequest.setRoleId((long) faker.number().numberBetween(1, 14)); // Giả sử có 10 roles
            userRequest.setDailyWage(new BigDecimal(faker.number().numberBetween(1000, 10000))); // Mức lương ngẫu nhiên

            // Gọi hàm create để lưu user vào cơ sở dữ liệu
            this.create(principal, userRequest);
        }
    }

    @GetMapping("/auth/social-login")
    public ResponseEntity<String> socialAuth(
            @RequestParam("login_type") String loginType,
            HttpServletRequest request
    ){
        //request.getRequestURI()
        loginType = loginType.trim().toLowerCase();  // Loại bỏ dấu cách và chuyển thành chữ thường
        String url = authService.generateAuthUrl(loginType);
        return ResponseEntity.ok(url);
    }


    @GetMapping("/auth/social/callback")
    public ResponseEntity<DefaultResponse> callback(
            @RequestParam("code") String code,
            @RequestParam("login_type") String loginType,
            HttpServletRequest request
    ) throws Exception {
        // Call the AuthService to get user info
        Map<String, Object> userInfo = authService.authenticateAndFetchProfile(code, loginType);

        if (userInfo == null) {
            return ResponseEntity.ok(DefaultResponse.error(
                    "Failed to authenticate"
            ));
        }

        // Extract user information from userInfo map
        String accountId = "";
        String name = "";
        String picture = "";
        String email = "";

        if (loginType.trim().equals("google")) {
            accountId = (String) Objects.requireNonNullElse(userInfo.get("sub"), "");
            name = (String) Objects.requireNonNullElse(userInfo.get("name"), "");
            picture = (String) Objects.requireNonNullElse(userInfo.get("picture"), "");
            email = (String) Objects.requireNonNullElse(userInfo.get("email"), "");
        } else if (loginType.trim().equals("facebook")) {
            accountId = (String) Objects.requireNonNullElse(userInfo.get("id"), "");
            name = (String) Objects.requireNonNullElse(userInfo.get("name"), "");
            email = (String) Objects.requireNonNullElse(userInfo.get("email"), "");
            // Lấy URL ảnh từ cấu trúc dữ liệu của Facebook
            Object pictureObj = userInfo.get("picture");
            if (pictureObj instanceof Map) {
                Map<?, ?> pictureData = (Map<?, ?>) pictureObj;
                Object dataObj = pictureData.get("data");
                if (dataObj instanceof Map) {
                    Map<?, ?> dataMap = (Map<?, ?>) dataObj;
                    Object urlObj = dataMap.get("url");
                    if (urlObj instanceof String) {
                        picture = (String) urlObj;
                    }
                }
            }
        }

        // Tạo đối tượng UserLoginDTO
        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .email(email)
                .fullname(name)
                .password("")
                .phoneNumber("")
                .profileImage(picture)
                .build();

        if (loginType.trim().equals("google")) {
            userLoginDTO.setGoogleAccountId(accountId);
            userLoginDTO.setFacebookAccountId("");
        } else if (loginType.trim().equals("facebook")) {
            userLoginDTO.setFacebookAccountId(accountId);
            userLoginDTO.setGoogleAccountId("");
        }

        return this.login(userLoginDTO, request);
    }

    @PostMapping("/login")
    public ResponseEntity<DefaultResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request
    ) throws Exception {
        // Kiểm tra thông tin đăng nhập và sinh token
        String token = userService.login(userLoginDTO);
        User userDetail = userService.getUserDetailsFromToken(token);
        Token jwtToken = tokenService.addToken(userDetail, token);

        LoginResponse loginResponse = LoginResponse.builder()
                .message("Login successfully")
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                //.roles(userDetail.getAuthorities().stream().map(item -> item.getAuthority()).toList())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) //method reference
                .id(userDetail.getId())
                .build();
        return ResponseEntity.ok().body(DefaultResponse.success(loginResponse));
    }
}
