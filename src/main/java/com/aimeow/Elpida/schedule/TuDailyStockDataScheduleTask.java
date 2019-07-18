package com.aimeow.Elpida.schedule;

import com.aimeow.Elpida.controller.TuStockController;
import com.aimeow.Elpida.tools.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Configuration
@EnableScheduling
public class TuDailyStockDataScheduleTask {
    @Autowired private TuStockController tuStockController;

    @Scheduled(cron = "0 0 19 * * ?")
//    @Scheduled(cron = "0/20 * * * * ?")
    private void configureTasks() {
        try {
            tuStockController.analyzeStockDataWithTradeData(DateUtil.formatDateToString(new Date(), "yyyy-MM-dd"));
        } catch (Exception ex) {

        }
    }
}
