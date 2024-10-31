package com.khanh.timekeeping.jobs;


import com.khanh.timekeeping.services.salary.SalaryService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@NoArgsConstructor
public class CalUserSalaryJob implements Job {

    public static Long count = 1L;

    @Autowired
    SalaryService salaryService;

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        long userId = dataMap.getLong("user_id");
        if (userId <= 0) {
            log.warn("userId không hợp lệ");
        }

        log.info("Chạy job tính lương cho nhân viên {} lần {}", userId, count);
        count++;

        salaryService.calSalary(userId);
    }
}
