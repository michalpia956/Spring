package com.capgemini.wsb.fitnesstracker.training.internal;

import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.training.api.TrainingProvider;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.api.UserProvider;
import com.capgemini.wsb.fitnesstracker.user.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/v1/trainings")
@RequiredArgsConstructor

public class TrainingController {

    private final TrainingServiceImpl trainingService;

    private final TrainingMapper trainingMapper;

    private final TrainingProvider trainingProvider;

    private final TrainingRepository trainingRepository;

    private final UserProvider userProvider;

    @PostMapping
    public ResponseEntity<Training> addTraining(@RequestBody TrainingDto trainingDto){
        return new ResponseEntity<>(trainingService.addTraining(trainingMapper.toEntity(trainingDto)), CREATED);
    }
    //get all trainings
    @GetMapping
    public List<TrainingDto> getAllTrainings() {
        return trainingRepository.findAll()
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    //get training of one user
    @GetMapping("/{userId}")
    public List<TrainingDto> getTrainingByUserId(@PathVariable Long userId) {
        User user = userProvider.getUser(userId).orElseThrow();
        return trainingRepository.findAll()
                .stream()
                .filter(training -> training.getUser().equals(user))
                .map(trainingMapper::toDto)
                .toList();
    }

    //get training finished after time
    @GetMapping("/finished/{afterTime}")
    public List<TrainingDto> getTrainingFinishedAfter(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date afterTime) {
        return trainingRepository.findAll()
                .stream()
                .filter(training -> training.getEndTime().after(afterTime))
                .map(trainingMapper::toDto)
                .toList();
    }

    //get trainings by activity type
    @GetMapping("/activityType")
    public List<TrainingDto> getTrainingsByActivityType(@RequestParam("activityType") ActivityType activityType){
        return trainingRepository.findAll()
                .stream()
                .filter(training -> training.getActivityType().equals(activityType))
                .map(trainingMapper::toDto)
                .toList();
    }

    @PutMapping("/{trainingId}")
    public ResponseEntity<Training> updateTraining(@PathVariable Long trainingId, @RequestBody TrainingDto trainingDto){
        Training training = trainingProvider.getTraining(trainingId).orElseThrow();
        training.setActivityType(trainingDto.activityType());
        training.setAverageSpeed(trainingDto.averageSpeed());
        training.setDistance(trainingDto.distance());
        training.setEndTime(trainingDto.endTime());
        training.setStartTime(trainingDto.startTime());
        return new ResponseEntity<>(trainingRepository.save(training), OK);
    }





}
