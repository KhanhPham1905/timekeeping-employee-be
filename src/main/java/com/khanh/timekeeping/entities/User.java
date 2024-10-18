package com.khanh.timekeeping.entities;


import com.khanh.timekeeping.constants.Gender;
import com.khanh.timekeeping.entities.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@ToString
public class User {

    public static final int STATUS_INACTIVE = 0;
    public static final int STATUS_ACTIVE = 1;

    public static final int GENDER_FEMALE = 0;
    public static final int GENDER_MALE = 1;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "gender")
    @Convert(converter = Gender.EnumConverter.class)
    private Gender gender;

    @Column(name = "username")
    private String username;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private UserStatus status;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "daily_wage")
    private BigDecimal dailyWage;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_by")
    private Long modifiedBy;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

//    public static User of(UserRequest request) {
//        return User.builder()
//                .fullName(request.getFullName())
//                .gender(Gender.of(request.getGender()))
//                .username(request.getUsername())
//                .status(UserStatus.of(request.getStatus()))
//                .roleId(request.getRoleId())
//                .dailyWage(request.getDailyWage())
//                .build();
//    }

    @Override
    public String toString() {
        return "User{"
                + "id="
                + id
                + ", fullName='"
                + fullName
                + '\''
                + ", username='"
                + username
                + '\''
                + '}';
    }
}
