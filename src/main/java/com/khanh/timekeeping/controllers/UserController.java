package com.khanh.timekeeping.controllers;


import com.khanh.timekeeping.entities.Principal;
import com.khanh.timekeeping.requests.UserRequest;
import com.khanh.timekeeping.requests.UserSearchRequest;
import com.khanh.timekeeping.responses.DefaultResponse;
import com.khanh.timekeeping.responses.UserResponse;
import com.khanh.timekeeping.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User")
public class UserController {

    private final UserService userService;

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
    @PreAuthorize("hasAuthority(T(com.ghtk.sample004.constants.RoleConst).CEO)")
    public ResponseEntity<DefaultResponse<UserResponse>> create(
            @AuthenticationPrincipal Principal principal,
            @Valid @RequestBody UserRequest request
    ) {
        return ResponseEntity.ok(userService.create(principal, request));
    }

    @PutMapping
    @Operation(summary = "Cập nhật User")
    @PreAuthorize("hasAnyAuthority(T(com.ghtk.sample004.constants.RoleConst).CEO, T(com.ghtk.sample004.constants.RoleConst).TL)")
    public ResponseEntity<DefaultResponse<UserResponse>> update(
            @AuthenticationPrincipal Principal principal,
            @Valid @RequestBody UserRequest request
    ) {
        return ResponseEntity.ok(userService.update(principal, request));
    }
}
