package com.khanh.timekeeping.controllers;

import com.github.javafaker.Faker;
import com.khanh.timekeeping.entities.Principal;
import com.khanh.timekeeping.entities.User;
import com.khanh.timekeeping.entities.UserElastic;
import com.khanh.timekeeping.requests.UserRequest;
import com.khanh.timekeeping.services.user.UserElasticSearchService;
import com.khanh.timekeeping.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api/users-elastic")
@RequiredArgsConstructor
@Tag(name = "User elastic", description = "User elastic search")
public class UserElasticSearchController {

    private final UserElasticSearchService userElasticSearchService;


    @PostMapping("add")
    public UserElastic createIndex(@RequestBody UserElastic userElastic) {
        return userElasticSearchService.createIndex(userElastic);
    }

    @GetMapping("get")
    public UserElastic getIndex(@RequestParam Long id) {
        return userElasticSearchService.getIndex(id);
    }

    @GetMapping("autoSuggestQuery/{name}")
    public List<String> autoSuggestQuery(@PathVariable String name) {
        return userElasticSearchService.autoSuggestItemsByNameWithQuery(name);
    }


    @PostMapping("/generateFakeUsers")
    @Operation(summary = "fake User Elastic")
    public void generateFakeUsers( @AuthenticationPrincipal Principal principal) {
        Faker faker = new Faker();
        for (int i = 0; i < 1_000_000; i++) {
            // Tạo UserRequest giả

            UserElastic userRequest = new UserElastic();
            userRequest.setId(i*1L);
            userRequest.setFullName(faker.name().fullName());
            userRequest.setUsername(faker.name().username() + i); // Đảm bảo unique username bằng cách thêm 'i'
            userRequest.setDailyWage(new BigDecimal(faker.number().numberBetween(1000, 10000))); // Mức lương ngẫu nhiên

            // Gọi hàm create để lưu user vào cơ sở dữ liệu
            createIndex(userRequest);
        }
    }
}
