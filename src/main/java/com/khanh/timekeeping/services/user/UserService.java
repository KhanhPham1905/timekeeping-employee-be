package com.khanh.timekeeping.services.user;

import com.khanh.timekeeping.dtos.UserLoginDTO;
import com.khanh.timekeeping.entities.Principal;
import com.khanh.timekeeping.entities.User;
import com.khanh.timekeeping.requests.UserRequest;
import com.khanh.timekeeping.requests.UserSearchRequest;
import com.khanh.timekeeping.responses.DefaultResponse;
import com.khanh.timekeeping.responses.UserResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface UserService {

    DefaultResponse<List<UserResponse>> list(Pageable pageable, UserSearchRequest request);

    DefaultResponse<UserResponse> create(Principal principal, UserRequest request);

    DefaultResponse<UserResponse> update(Principal principal, UserRequest request);

    User getUser(String username);

    String login(UserLoginDTO userLoginDT) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User getUserDetailsFromRefreshToken(String token) throws Exception;
}
