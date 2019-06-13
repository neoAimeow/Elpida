package com.aimeow.Elpida.controller.impl;

import com.aimeow.Elpida.controller.StockController;
import com.aimeow.Elpida.entity.DailyStockEntity;
import com.aimeow.Elpida.entity.StockListEntity;
import com.aimeow.Elpida.entity.TradeCalendarEntity;
import com.aimeow.Elpida.tools.DateUtil;
import com.aimeow.Elpida.tools.RedisUtil;
import com.aimeow.Elpida.tools.Result;
import com.aimeow.Elpida.tools.ResultUtil;
import com.aimeow.Elpida.wrapper.StockRequestWrapper;
import com.alibaba.fastjson.JSONArray;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public Result<List<TradeCalendarEntity>> updateTradeCalendarWithYear(@NonNull String year) throws Exception {
        String startDateStr = year + "-01-01 00:00:00";
        String endDateStr = year + "-12-31 23:59:59";

        List<TradeCalendarEntity> tradeCalendarEntities = stockRequestWrapper.requestTradeCalendar(
                DateUtil.formatStringToDate(startDateStr,DATE_FORMAT_FULL),
                DateUtil.formatStringToDate(endDateStr,DATE_FORMAT_FULL)
        );

        Result<List<TradeCalendarEntity>> result = ResultUtil.buildSuccessResult(new Result<>(), tradeCalendarEntities);
        redisUtil.set(TRADE_CAL_PRE + year, JSONArray.toJSONString(result));

        return result;
    }

    @Override
    public Result<List<DailyStockEntity>> recordDailyStockWithTradeDate(@NonNull String tradeDate) throws Exception {
        List<DailyStockEntity> dailyStockEntities = stockRequestWrapper.requestDailyStockInfoWithTradeDate(
                DateUtil.formatStringToDate(tradeDate, "yyyyMMdd"));
        Result<List<DailyStockEntity>> result = ResultUtil.buildSuccessResult(new Result<>(), dailyStockEntities);

        for (DailyStockEntity dailyStockEntity : dailyStockEntities) {
            mongoTemplate.save(dailyStockEntity);
        }
        return result;
    }

    @Override
    public Result<List<StockListEntity>> updateStockListWithStatus(@NonNull String status) throws Exception {
        List<StockListEntity> stockListEntities = stockRequestWrapper.requestStockList(status);
        Result<List<StockListEntity>> result = ResultUtil.buildSuccessResult(new Result<>(), stockListEntities);

        redisUtil.set(STOCK_LIST_PRE + status, JSONArray.toJSONString(result));
        return result;
    }
}
