package com.khanh.timekeeping.services.attendance;


import com.khanh.timekeeping.configs.kafka.KafkaTopicConfig;
import com.khanh.timekeeping.entities.Attendance;
import com.khanh.timekeeping.entities.User;
import com.khanh.timekeeping.entities.enums.AttendanceStatus;
import com.khanh.timekeeping.entities.enums.UserStatus;
import com.khanh.timekeeping.exceptions.ERPRuntimeException;
import com.khanh.timekeeping.repositories.AttendanceRepository;
import com.khanh.timekeeping.requests.AttendanceRequest;
import com.khanh.timekeeping.services.user.UserService;
import com.khanh.timekeeping.utils.ConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final UserService userService;
    private final AttendanceRepository attendanceRepository;
    private final KafkaTopicConfig kafkaTopicConfig;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void timekeeping(AttendanceRequest request) {
        User user = userService.getUser(request.getUsername());
        if (UserStatus.ACTIVE != user.getStatus()) {
            throw new ERPRuntimeException("Trạng thái của người dùng không hợp lệ");
        }

        Long userId = user.getId();

        LocalDateTime time = request.getTime();

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime startDate = currentDate.with(LocalTime.MIN);
        LocalDateTime endDate = currentDate.with(LocalTime.MAX);
        Attendance attendance =
                attendanceRepository.findFirstByUserIdAndCheckInTimeBetween(userId, startDate, endDate);
        if (Objects.isNull(attendance)) {
            attendance =
                    Attendance.builder()
                            .userId(userId)
                            .status(AttendanceStatus.RECORDING)
                            .createdBy(userId)
                            .build();
        }
        AttendanceRequest.AttendanceType type = request.getType();
        if (AttendanceRequest.AttendanceType.CHECKIN == type) {
            if (Objects.nonNull(attendance.getCheckInTime())) {
                throw new ERPRuntimeException("Người dùng đã thực hiện checkin");
            } else {
                attendance.setCheckInTime(time);
            }
        } else if (AttendanceRequest.AttendanceType.CHECKOUT == type) {
            if (Objects.nonNull(attendance.getCheckOutTime())) {
                throw new ERPRuntimeException("Người dùng đã thực hiện checkout");
            } else {
                attendance.setCheckOutTime(time);
            }
        }
        attendance.setStatus(getAttendanceRecordStatus(attendance));
        attendance.setModifiedBy(userId);
        attendanceRepository.save(attendance);

        String message = ConvertUtil.toString(attendance);
        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(
                        kafkaTopicConfig.getUserAttendanceTopic(),
                        String.valueOf(userId),
                        message);
        future.whenComplete(
                (result, ex) -> {
                    if (Objects.isNull(ex)) {
                        // TODO - Send message success
                    } else {
                        log.error(
                                "Error occur when send message {} to kafka - Detail {}", message, ex.getMessage());
                    }
                });

        log.info("Người dùng {} đã thực hiện {} xong", user.getUsername(), request.getType());
    }

    private AttendanceStatus getAttendanceRecordStatus(Attendance attendance) {
        LocalDateTime checkinTime = attendance.getCheckInTime();
        LocalDateTime checkoutTime = attendance.getCheckOutTime();

        if (Objects.isNull(checkinTime) && Objects.isNull(checkoutTime)) {
            return AttendanceStatus.INVALID;
        }
        if (Objects.isNull(checkinTime) || Objects.isNull(checkoutTime)) {
            return AttendanceStatus.RECORDING;
        }
        if (!checkinTime.isBefore(checkoutTime)) {
            return AttendanceStatus.INVALID;
        }
        if (ChronoUnit.HOURS.between(checkinTime, checkoutTime) < 6) {
            return AttendanceStatus.INVALID;
        }
        return AttendanceStatus.VALID;
    }
}

