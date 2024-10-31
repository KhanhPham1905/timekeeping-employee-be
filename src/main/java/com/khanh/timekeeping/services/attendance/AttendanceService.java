package com.khanh.timekeeping.services.attendance;


import com.khanh.timekeeping.requests.AttendanceRequest;

public interface AttendanceService {

    void timekeeping(AttendanceRequest request);
}
