package com.aimeow.Elpida.controller.impl;

import com.aimeow.Elpida.assembler.StockAssembler;
import com.aimeow.Elpida.controller.StockController;
import com.aimeow.Elpida.entity.*;
import com.aimeow.Elpida.tools.DateUtil;
import com.aimeow.Elpida.tools.RedisUtil;
import com.aimeow.Elpida.tools.Result;
import com.aimeow.Elpida.tools.ResultUtil;
import com.aimeow.Elpida.wrapper.StockRequestWrapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static com.aimeow.Elpida.tools.DateUtil.DATE_FORMAT_FULL;

@Component
public class StockControllerImpl implements StockController {

    private final static String TRADE_CAL_PRE = "trade_cal_";
    private final static String ANALYZE_STOCK_PRE = "analyze_stock_";

    @Autowired private StockRequestWrapper stockRequestWrapper;
    @Autowired private RedisUtil redisUtil;
    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public Result<List<TradeCalendarEntity>> getTradeCalendarWithYear(@NonNull String year) throws Exception {
        String startDateStr = year + "-01-01 00:00:00";
        String endDateStr = year + "-12-31 23:59:59";

        List<TradeCalendarEntity> tradeCalendarEntities = stockRequestWrapper.requestTradeCalendar(
                DateUtil.formatStringToDate(startDateStr,DATE_FORMAT_FULL),
                DateUtil.formatStringToDate(endDateStr,DATE_FORMAT_FULL)
        );

        return ResultUtil.buildSuccessResult(new Result<>(), tradeCalendarEntities);
    }

    @Override
    public Result<Boolean> isDateCanTrade(String tradeDate) throws Exception {
        return null;
    }

    @Override
    public Result<List<DailyStockEntity>> getDailyStockWithTradeDate(@NonNull String tradeDate) throws Exception {
        List<DailyStockEntity> dailyStockEntities = stockRequestWrapper.requestDailyStockInfoWithTradeDate(
                DateUtil.formatStringToDate(tradeDate, "yyyyMMdd"));
        Result<List<DailyStockEntity>> result = ResultUtil.buildSuccessResult(new Result<>(), dailyStockEntities);

        return result;
    }

    @Override
    public Result<List<StockBasicEntity>> getBasicStockDataWithTradeData(String tradeDate) throws Exception {
        List<StockBasicEntity> stockBasicEntities = stockRequestWrapper.requestBasicStockInfoWithTradeDate(
                DateUtil.formatStringToDate(tradeDate, "yyyyMMdd"));
        Result<List<StockBasicEntity>> result = ResultUtil.buildSuccessResult(new Result<>(), stockBasicEntities);
        return result;
    }

    @Override
    public Result<List<DailyStockEntity>> getIndexStockDataWithTradeData(String tradeDate) throws Exception {
        List<String> indexList = new ArrayList<>();
        indexList.add("000001.SH");
        indexList.add("399001.SZ");
        indexList.add("399006.SZ");
        indexList.add("399005.SZ");

        List<DailyStockEntity> dailyStockEntities = new ArrayList<>();

        indexList.parallelStream().forEach(
                obj -> {
                    try {
                        dailyStockEntities.add(stockRequestWrapper.requestIndexStockInfoWithCodeAndTradeDate(
                                obj, DateUtil.formatStringToDate("tradeDate", "yyyyMMdd")));
                    } catch (Exception ex) {
                        System.out.println("getIndexStockDataWithTradeDataError:" + ex.getMessage());
                    }
                }
        );
        return ResultUtil.buildSuccessResult(new Result<>(), dailyStockEntities);
    }

    @Override
    public Result<List<StockListEntity>> getUpdateStockListWithStatus(String status) throws Exception {
        List<StockListEntity> stockListEntities = stockRequestWrapper.requestStockList(status);
        Result<List<StockListEntity>> result = ResultUtil.buildSuccessResult(new Result<>(), stockListEntities);
        return result;
    }

    @Override
    public Result<AnalyzeEntity> analyzeStockDataWithTradeData(@NonNull String tradeDate) throws Exception {
        List<DailyStockEntity> stockEntityList = getDailyStockWithTradeDate(tradeDate).getModel();
        List<StockBasicEntity> stockBasicEntityList = getBasicStockDataWithTradeData(tradeDate).getModel();
        List<StockListEntity> stockListEntityList = getUpdateStockListWithStatus("L").getModel();

        List<FullStockEntity> fullStockEntityList = StockAssembler.assemblerStocks(stockBasicEntityList, stockEntityList, stockListEntityList);

        List<FullStockEntity> limitUpList = new ArrayList<>();
        List<FullStockEntity> limitDownList = new ArrayList<>();
        List<FullStockEntity> upList = new ArrayList<>();
        List<FullStockEntity> downList = new ArrayList<>();
        List<FullStockEntity> flatList = new ArrayList<>();
        List<FullStockEntity> topList = new ArrayList<>();
        List<FullStockEntity> explodeList = new ArrayList<>();

        AnalyzeEntity analyzeEntity = new AnalyzeEntity();
        analyzeEntity.setLimitUpStocks(limitUpList);
        analyzeEntity.setLimitDownStocks(limitDownList);
        analyzeEntity.setTopStocks(topList);
        analyzeEntity.setExplodeStocks(explodeList);

        for (FullStockEntity fullStockEntity : fullStockEntityList) {
            //涨幅大于0%;
            if (fullStockEntity.getChangeRate() > 0) {
                upList.add(fullStockEntity);
            }

            //跌幅大于0%
            if (fullStockEntity.getChangeRate() < 0) {
                downList.add(fullStockEntity);
            }

            //涨幅为0%
            if (fullStockEntity.getChangeRate() == 0) {
                flatList.add(fullStockEntity);
            }

            //涨幅大于9.8%
            if (fullStockEntity.getChangeRate() > 9.8) {
                limitUpList.add(fullStockEntity);

            }

            //跌幅大于9.8%
            if (fullStockEntity.getChangeRate() < - 9.8) {
                limitDownList.add(fullStockEntity);
            }

            if (fullStockEntity.getChangeRate() > 9.8 && fullStockEntity.getHighPrice().equals(fullStockEntity.getLowPrice())) {
                topList.add(fullStockEntity);
            }

            Float upShouldPrice = fullStockEntity.getPrePrice() * 1.098f;

            if (upShouldPrice > fullStockEntity.getClosePrice() && upShouldPrice < fullStockEntity.getHighPrice()) {
                explodeList.add(fullStockEntity);
            }

        }

        analyzeEntity.setLimitUpAmount(limitUpList.size());
        analyzeEntity.setLimitDownAmount(limitDownList.size());
        analyzeEntity.setTopAmount(topList.size());
        analyzeEntity.setExplodeAmount(explodeList.size());
        analyzeEntity.setUpAmount(upList.size());
        analyzeEntity.setDownAmount(downList.size());

        Result<AnalyzeEntity> analyzeEntityResult = ResultUtil.buildSuccessResult(new Result<>(),analyzeEntity);
        redisUtil.set(ANALYZE_STOCK_PRE + tradeDate, JSONObject.toJSONString(analyzeEntityResult, SerializerFeature.DisableCircularReferenceDetect));
        return analyzeEntityResult;
    }

    @Override
    public String test() throws Exception {
        List<String> yearList = new ArrayList<>();
        yearList.add("2008");
        yearList.add("2009");
        yearList.add("2010");
        yearList.add("2011");
        yearList.add("2012");
        yearList.add("2013");
        yearList.add("2014");
        yearList.add("2015");
        yearList.add("2016");
        yearList.add("2017");
        yearList.add("2018");
        yearList.add("2019");


        yearList.parallelStream().forEach(
                obj -> {
                    try {
                        List<TradeCalendarEntity> tradeCalendarEntities = getTradeCalendarWithYear(obj).getModel();
                        List<TradeCalendarEntity> canTraderEntities = new ArrayList<>();
                        for (TradeCalendarEntity tradeCalendarEntity : tradeCalendarEntities) {
                            if (tradeCalendarEntity.getIsOpen()) {
                                canTraderEntities.add(tradeCalendarEntity);
                            }
                        }

                        for (TradeCalendarEntity tradeCalendarEntity : canTraderEntities) {
                            String dateStr = DateUtil.formatDateToString(tradeCalendarEntity.getCalDate(), "yyyyMMdd");
                            System.out.println(dateStr);
                            analyzeStockDataWithTradeData(dateStr);
                        }
                    } catch (Exception ex) {
                        System.out.println("hello ex");
                    }

                }
        );

        return "success";
    }
}
