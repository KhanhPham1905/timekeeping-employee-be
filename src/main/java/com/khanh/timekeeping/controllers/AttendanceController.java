package com.khanh.timekeeping.controllers;


import com.khanh.timekeeping.requests.AttendanceRequest;
import com.khanh.timekeeping.responses.DefaultResponse;
import com.khanh.timekeeping.services.attendance.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
@Tag(name = "Attendances", description = "Attendances")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("")
    @Operation(summary = "Chấm công")
    public ResponseEntity<DefaultResponse<Boolean>> attendance(
            @RequestBody AttendanceRequest request) {
        attendanceService.timekeeping(request);
        return ResponseEntity.ok(DefaultResponse.success(Boolean.TRUE));
    }
}

