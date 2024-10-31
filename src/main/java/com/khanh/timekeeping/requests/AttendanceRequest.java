package com.khanh.timekeeping.requests;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class AttendanceRequest {

    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public enum AttendanceType {
        CHECKIN,
        CHECKOUT
    }

    private String username;
    private AttendanceType type;
    private LocalDateTime time;

    public void setTime(String time) {
        this.time = LocalDateTime.parse(time, format);
    }
}
