package com.aimeow.Elpida.controller;

import com.aimeow.Elpida.entity.*;
import com.aimeow.Elpida.tools.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(value = "/stock")
public interface StockController {

    @RequestMapping(value = "/getTradeCalendar", method = RequestMethod.GET)
    /** 取得某一年所有的交易日期 */
    Result<List<TradeCalendarEntity>> getTradeCalendarWithYear(String year) throws Exception;

    @RequestMapping(value = "isDateCanTrade", method = RequestMethod.GET)
    /** 判断日期是否能交易 */
    Result<Boolean> isDateCanTrade(String tradeDate) throws Exception;

    @RequestMapping(value = "/getDailyStockData", method = RequestMethod.GET)
    /** 获得某一天所有股票的行情信息 yyyyMMdd */
    Result<List<DailyStockEntity>> getDailyStockWithTradeDate(String tradeDate) throws Exception;

    /** 获得某一天所有股票的基本信息 yyyyMMdd */
    @RequestMapping(value = "getBasicStockData", method = RequestMethod.GET)
    Result<List<StockBasicEntity>> getBasicStockDataWithTradeData(String tradeDate) throws Exception;

    @RequestMapping(value = "/getStockListWithStatus", method = RequestMethod.GET)
    /**  L上市 D退市 P暂停上市 */
    Result<List<StockListEntity>> getUpdateStockListWithStatus(String status) throws Exception;

    @RequestMapping(value = "analyzeStockDataWithTradeData", method = RequestMethod.GET)
    Result<AnalyzeEntity> analyzeStockDataWithTradeData(String tradeDate) throws Exception;

    @RequestMapping(value = "test", method = RequestMethod.GET)
    String test() throws Exception;
}
