package com.khanh.timekeeping.services.salary;


import com.khanh.timekeeping.exceptions.RestAPIServerException;
import com.khanh.timekeeping.jobs.CalUserSalaryJob;
import com.khanh.timekeeping.jobs.listeners.CalUserSalaryListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.KeyMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {

    private final Scheduler scheduler;

    private void setupJobRecalculateSalary(Long userId) {
        log.info("Setup job để chạy tính lương lại cho nhân viên {}", userId);
        try {
            // Tạo job
            JobDetail jobDetail =
                    JobBuilder.newJob(CalUserSalaryJob.class)
                            // .storeDurably()
                            .withIdentity("JOB_Recalculate_Salary")
                            .withDescription("Job recalculate salary")
                            .usingJobData("user_id", userId)
                            .build();

            // Lên lịch job
            Date afterFiveMinutes =
                    Date.from(LocalDateTime.now().plusSeconds(10).atZone(ZoneId.systemDefault()).toInstant());
            Trigger trigger = TriggerBuilder.newTrigger().startAt(afterFiveMinutes).build();

            // Thêm listener cho job
            scheduler
                    .getListenerManager()
                    .addJobListener(
                            new CalUserSalaryListener(),
                            KeyMatcher.keyEquals(JobKey.jobKey("JOB_Recalculate_Salary")));

            // Đăng ký job, trigger vơí scheduler
            scheduler.scheduleJob(jobDetail, trigger);

            // Thực hiện scheduler
            scheduler.start();
        } catch (SchedulerException ex) {
            log.warn("Lỗi khi tính lại lương cho nhân viên {}", userId, ex);
        }
    }

    @Override
    public void calSalary() {
        log.info("Bắt đầu tính lương cho toàn thể nhân viên");
        Long userId = 1L;
        try {
            // TODO
            throw new RestAPIServerException(
                    "Lỗi xảy ra khi gọi API lấy thông tin thẻ phạt của nhân viên " + userId);
        } catch (RestAPIServerException ex) {
            // log.warn(ex.getMessage(), ex);

            setupJobRecalculateSalary(userId);
        }
    }

    @Override
    public void calSalary(Long userId) {
        log.info("Đang tính lương cho nhân viên {}", userId);
//    setupJobRecalculateSalary(1L);
    }
}
