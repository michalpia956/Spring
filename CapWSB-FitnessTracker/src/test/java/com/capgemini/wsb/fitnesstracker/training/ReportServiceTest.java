package com.capgemini.wsb.fitnesstracker.training;

import com.capgemini.wsb.fitnesstracker.IntegrationTest;
import com.capgemini.wsb.fitnesstracker.IntegrationTestBase;
import com.capgemini.wsb.fitnesstracker.training.api.Training;
import com.capgemini.wsb.fitnesstracker.training.internal.ActivityType;
import com.capgemini.wsb.fitnesstracker.training.internal.TrainingServiceImpl;
import com.capgemini.wsb.fitnesstracker.user.api.User;
import com.capgemini.wsb.fitnesstracker.user.internal.WeeklyReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
public class ReportServiceTest extends IntegrationTestBase {

    @Autowired
    TrainingServiceImpl trainingService;
    @Autowired
    WeeklyReportService weeklyReportService;

    @Autowired
    private MockMvc mockMvc;

    //check whether DateIsInLastWeek method works properly
    @Test
    void testDateIsInLastWeek() {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime weekAgo = now.minusDays(8);
        Date date = Date.from(yesterday.atZone(ZoneId.systemDefault()).toInstant());

        assert(trainingService.DateIsInLastWeek(date));
        date = Date.from(weekAgo.atZone(ZoneId.systemDefault()).toInstant());
        assert(!trainingService.DateIsInLastWeek(date));
    }

    //check whether convertToLocalDateViaInstant method works properly
    @Test
    void testConvertToLocalDateViaInstant() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Date date = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        assert(now.toLocalDate().isEqual(trainingService.convertToLocalDateViaInstant(date)));
    }

    @Test
    void shouldGiveProperReport_whenGettingWeeklyReport() {
        // given
        User user1 = existingUser(generateClient());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDate = yesterday.format(formatter);
        System.out.println(formattedDate);
        String requestBody = """
                {
                "userId": "%s",
                "startTime": "%s",
                "endTime": "%s",
                "activityType": "TENNIS",
                "distance": 0.0,
                "averageSpeed": 0.0
                }
                """.formatted(user1.getId(), formattedDate, now.format(formatter));
        try{
            mockMvc.perform(post("/v1/trainings").contentType(MediaType.APPLICATION_JSON).content(requestBody));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // when
        String report = weeklyReportService.generateReport(user1);
        // then
        assert report.contains("1 training");
    }
    private static User generateClient() {
        return new User(randomUUID().toString(), randomUUID().toString(), now(), randomUUID().toString());
    }
}
