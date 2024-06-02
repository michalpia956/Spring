package com.capgemini.wsb.fitnesstracker.user.internal;

import com.capgemini.wsb.fitnesstracker.mail.api.EmailDto;
import com.capgemini.wsb.fitnesstracker.mail.internal.EmailServiceImpl;
import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.training.internal.TrainingServiceImpl;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@EnableScheduling
@RequiredArgsConstructor
@Service
public class WeeklyReportService {
    private final EmailServiceImpl emailSender;

    private final UserServiceImpl userService;

    private final TrainingServiceImpl trainingService;

    public String generateReport(User user) {
        List<Training> trainings = trainingService.getTrainingsFromLastWeek(user);
        String plurar = trainings.size() == 1 ? "" : "s";
        return "Hello " + user.getFirstName() + " " + user.getLastName() + "\n\nYou have done " +
                trainings.size() + String.format(" training{%s} in the last week\n\nKeep up the good work!", plurar);

    }
    @Scheduled(cron = "0 0 0 * * MON")
    public void sendWeeklyReport() {
        userService.findAllUsers().forEach(user -> {
            emailSender.send(new EmailDto(user.getEmail(), "Weekly report", generateReport(user)));
        });
    }
}
