package com.aimeow.Elpida.schedule;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Configuration
@EnableScheduling
public class StockListScheduleTask {
    @Scheduled(cron = "0/5 * * * * ?")
    private void configureTasks() {
//        System.out.println("StockListScheduleTask " + new Date());
    }
}
