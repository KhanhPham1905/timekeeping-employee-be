package com.khanh.timekeeping.consumers;


import com.khanh.timekeeping.configs.kafka.KafkaConsumerConfig;
import com.khanh.timekeeping.entities.Attendance;
import com.khanh.timekeeping.entities.Payslip;
import com.khanh.timekeeping.entities.User;
import com.khanh.timekeeping.entities.enums.AttendanceStatus;
import com.khanh.timekeeping.entities.enums.UserStatus;
import com.khanh.timekeeping.repositories.AttendanceRepository;
import com.khanh.timekeeping.repositories.PayslipRepository;
import com.khanh.timekeeping.repositories.UserRepository;
import com.khanh.timekeeping.utils.ConvertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class UserAttendanceConsumer {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final PayslipRepository payslipRepository;

    private void processValidateAccount(String message) {
        if (!StringUtils.hasText(message)) {
            return;
        }

        Attendance attendance = ConvertUtil.toObject(message, Attendance.class);
        if (Objects.isNull(attendance)) {
            return;
        }

        Long userId = attendance.getUserId();
        if (AttendanceStatus.INVALID == attendance.getStatus()) {

            log.info("Đang kiểm tra trạng thái tài khoản user {}", userId);
            List<Attendance> attendanceRecords =
                    attendanceRepository.findHistoryAttendances(userId, LocalDateTime.now(), 2);
            if (CollectionUtils.isEmpty(attendanceRecords) || attendanceRecords.size() < 2) {
                return;
            }
            boolean lockAccount =
                    attendanceRecords.stream()
                            .allMatch(record -> AttendanceStatus.INVALID == record.getStatus());
            if (lockAccount) {
                Optional<User> userOptional = userRepository.findById(userId);
                if (userOptional.isEmpty()) return;

                User user = userOptional.get();
                user.setStatus(UserStatus.LOCK);
                userRepository.save(user);
            }
        }

        log.info("Hoàn thành kiểm tra trạng thái tài khoản user {}", userId);
    }

    private void processCalSalary(String message) {
        if (!StringUtils.hasText(message)) {
            return;
        }

        Attendance attendance = ConvertUtil.toObject(message, Attendance.class);
        if (Objects.isNull(attendance)) {
            return;
        }

        if (AttendanceStatus.VALID != attendance.getStatus()) {
            return;
        }

        log.info("Đang thực hiện tính lương");
        LocalDate date = attendance.getCheckInTime().toLocalDate();

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMM");
        Integer payPeriod = Integer.valueOf(format.format(date));

        Long userId = attendance.getUserId();
        User user = userRepository.findById(userId).orElse(null);
        if (Objects.isNull(user)) {
            log.warn("Nhân viên không tồn tại trong hệ thống");
            return;
        }
        if (Objects.isNull(user.getDailyWage())) {
            log.warn("Thiếu thông tin thu nhập 1 ngày công");
            return;
        }

        Payslip payslip = payslipRepository.findFirstByUserIdAndPeriod(userId, payPeriod);
        if (Objects.isNull(payslip)) {
            payslip = Payslip.builder().userId(userId).period(payPeriod).createdBy(userId).build();
        }

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime startTime =
                LocalDateTime.of(
                        LocalDate.of(currentTime.getYear(), currentTime.getMonth(), 1), LocalTime.MIN);
        LocalDateTime endTime = currentTime.with(LocalTime.MAX);

        Long countWorkDay =
                attendanceRepository.countByUserIdAndCheckInTimeBetweenAndStatus(
                        userId, startTime, endTime, AttendanceStatus.VALID);
        BigDecimal grossAmount = user.getDailyWage().multiply(BigDecimal.valueOf(countWorkDay));
        payslip.setGrossAmount(grossAmount);
        payslip.setNetAmount(grossAmount.multiply(BigDecimal.valueOf(0.8)));
        payslip.setModifiedBy(userId);
        payslipRepository.save(payslip);

        log.info("Hoàn thành tính lương tài khoản user {}", userId);
    }

    //    @KafkaListener(containerFactory = KafkaConsumerConfig.SIMPLE_CONTAINER_FACTORY, topics =
    // "${spring.kafka.topics.user-attendance}", groupId = "erp-demo-account")
    //    public void validateAccount(String message) {
    //        processValidateAccount(message);
    //    }

    @KafkaListener(
            containerFactory = KafkaConsumerConfig.CONTAINER_FACTORY_CONFIG_BATCH,
            topics = "${spring.kafka.topics.user-attendance}",
            groupId = "erp-demo-account")
    public void validateAccount(List<String> messages) {
        log.info("Số lượng message xử lý {}", messages.size());
        messages.forEach(this::processValidateAccount);
    }

    //    @KafkaListener(containerFactory = KafkaConsumerConfig.SIMPLE_CONTAINER_FACTORY, topics =
    // "${spring.kafka.topics.user-attendance}", groupId = "erp-demo-salary")
    //    public void calSalary(String message) {
    //        processCalSalary(message);
    //    }

    @KafkaListener(
            containerFactory = KafkaConsumerConfig.CONTAINER_FACTORY_CONFIG_BATCH,
            topics = "${spring.kafka.topics.user-attendance}",
            groupId = "erp-demo-salary")
    public void calSalary(List<String> messages) {
        log.info("Số lượng message xử lý {}", messages.size());
        messages.forEach(this::processCalSalary);
    }
}
