package com.khanh.timekeeping.repositories;

import com.khanh.timekeeping.entities.User;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepositoryCustom {

    List<User> searchTopUser(
            Integer gender, Integer status, LocalDateTime createdAt, Pageable pageable);

    List<User> searchTopUserWithNativeQuery(
            Integer gender, Integer status, LocalDateTime createdAt, Pageable pageable);
}
