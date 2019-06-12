package com.aimeow.Elpida.wrapper;

import com.aimeow.Elpida.entity.DailyStockEntity;
import com.aimeow.Elpida.entity.StockListEntity;
import com.aimeow.Elpida.entity.TradeCalendarEntity;
import com.aimeow.Elpida.request.GetTradeCalendarRequest;
import com.aimeow.Elpida.request.StockListRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/service")
public interface StockRequestWrapper {

    /** 股票列表,获取基础信息数据，包括股票代码、名称、上市日期、退市日期等 */
    List<StockListEntity> requestStockList(StockListRequest stockListRequest);

    /** 通过交易时间来获取当时所有股票的日线行情 */
    List<DailyStockEntity> requestDailyStockInfoWithTradeDate(Date tradeDate);

    /** 获取各大交易所交易日历数据,默认提取的是上交所 */
    @RequestMapping(value = "/getTradeCalendar", method = RequestMethod.GET)
    List<TradeCalendarEntity> requestTradeCalendar(GetTradeCalendarRequest getTradeCalendarRequest);

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    String test() ;

}
