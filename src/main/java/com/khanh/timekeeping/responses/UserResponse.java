package com.khanh.timekeeping.responses;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.khanh.timekeeping.entities.User;
import com.khanh.timekeeping.entities.enums.UserStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponse {

    private Long id;
    private String fullName;
    private Integer gender;
    private String username;
    private UserStatus status;
    private Long roleId;
    private BigDecimal dailyWage;
    private Long createdBy;
    private LocalDateTime createdAt;
    private Long modifiedBy;
    private LocalDateTime modifiedAt;

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .gender(user.getGender().getValue())
                .username(user.getUsername())
                .status(user.getStatus())
                .roleId(user.getRoleId())
                .dailyWage(user.getDailyWage())
                .createdBy(user.getCreatedBy())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
