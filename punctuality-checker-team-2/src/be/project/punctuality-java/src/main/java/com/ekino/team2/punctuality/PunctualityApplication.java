package com.ekino.team2.punctuality;

import com.ekino.team2.punctuality.service.DailyReport;
import com.ekino.team2.punctuality.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling

public class PunctualityApplication {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DailyReport dailyReport;

    public static void main(String[] args) {
        SpringApplication.run(PunctualityApplication.class, args);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void updateEmployee() {
        employeeService.updateEmployee();
    }

    @Scheduled(cron = "0 15 12,17 * * ?")
    public void sendEmailDaily() {
        LocalDateTime localDateTime = LocalDateTime.now();
        dailyReport.fingerPrintDaily(localDateTime.minusDays(30));
    }


}
