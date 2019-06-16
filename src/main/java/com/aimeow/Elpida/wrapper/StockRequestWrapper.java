package com.aimeow.Elpida.wrapper;

import com.aimeow.Elpida.entity.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public interface StockRequestWrapper {

    /** 股票列表,获取基础信息数据，包括股票代码、名称、上市日期、退市日期等 */
    List<StockListEntity> requestStockList(String status) throws Exception;

    /** 通过交易时间来获取当时所有股票的日线行情 */
    List<DailyStockEntity> requestDailyStockInfoWithTradeDate(Date tradeDate) throws Exception;

    /** 通过交易时间来 获取当时所有股票的基础信息数据 */
    List<StockBasicEntity> requestBasicStockInfoWithTradeDate(Date tradeDate) throws Exception;

    /** 通过指数代码与交易时间来获取指数数据 */
    DailyStockEntity requestIndexStockInfoWithCodeAndTradeDate(String tsCode, Date tradeDate) throws Exception;

    /** 通过开始时间与结束时间来获取新闻，src包含sina、wallstreetcn、10jqka、eastmoney、yuncaijing */
    List<NewsEntity> requestNewsWithDate(Date startDate, Date endDate, String src) throws Exception;

    /** 增减持 */
    List<HoldingSharesEntity> requestHoldingSharesChangeWithTradeDate(Date tradeDate) throws Exception;

    /** 获取沪深港通资金流向 */
    MoneyFlowEntity requestMoneyFlowWithTradeDate(Date tradeDate) throws Exception;

    /** 获取各大交易所交易日历数据,默认提取的是上交所 */
    List<TradeCalendarEntity> requestTradeCalendar(Date startDate, Date endDate) throws Exception;

}
