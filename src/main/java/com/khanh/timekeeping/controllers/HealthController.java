package com.khanh.timekeeping.controllers;


import com.khanh.timekeeping.responses.DefaultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping("/ping")
    public ResponseEntity<DefaultResponse<String>> ping() {
        return ResponseEntity.ok(DefaultResponse.success("pong"));
    }

}
