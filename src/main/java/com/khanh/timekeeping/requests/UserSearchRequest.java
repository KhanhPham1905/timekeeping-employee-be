package com.khanh.timekeeping.requests;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchRequest {

    private String username;
    private String fullName;
    private Integer gender;

}

