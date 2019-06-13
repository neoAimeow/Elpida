package com.aimeow.Elpida.controller;

import com.aimeow.Elpida.entity.DailyStockEntity;
import com.aimeow.Elpida.entity.StockListEntity;
import com.aimeow.Elpida.entity.TradeCalendarEntity;
import com.aimeow.Elpida.tools.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(value = "/stock")
public interface StockController {

    @RequestMapping(value = "/updateTradeCalendar", method = RequestMethod.GET)
    /** 某一年所有的交易日期从外部同步到内部redis */
    Result<List<TradeCalendarEntity>> updateTradeCalendarWithYear(String year) throws Exception;

    @RequestMapping(value = "/getTradeCalendar", method = RequestMethod.GET)
    /** 从redis中取得某一年所有的交易日期 */
    Result<List<TradeCalendarEntity>> getTradeCalendar(String year) throws Exception;

    @RequestMapping(value = "isDateCanTrade", method = RequestMethod.GET)
    /** 判断日期是否能交易 */
    Result<Boolean> isDateCanTrade(String tradeDate) throws Exception;

    @RequestMapping(value = "/recordDailyStock", method = RequestMethod.GET)
    /** 将某一天所有股票的行情信息存入mongodb dateFormat yyyyMMdd */
    Result<List<DailyStockEntity>> recordDailyStockWithTradeDate(String tradeDate) throws Exception;

//    Result<List>

    @RequestMapping(value = "/updateStockListWithStatus", method = RequestMethod.GET)
    /**  L上市 D退市 P暂停上市 */
    Result<List<StockListEntity>> updateStockListWithStatus(String status) throws Exception;
}
