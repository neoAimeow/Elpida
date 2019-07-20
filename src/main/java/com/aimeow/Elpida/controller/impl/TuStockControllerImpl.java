package com.aimeow.Elpida.controller.impl;

import com.aimeow.Elpida.assembler.StockAssembler;
import com.aimeow.Elpida.controller.TuStockController;
import com.aimeow.Elpida.entity.*;
import com.aimeow.Elpida.entity.tushare.*;
import com.aimeow.Elpida.tools.DateUtil;
import com.aimeow.Elpida.tools.RedisUtil;
import com.aimeow.Elpida.tools.Result;
import com.aimeow.Elpida.tools.ResultUtil;
import com.aimeow.Elpida.wrapper.StockRequestWrapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.aimeow.Elpida.tools.DateUtil.DATE_FORMAT_FULL;
import static com.aimeow.Elpida.tools.DateUtil.DATE_FORMAT_YMD;

@Component
public class TuStockControllerImpl implements TuStockController {

    private final static String TRADE_CAL_PRE = "trade_cal_";
    private final static String ANALYZE_STOCK_PRE = "analyze_stock_";

    @Autowired private StockRequestWrapper stockRequestWrapper;
    @Autowired private RedisUtil redisUtil;
    @Autowired private MongoTemplate mongoTemplate;

    @Override
    public Result<List<TradeCalendarEntity>> getTradeCalendarWithYear(@NonNull String year) throws Exception {
        String startDateStr = year + "-01-01 00:00:00";
        String endDateStr = year + "-12-31 23:59:59";

        List<TradeCalendarEntity> tradeCalendarEntities = stockRequestWrapper.tuRequestTradeCalendar(
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
    public Result<List<TuDailyStockEntity>> getDailyStockWithTradeDate(@NonNull String tradeDate) throws Exception {
        List<TuDailyStockEntity> dailyStockEntities = stockRequestWrapper.tuRequestDailyStockInfoWithTradeDate(
                DateUtil.formatStringToDate(tradeDate, "yyyyMMdd"));
        Result<List<TuDailyStockEntity>> result = ResultUtil.buildSuccessResult(new Result<>(), dailyStockEntities);

        return result;
    }

    @Override
    public Result<List<TuStockBasicEntity>> getBasicStockDataWithTradeDate(String tradeDate) throws Exception {
        List<TuStockBasicEntity> stockBasicEntities = stockRequestWrapper.tuRequestBasicStockInfoWithTradeDate(
                DateUtil.formatStringToDate(tradeDate, "yyyyMMdd"));
        Result<List<TuStockBasicEntity>> result = ResultUtil.buildSuccessResult(new Result<>(), stockBasicEntities);
        return result;
    }

    @Override
    public Result<List<TuDailyStockEntity>> getIndexStockDataWithTradeDate(String tradeDate) throws Exception {
        List<String> indexList = new ArrayList<>();
        indexList.add("000001.SH");
        indexList.add("399001.SZ");
        indexList.add("399005.SZ");
        indexList.add("399006.SZ");
        List<TuDailyStockEntity> dailyStockEntities = new ArrayList<>();

        indexList.stream().forEach(
                obj -> {
                    try {
                        dailyStockEntities.add(stockRequestWrapper.tuRequestIndexStockInfoWithCodeAndTradeDate(
                                obj, DateUtil.formatStringToDate(tradeDate, "yyyyMMdd")));
                    } catch (Exception ex) {
                        System.out.println("getIndexStockDataWithTradeDataError:" + ex.getMessage());
                    }
                }
        );
        return ResultUtil.buildSuccessResult(new Result<>(), dailyStockEntities);
    }

    @Override
    public Result<List<NewsEntity>> getNewsWithTradeDate(String tradeDate) throws Exception {
        List<String> srcList = new ArrayList<>();
        srcList.add("sina");
        srcList.add("wallstreetcn");
        srcList.add("10jqka");
        srcList.add("eastmoney");
        srcList.add("yuncaijing");

        List<NewsEntity> newsEntities = new ArrayList<>();

        srcList.parallelStream().forEach(
                obj -> {
                    try {
                        Integer tomorrow = Integer.valueOf(tradeDate) + 1;
                        newsEntities.addAll(stockRequestWrapper.tuRequestNewsWithDate(
                                DateUtil.formatStringToDate(tradeDate, "yyyyMMdd"),
                                DateUtil.formatStringToDate(tomorrow.toString(), "yyyyMMdd"), obj));
                    } catch (Exception ex) {
                        System.out.println("getNewsWithTradeData:" + ex.getMessage());
                    }
                }
        );

        Comparator<NewsEntity> comparator = new Comparator<NewsEntity>() {
            @Override
            public int compare(NewsEntity o1, NewsEntity o2) {
                if (o1 == null || o2 == null) {
                    return 0;
                } else {
                    return o1.getDateTime().compareTo(o2.getDateTime());
                }
            }
        };

        Collections.sort(newsEntities, comparator);

        return ResultUtil.buildSuccessResult(new Result<>(), newsEntities);
    }

    @Override
    public Result<List<HoldingSharesEntity>> getHoldingShareChangeWithTradeDate(String tradeDate) throws Exception {
        List<HoldingSharesEntity> holdingSharesEntities = stockRequestWrapper.tuRequestHoldingSharesChangeWithTradeDate(
                DateUtil.formatStringToDate(tradeDate, "yyyyMMdd"));

        return ResultUtil.buildSuccessResult(new Result<>(), holdingSharesEntities);
    }

    @Override
    public Result<List<TuStockListEntity>> getUpdateStockListWithStatus(String status) throws Exception {
        List<TuStockListEntity> stockListEntities = stockRequestWrapper.tuRequestStockList(status);
        Result<List<TuStockListEntity>> result = ResultUtil.buildSuccessResult(new Result<>(), stockListEntities);
        return result;
    }

    @Override
    public Result<MoneyFlowEntity> getMoneyFlowWithTradeDate(String tradeDate) throws Exception {
        return ResultUtil.buildSuccessResult(new Result<>(), stockRequestWrapper.tuRequestMoneyFlowWithTradeDate(
                DateUtil.formatStringToDate(tradeDate, "yyyyMMdd")));
    }

    @Override
    public Result<AnalyzeEntity> getAnalysisResultWithTradeDate(String tradeDate) throws Exception {
        String dataStr = redisUtil.get(ANALYZE_STOCK_PRE + tradeDate);
        JSONObject jsonObject = JSONObject.parseObject(dataStr);
        JSONObject model = jsonObject.getJSONObject("model");
        AnalyzeEntity analyzeEntity = JSONObject.parseObject(JSON.toJSONString(model), AnalyzeEntity.class);

        return ResultUtil.buildSuccessResult(new Result<>(), analyzeEntity);
    }

    @Override
    public Result<List<AnalyzeEntity>> getLastTwentyDaysAnalysisResultWithTradeDate(String tradeDate) throws Exception {
        Date dateAfter = DateUtil.formatStringToDate(tradeDate, "yyyyMMdd");
        String dateAfterStr = DateUtil.formatDateToString(dateAfter, DATE_FORMAT_YMD);
        String beforeDateStr = DateUtil.getCalculateDateToString(dateAfterStr, -20);
        Date dateBefore = DateUtil.formatStringToDate(beforeDateStr, DATE_FORMAT_YMD);

        List<AnalyzeEntity> analyzeEntities = new ArrayList<>();

        List<TradeCalendarEntity> tradeCalendarEntities = stockRequestWrapper.tuRequestTradeCalendar(dateBefore, dateAfter);
        tradeCalendarEntities.parallelStream().forEach(
                obj -> {
                    try {
                        if (obj.getIsOpen()) {
                            AnalyzeEntity analyzeEntity = getAnalysisResultWithTradeDate(
                                    DateUtil.formatDateToString(obj.getCalDate(), "yyyyMMdd")).getModel();
                            analyzeEntities.add(analyzeEntity);
                        }
                    } catch (Exception ex) {
                        System.out.println("getLastTwentyDaysAnalysisResultError" + ex.getMessage());
                    }
                }
        );

        Comparator<AnalyzeEntity> comparator = new Comparator<AnalyzeEntity>() {
            @Override
            public int compare(AnalyzeEntity o1, AnalyzeEntity o2) {
                if (o1.getTradeDate().before(o2.getTradeDate())) {
                    return 1;
                } else {
                    return -1;
                }
            }
        };

        Collections.sort(analyzeEntities, comparator);

        return ResultUtil.buildSuccessResult(new Result<>(), analyzeEntities);
    }

    @Override
    public Result<AnalyzeEntity> analyzeStockDataWithTradeData(@NonNull String tradeDate) throws Exception {
        List<TuDailyStockEntity> stockEntityList = getDailyStockWithTradeDate(tradeDate).getModel();
        List<TuStockBasicEntity> tuStockBasicEntityList = getBasicStockDataWithTradeDate(tradeDate).getModel();
        List<TuStockListEntity> tuStockListEntityList = getUpdateStockListWithStatus("L").getModel();
        List<TuDailyStockEntity> indexStockList = getIndexStockDataWithTradeDate(tradeDate).getModel();
        List<HoldingSharesEntity> holdingSharesEntities = getHoldingShareChangeWithTradeDate(tradeDate).getModel();
        MoneyFlowEntity moneyFlowEntity = getMoneyFlowWithTradeDate(tradeDate).getModel();

        List<TuFullStockEntity> tuFullStockEntityList = StockAssembler.assemblerStocks(tuStockBasicEntityList, stockEntityList, tuStockListEntityList);

        List<TuFullStockEntity> limitUpList = new ArrayList<>();
        List<TuFullStockEntity> limitDownList = new ArrayList<>();
        List<TuFullStockEntity> upList = new ArrayList<>();
        List<TuFullStockEntity> downList = new ArrayList<>();
        List<TuFullStockEntity> flatList = new ArrayList<>();
        List<TuFullStockEntity> topList = new ArrayList<>();
        List<TuFullStockEntity> explodeList = new ArrayList<>();

        AnalyzeEntity analyzeEntity = new AnalyzeEntity();
        analyzeEntity.setLimitUpStocks(limitUpList);
        analyzeEntity.setLimitDownStocks(limitDownList);
        analyzeEntity.setTopStocks(topList);
        analyzeEntity.setExplodeStocks(explodeList);
        analyzeEntity.setIndexStocks(indexStockList);
        analyzeEntity.setHoldingSharesList(holdingSharesEntities);
        analyzeEntity.setMoneyFlow(moneyFlowEntity);
        analyzeEntity.setTradeDate(DateUtil.formatStringToDate(tradeDate, "yyyyMMdd"));

        for (TuFullStockEntity tuFullStockEntity : tuFullStockEntityList) {
            //涨幅大于0%;
            if (tuFullStockEntity.getChangeRate() > 0) {
                upList.add(tuFullStockEntity);
            }

            //跌幅大于0%
            if (tuFullStockEntity.getChangeRate() < 0) {
                downList.add(tuFullStockEntity);
            }

            //涨幅为0%
            if (tuFullStockEntity.getChangeRate() == 0) {
                flatList.add(tuFullStockEntity);
            }

            //涨幅大于9.8%
            if (tuFullStockEntity.getChangeRate() > 9.8) {
                limitUpList.add(tuFullStockEntity);
            }

            //跌幅大于9.8%
            if (tuFullStockEntity.getChangeRate() < - 9.8) {
                limitDownList.add(tuFullStockEntity);
            }

            if (tuFullStockEntity.getChangeRate() > 9.8 && tuFullStockEntity.getHighPrice().equals(tuFullStockEntity.getLowPrice())) {
                topList.add(tuFullStockEntity);
            }

            Float upShouldPrice = tuFullStockEntity.getPrePrice() * 1.098f;

            if (upShouldPrice > tuFullStockEntity.getClosePrice() && upShouldPrice < tuFullStockEntity.getHighPrice()) {
                explodeList.add(tuFullStockEntity);
            }

        }

        analyzeEntity.setLimitUpAmount(limitUpList.size());
        analyzeEntity.setLimitDownAmount(limitDownList.size());
        analyzeEntity.setTopAmount(topList.size());
        analyzeEntity.setExplodeAmount(explodeList.size());
        analyzeEntity.setUpAmount(upList.size());
        analyzeEntity.setDownAmount(downList.size());


//        //分析连板数
        List<AnalyzeEntity> lastTwentyDaysAnalyze = getLastTwentyDaysAnalysisResultWithTradeDate(tradeDate).getModel();
        if (lastTwentyDaysAnalyze.size() > 0 && DateUtil.formatDateToString(
                lastTwentyDaysAnalyze.get(0).getTradeDate(),"yyyyMMdd").equals(tradeDate)) {
            lastTwentyDaysAnalyze.remove(0);
        }
        Map<String, List<TuSimpleStockEntity>> map = analyzeContinuousStocksWithAnalyzeEntities(analyzeEntity, lastTwentyDaysAnalyze);
        analyzeEntity.setContinuousLimitUpStocks(map);

        Result<AnalyzeEntity> analyzeEntityResult = ResultUtil.buildSuccessResult(new Result<>(),analyzeEntity);
        redisUtil.set(ANALYZE_STOCK_PRE + tradeDate, JSONObject.toJSONString(analyzeEntityResult, SerializerFeature.DisableCircularReferenceDetect));

        return analyzeEntityResult;
    }

//    private String analyzeEmotionWithAnalyzeEntities(List<AnalyzeEntity> analyzeEntities) {
//
//    }

    private Map<String, List<TuSimpleStockEntity>> analyzeContinuousStocksWithAnalyzeEntities(
            @NonNull AnalyzeEntity todayAnalyze, @NonNull List<AnalyzeEntity> analyzeEntities) {
        Map<TuSimpleStockEntity, JSONObject> simpleStockEntityIntegerMap = new HashMap<>();

        //初始化生成map
        List<TuFullStockEntity> fullStockEntities = todayAnalyze.getLimitUpStocks();
        for (TuFullStockEntity tuFullStockEntity : fullStockEntities) {
            TuSimpleStockEntity tuSimpleStockEntity = new TuSimpleStockEntity();
            tuSimpleStockEntity.setStockCode(tuFullStockEntity.getStockCode());
            tuSimpleStockEntity.setStockName(tuFullStockEntity.getStockName());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("count", 1);
            jsonObject.put("flag", 0);
            simpleStockEntityIntegerMap.put(tuSimpleStockEntity, jsonObject);
        }

        for (AnalyzeEntity analyzeEntity : analyzeEntities) {
            List<TuFullStockEntity> limitUpList = analyzeEntity.getLimitUpStocks();
            for (TuFullStockEntity tuFullStockEntity : limitUpList) {

                TuSimpleStockEntity target = null;
                for (Map.Entry<TuSimpleStockEntity, JSONObject> entry : simpleStockEntityIntegerMap.entrySet()) {
                    TuSimpleStockEntity tuSimpleStockEntity = entry.getKey();
                    if (tuSimpleStockEntity.getStockCode().equals(tuFullStockEntity.getStockCode())) {
                        target = tuSimpleStockEntity;
                    }
                }
                JSONObject object = simpleStockEntityIntegerMap.get(target);

                if (target != null && (object.getInteger("flag") == 0 || object.getInteger("flag") == 1)) {
                    object.put("count", object.getInteger("count") + 1);
                    object.put("flag", 1);
                    simpleStockEntityIntegerMap.put(target, object);
                }
            }

            for (Map.Entry<TuSimpleStockEntity, JSONObject> entry : simpleStockEntityIntegerMap.entrySet()) {
                JSONObject tempObj = entry.getValue();
                if (tempObj.getInteger("flag") == 0) {
                    tempObj.put("flag", 2);
                } else if (tempObj.getInteger("flag") == 1) {
                    tempObj.put("flag", 0);
                }
                simpleStockEntityIntegerMap.put(entry.getKey(), tempObj);
            }
        }

        Map<String, List<TuSimpleStockEntity>> myNewHashMap = new HashMap<>();

        for(Map.Entry<TuSimpleStockEntity, JSONObject> entry : simpleStockEntityIntegerMap.entrySet()) {
            if (myNewHashMap.get(entry.getValue().getString("count")) == null) {
                List<TuSimpleStockEntity> simpleStockEntities = new ArrayList<>();
                myNewHashMap.put(entry.getValue().getString("count"), simpleStockEntities);
            }
            List<TuSimpleStockEntity> simpleStockEntities = myNewHashMap.get(entry.getValue().getString("count"));
            simpleStockEntities.add(entry.getKey());
            myNewHashMap.put(entry.getValue().getString("count"), simpleStockEntities);
        }

        return myNewHashMap;
    }

    @Override
    public String test() throws Exception {
        List<String> yearList = new ArrayList<>();
//        yearList.add("2008");
//        yearList.add("2009");
//        yearList.add("2010");
//        yearList.add("2011");
//        yearList.add("2012");
//        yearList.add("2013");
//        yearList.add("2014");
//        yearList.add("2015");
//        yearList.add("2016");
//        yearList.add("2017");
//        yearList.add("2018");
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
                        System.out.println("hello ex" + ex.getMessage());
                    }

                }
        );

        return "success";
    }

//    @Override
//    public String test2() throws Exception {
//        List<AnalyzeEntity> analyzeEntities = getLastTwentyDaysAnalysisResultWithTradeDate("20190614").getModel();
//        return JSON.toJSONString(analyzeContinuousStocksWithAnalyzeEntities(analyzeEntities));
//    }
}
