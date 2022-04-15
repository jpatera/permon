package com.example.application.data.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;

@Entity
public class Job extends AbstractEntity {

    private Integer jobNo;
    private String jobName;
    private LocalDateTime startDate;
    private LocalDateTime duration;

    public Integer getJobNo() {
        return jobNo;
    }
    public void setJobNo(Integer jobNo) {
        this.jobNo = jobNo;
    }
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public LocalDateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    public LocalDateTime getDuration() {
        return duration;
    }
    public void setDuration(LocalDateTime duration) {
        this.duration = duration;
    }

}
