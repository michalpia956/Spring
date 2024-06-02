package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.user.api.UserProvider;
import com.capgemini.wsb.fitnesstracker.user.api.UserService;
import com.capgemini.wsb.fitnesstracker.user.internal.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
public class TrainingMapper {
    private final UserProvider userProvider;
    private final UserMapper userMapper;
    public Training toEntity(TrainingDto trainingDto) {
        if(userProvider.getUser(trainingDto.userId()).isEmpty()) {
            throw new IllegalArgumentException("User with id " + trainingDto.userId() + " does not exist");
        }
        return new Training(userProvider.getUser(trainingDto.userId()).get(),
                            trainingDto.startTime(),
                            trainingDto.endTime(),
                            trainingDto.activityType(),
                            trainingDto.distance(),
                            trainingDto.averageSpeed()
                    );
    }

    TrainingDto toDto(Training training) {
        return new TrainingDto(training.getUser().getId(),
                               training.getStartTime(),
                               training.getEndTime(),
                               training.getDistance(),
                               training.getAverageSpeed(),
                               training.getActivityType());
    }

}
