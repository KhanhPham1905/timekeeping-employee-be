package com.khanh.timekeeping.repositories;

import com.khanh.timekeeping.constants.Gender;
import com.khanh.timekeeping.entities.User;
import com.khanh.timekeeping.entities.enums.UserStatus;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepositoryCustom {

    List<User> searchTopUser(
            Gender gender, UserStatus status, LocalDateTime createdAt, Pageable pageable);

    List<User> searchTopUserWithNativeQuery(
            Gender gender, UserStatus status, LocalDateTime createdAt, Pageable pageable);
}
