package com.khanh.timekeeping.jobs.listeners;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

public class CalUserSalaryListener extends JobListenerSupport {
    @Override
    public String getName() {
        return "CalUserSalaryListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        getLog().info("Job {} chuẩn bị được thực hiện", context.getJobDetail().getKey());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        getLog().info("Job {} không được thực hiện", context.getJobDetail().getKey());
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        getLog().info("Job {} đã hoàn thành", context.getJobDetail().getKey());
    }
}
