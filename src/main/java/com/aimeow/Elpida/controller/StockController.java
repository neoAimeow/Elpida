package com.aimeow.Elpida.controller;

import com.aimeow.Elpida.entity.DailyStockEntity;
import com.aimeow.Elpida.entity.StockListEntity;
import com.aimeow.Elpida.entity.TradeCalendarEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(value = "/stock")
public interface StockController {

    @RequestMapping(value = "/updateTradeCalendar", method = RequestMethod.GET)
    List<TradeCalendarEntity> updateTradeCalendarWithYear(String year) throws Exception;

    @RequestMapping(value = "/recordDailyStock", method = RequestMethod.GET)
    /** dateFormat yyyyMMdd */
    List<DailyStockEntity> recordDailyStockWithTradeDate(String tradeDate) throws Exception;

    @RequestMapping(value = "/updateStockListWithStatus", method = RequestMethod.GET)
    /**  L上市 D退市 P暂停上市 */
    List<StockListEntity> updateStockListWithStatus(String status) throws Exception;
}
