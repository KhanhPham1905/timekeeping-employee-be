package com.khanh.timekeeping.schedulers;

import com.khanh.timekeeping.services.salary.SalaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CalSalaryScheduler {

    private final SalaryService salaryService;

    @Scheduled(initialDelay = 5000L, fixedDelay = Long.MAX_VALUE)
    @SchedulerLock(
            name = "TaskScheduler_scheduledTask",
            lockAtLeastFor = "PT10S",
            lockAtMostFor = "PT30S")
    public void calSalaryScheduler() {
        salaryService.calSalary();
    }
}
