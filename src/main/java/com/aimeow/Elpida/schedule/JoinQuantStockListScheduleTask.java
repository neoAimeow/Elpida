package com.aimeow.Elpida.schedule;

import com.aimeow.Elpida.controller.JoinQuantStockController;
import com.aimeow.Elpida.controller.TuStockController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Configuration
@EnableScheduling
public class JoinQuantStockListScheduleTask {
    @Autowired private JoinQuantStockController joinQuantStockController;

    //每天早上8点触发
    @Scheduled(cron = "0 0 8 * * ?")
//    @Scheduled(cron = "0/10 * * * * ?")
    private void configureTasks() {
        try {
            joinQuantStockController.joinQuantGetAllSecurities("stock");
        } catch (Exception ex) {

        }
    }
}
