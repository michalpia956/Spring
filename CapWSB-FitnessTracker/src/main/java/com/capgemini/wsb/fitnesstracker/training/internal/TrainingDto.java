package com.capgemini.wsb.fitnesstracker.training.internal;


import java.util.Date;

public record TrainingDto(Long userId, Date startTime, Date endTime, Double distance, Double averageSpeed, ActivityType activityType) {

}
