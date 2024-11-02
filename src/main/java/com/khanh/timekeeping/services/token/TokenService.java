package com.khanh.timekeeping.services.token;

import com.khanh.timekeeping.entities.Token;
import com.khanh.timekeeping.entities.User;

public interface TokenService {
    Token addToken(User user, String token);
    Token refreshToken(String refreshToken, User user) throws Exception;
}
