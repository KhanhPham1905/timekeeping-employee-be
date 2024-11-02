package com.khanh.timekeeping.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.khanh.timekeeping.dtos.SocialAccountDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserRequest extends SocialAccountDTO {

    @Schema(description = "id")
    private Long id;
    @NotEmpty(message = "Họ và tên bắt buộc nhập!")
    @Schema(description = "Họ và tên")
    private String fullName;
    @Schema(description = "Giới tính")
    private Integer gender;
    @Schema(description = "Username")
    private String username;
    @Schema(description = "Password")
    private String password;
    @Schema(description = "Trạng thái")
    private Integer status;
    @Schema(description = "ID của Role")
    private Long roleId;
    @Schema(description = "Lương 1 ngày")
    private BigDecimal dailyWage;

}

