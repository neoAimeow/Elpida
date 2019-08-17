package com.aimeow.Elpida.controller;

import com.aimeow.Elpida.entity.*;
import com.aimeow.Elpida.entity.tushare.TuDailyStockEntity;
import com.aimeow.Elpida.entity.tushare.TuNewStockEntity;
import com.aimeow.Elpida.entity.tushare.TuStockBasicEntity;
import com.aimeow.Elpida.entity.tushare.TuStockListEntity;
import com.aimeow.Elpida.tools.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(value = "/stock")
public interface TuStockController {

    @RequestMapping(value = "/getTradeCalendar", method = RequestMethod.GET)
    /** 取得某一年所有的交易日期 */
    Result<List<TradeCalendarEntity>> getTradeCalendarWithYear(String year) throws Exception;

    @RequestMapping(value = "isDateCanTrade", method = RequestMethod.GET)
    /** 判断日期是否能交易 */
    Result<Boolean> isDateCanTrade(String tradeDate) throws Exception;

    @RequestMapping(value = "/getDailyStockData", method = RequestMethod.GET)
    /** 获得某一天所有股票的行情信息 yyyyMMdd */
    Result<List<TuDailyStockEntity>> getDailyStockWithTradeDate(String tradeDate) throws Exception;

    @RequestMapping(value = "/getNewStockData", method = RequestMethod.GET)
    Result<List<TuNewStockEntity>> getNewStockInfo(String startDate, String endDate) throws Exception;

    /** 获得某一天所有股票的基本信息 yyyyMMdd */
    @RequestMapping(value = "getBasicStockData", method = RequestMethod.GET)
    Result<List<TuStockBasicEntity>> getBasicStockDataWithTradeDate(String tradeDate) throws Exception;

    /** 获得某一天指数行情，目前有上证、深圳成指、创业板指、中小板指 yyyyMMdd */
    @RequestMapping(value = "getIndexStockData", method = RequestMethod.GET)
    Result<List<TuDailyStockEntity>> getIndexStockDataWithTradeDate(String tradeDate) throws Exception;

    @RequestMapping(value = "collectNewsFromTuShare", method = RequestMethod.GET)
    Result<List<NewsEntity>> collectNewsFromTuShare(String tradeDate) throws Exception;

    /** 获得某一天的新闻 */
    @RequestMapping(value = "getNews", method = RequestMethod.GET)
    Result<List<NewsEntity>> getNewsWithTradeDate(String tradeDate) throws Exception;

    @RequestMapping(value = "markNewsAsImportant", method = RequestMethod.GET)
    Result<Boolean> markNewsAsImportant(String id);

    /** 获得股东增减持 */
    @RequestMapping(value = "getHoldingSharesChange", method = RequestMethod.GET)
    Result<List<HoldingSharesEntity>> getHoldingShareChangeWithTradeDate(String tradeDate) throws Exception;

    /** 获得股票列表 */
    @RequestMapping(value = "/getStockListWithStatus", method = RequestMethod.GET)
    /**  L上市 D退市 P暂停上市 */
    Result<List<TuStockListEntity>> getUpdateStockListWithStatus(String status) throws Exception;

    /** 获得沪深港通资金流向 */
    @RequestMapping(value = "getMoneyFlow", method = RequestMethod.GET)
    Result<MoneyFlowEntity> getMoneyFlowWithTradeDate(String tradeDate) throws Exception;

    /** 通过交易日期获取那天的分析结果 */
    @RequestMapping(value = "getAnalysisResult", method = RequestMethod.GET)
    Result<AnalyzeEntity> getAnalysisResultWithTradeDate(String tradeDate) throws Exception;

    /** 分析某一天的数据并且记录到redis */
    @RequestMapping(value = "analyzeStockDataWithTradeData", method = RequestMethod.GET)
    Result<AnalyzeEntity> analyzeStockDataWithTradeData(String tradeDate) throws Exception;

    @RequestMapping(value = "test", method = RequestMethod.GET)
    String test() throws Exception;

    @RequestMapping(value = "test2", method = RequestMethod.GET)
    String test2() throws Exception;

//    @RequestMapping(value = "test2", method = RequestMethod.GET)
//    String test2() throws Exception;
}
