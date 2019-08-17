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
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    public Result<List<TuNewStockEntity>> getNewStockInfo(String startDate, String endDate) throws Exception {
        List<TuNewStockEntity> newStockEntities = stockRequestWrapper.tuRequestNewStockInfo(
                DateUtil.formatStringToDate(startDate, "yyyyMMdd"), DateUtil.formatStringToDate(endDate, "yyyyMMdd"));
        for (TuNewStockEntity newStockEntity : newStockEntities) {
            Query query=new Query(Criteria.where("stockCode").is(newStockEntity.getStockCode()));
            TuNewStockEntity td =  mongoTemplate.findOne(query , TuNewStockEntity.class);
            if (td == null) {
                mongoTemplate.save(newStockEntity);
            }
        }
        return ResultUtil.buildSuccessResult(new Result<>(), newStockEntities);
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
    public Result<List<NewsEntity>> collectNewsFromTuShare(String tradeDate) throws Exception {
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

        for (NewsEntity newsEntity : newsEntities) {
            Query query = new Query();
            Criteria criteria = Criteria
                    .where("title").is(newsEntity.getTitle())
                    .and("content").is(newsEntity.getContent())
//                    .and("src").is(newsEntity.getSrc())
                    ;

            query.addCriteria(criteria);

            List<NewsEntity> newStockEntities =  mongoTemplate.find(query, NewsEntity.class);
            if (newStockEntities.size() == 0) {
                mongoTemplate.save(newsEntity);
            }
        }
        return getNewsWithTradeDate(tradeDate);
    }

    @Override
    public Result<List<NewsEntity>> getNewsWithTradeDate(String tradeDate) throws Exception {
        Query query = new Query();
        Date date = DateUtil.formatStringToDate(tradeDate, "yyyyMMdd");

        Criteria c = Criteria.where("dateTime").gte(date).lt(DateUtil.addDays(date, 1));
        query.addCriteria(c);
        query.with(new Sort(Sort.Direction.DESC, "dateTime"));
        List<NewsEntity> newStockEntities =  mongoTemplate.find(query, NewsEntity.class);
        return ResultUtil.buildSuccessResult(new Result<>(), newStockEntities);
    }

    @Override
    public Result<Boolean> markNewsAsImportant(
            @NonNull String id) {
        mongoTemplate.updateFirst(
                new Query(Criteria.where("id").is(id)),
                Update.update("isMark", true),
                NewsEntity.class);
        return ResultUtil.buildSuccessResult(new Result<>(), true);
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
        if (null == dataStr) {
            return ResultUtil.getFailureResult("analysisResult get error, dataStr is empty.");
        }

        JSONObject jsonObject = JSONObject.parseObject(dataStr);
        JSONObject model = jsonObject.getJSONObject("model");
        AnalyzeEntity analyzeEntity = JSONObject.parseObject(JSON.toJSONString(model), AnalyzeEntity.class);

        return ResultUtil.buildSuccessResult(new Result<>(), analyzeEntity);
    }

    @Override
    public Result<AnalyzeEntity> analyzeStockDataWithTradeData(@NonNull String tradeDate) throws Exception {

        Date dateAfter = DateUtil.formatStringToDate(tradeDate, "yyyyMMdd");
        String dateAfterStr = DateUtil.formatDateToString(dateAfter, DATE_FORMAT_YMD);
        String beforeDateStr = DateUtil.getCalculateDateToString(dateAfterStr, -15);
        Date dateBefore = DateUtil.formatStringToDate(beforeDateStr, DATE_FORMAT_YMD);


        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "issueDate"));
        Criteria criteria = Criteria.where("issueDate").gte(dateBefore).lt(dateAfter);
        query.addCriteria(criteria);

        List<TuNewStockEntity> newStockEntities =  mongoTemplate.find(query, TuNewStockEntity.class);

        List<TuDailyStockEntity> stockEntityList = getDailyStockWithTradeDate(tradeDate).getModel();
        List<TuStockBasicEntity> tuStockBasicEntityList = getBasicStockDataWithTradeDate(tradeDate).getModel();
        List<TuStockListEntity> tuStockListEntityList = getUpdateStockListWithStatus("L").getModel();
        List<TuDailyStockEntity> indexStockList = getIndexStockDataWithTradeDate(tradeDate).getModel();
        List<HoldingSharesEntity> holdingSharesEntities = getHoldingShareChangeWithTradeDate(tradeDate).getModel();
        MoneyFlowEntity moneyFlowEntity = getMoneyFlowWithTradeDate(tradeDate).getModel();

        List<TuFullStockEntity> tuFullStockEntityList = StockAssembler.assemblerStocks(
                tuStockBasicEntityList, stockEntityList, tuStockListEntityList, newStockEntities);

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

        Map<String, List<TuSimpleStockEntity>> map = analyzeContinuousStocksWithAnalyzeEntities(analyzeEntity, tradeDate);
        analyzeEntity.setContinuousLimitUpStocks(map);

        Result<AnalyzeEntity> analyzeEntityResult = ResultUtil.buildSuccessResult(new Result<>(),analyzeEntity);
        redisUtil.set(ANALYZE_STOCK_PRE + tradeDate, JSONObject.toJSONString(analyzeEntityResult, SerializerFeature.DisableCircularReferenceDetect));

        return analyzeEntityResult;
    }

//    private String analyzeEmotionWithAnalyzeEntities(List<AnalyzeEntity> analyzeEntities) {
//
//    }

    private Map<String, List<TuSimpleStockEntity>> analyzeContinuousStocksWithAnalyzeEntities(
            @NonNull AnalyzeEntity todayAnalyze, @NonNull String tradeDate) throws Exception {
        Date dateAfter = DateUtil.formatStringToDate(tradeDate, "yyyyMMdd");
        String dateAfterStr = DateUtil.formatDateToString(dateAfter, DATE_FORMAT_YMD);
        String beforeDateStr = DateUtil.getCalculateDateToString(dateAfterStr, -20);
        Date dateBefore = DateUtil.formatStringToDate(beforeDateStr, DATE_FORMAT_YMD);

        Map<String, List<TuSimpleStockEntity>> myNewHashMap = new HashMap<>();

        //初始化生成map
        List<TuFullStockEntity> fullStockEntities = todayAnalyze.getLimitUpStocks();

        fullStockEntities.parallelStream().forEach(
                obj -> {
                    try {
                        TuSimpleStockEntity tuSimpleStockEntity = new TuSimpleStockEntity();
                        tuSimpleStockEntity.setStockCode(obj.getStockCode());
                        tuSimpleStockEntity.setStockName(obj.getStockName());

                        List<TuDailyStockEntity> historyStockEntities =  stockRequestWrapper.tuRequestDailyStockInfoWithStockCode(obj.getStockCode(), dateBefore, dateAfter);

                        Integer count = 0;
                        for (TuDailyStockEntity dailyStockEntity : historyStockEntities) {
                            if (dailyStockEntity.getChangeRate() > 9.8) {
                                count ++;
                            } else {
                                break;
                            }
                        }

                        if (myNewHashMap.get(count.toString()) == null) {
                            List<TuSimpleStockEntity> simpleStockEntities = new ArrayList<>();
                            myNewHashMap.put(count.toString(), simpleStockEntities);
                        }

                        List<TuSimpleStockEntity> tuDailyStockEntities = myNewHashMap.get(count.toString());
                        tuDailyStockEntities.add(tuSimpleStockEntity);

                    } catch (Exception ex) {

                    }
                }
        );

        return myNewHashMap;
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
                        System.out.println("hello ex" + ex.getMessage());
                    }

                }
        );

        return "success";
    }

    @Override
    public String test2() throws Exception {

        Query query = new Query();
        Criteria criteria = Criteria.where("title").is("")
                .and("content").is("市场消息：瑞士证券交易所据称考虑收购总部位于欧盟的交易所。")
                .and("src").is("sina")
                ;

        query.addCriteria(criteria);

        List<NewsEntity> newStockEntities =  mongoTemplate.find(query, NewsEntity.class);
        return JSONObject.toJSONString(newStockEntities);
    }
}
