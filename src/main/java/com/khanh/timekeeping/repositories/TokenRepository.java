package com.khanh.timekeeping.repositories;

import com.khanh.timekeeping.entities.Token;
import com.khanh.timekeeping.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUser(User user);
    Token findByToken(String token);
    Token findByRefreshToken(String token);
}
