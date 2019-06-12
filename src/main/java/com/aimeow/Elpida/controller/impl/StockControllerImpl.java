package com.aimeow.Elpida.controller.impl;

import com.aimeow.Elpida.controller.StockController;
import com.aimeow.Elpida.entity.DailyStockEntity;
import com.aimeow.Elpida.entity.StockListEntity;
import com.aimeow.Elpida.entity.TradeCalendarEntity;
import com.aimeow.Elpida.request.GetTradeCalendarRequest;
import com.aimeow.Elpida.tools.DateUtil;
import com.aimeow.Elpida.tools.RedisUtil;
import com.aimeow.Elpida.wrapper.StockRequestWrapper;
import com.alibaba.fastjson.JSONArray;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

import static com.aimeow.Elpida.tools.DateUtil.DATE_FORMAT_FULL;

@Component
public class StockControllerImpl implements StockController {

    private final static String TRADE_CAL_PRE = "trade_cal_";
    private final static String STOCK_LIST_PRE = "stock_list_";

    @Autowired private StockRequestWrapper stockRequestWrapper;
    @Autowired private RedisUtil redisUtil;

    @Override
    public List<TradeCalendarEntity> updateTradeCalendarWithYear(@NonNull String year) throws Exception {
        GetTradeCalendarRequest getTradeCalendarRequest = new GetTradeCalendarRequest();
        String startDateStr = year + "-01-01 00:00:00";
        String endDateStr = year + "-12-31 23:59:59";
        getTradeCalendarRequest.setStartDate(DateUtil.formatStringToDate(startDateStr,DATE_FORMAT_FULL));
        getTradeCalendarRequest.setEndDate(DateUtil.formatStringToDate(endDateStr,DATE_FORMAT_FULL));

        List<TradeCalendarEntity> tradeCalendarEntities = stockRequestWrapper.requestTradeCalendar(
                DateUtil.formatStringToDate(startDateStr,DATE_FORMAT_FULL),
                DateUtil.formatStringToDate(endDateStr,DATE_FORMAT_FULL)
        );

        redisUtil.set(TRADE_CAL_PRE + year, JSONArray.toJSONString(tradeCalendarEntities));

        return tradeCalendarEntities;
    }

    @Override
    public List<DailyStockEntity> recordDailyStockWithTradeDate(@NonNull String tradeDate) throws Exception {
        return null;
    }

    @Override
    public List<StockListEntity> updateStockListWithStatus(@NonNull String status) throws Exception {
        List<StockListEntity> stockListEntities = stockRequestWrapper.requestStockList(status);
        redisUtil.set(STOCK_LIST_PRE + status, JSONArray.toJSONString(stockListEntities));
        return stockListEntities;
    }
}
