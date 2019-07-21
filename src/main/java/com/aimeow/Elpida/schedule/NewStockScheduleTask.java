package com.aimeow.Elpida.schedule;

import com.aimeow.Elpida.controller.JoinQuantStockController;
import com.aimeow.Elpida.controller.TuStockController;
import com.aimeow.Elpida.tools.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.aimeow.Elpida.tools.DateUtil.DATE_FORMAT_YMD;

@Component
@Configuration
@EnableScheduling
public class NewStockScheduleTask {
    @Autowired
    private TuStockController tuStockController;

    //每天早上8点触发
    @Scheduled(cron = "0 0 8 * * ?")
//    @Scheduled(cron = "0/10 * * * * ?")
    private void configureTasks() {
        try {
            Date dateAfter = new Date();
            String dateAfterStr = DateUtil.formatDateToString(dateAfter, DATE_FORMAT_YMD);
            String beforeDateStr = DateUtil.getCalculateDateToString(dateAfterStr, -20);
            Date dateBefore = DateUtil.formatStringToDate(beforeDateStr, DATE_FORMAT_YMD);

            tuStockController.getNewStockInfo(DateUtil.formatDateToString(dateBefore, "yyyyMMdd"), DateUtil.formatDateToString(dateAfter, "yyyyMMdd"));
        } catch (Exception ex) {

        }
    }
}
