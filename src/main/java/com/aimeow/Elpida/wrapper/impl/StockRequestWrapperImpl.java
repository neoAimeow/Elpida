package com.aimeow.Elpida.wrapper.impl;

import com.aimeow.Elpida.entity.*;
import com.aimeow.Elpida.entity.joinQuant.JoinQuantSecurityEntity;
import com.aimeow.Elpida.entity.joinQuant.JoinQuantStockEntity;
import com.aimeow.Elpida.entity.tushare.TuDailyStockEntity;
import com.aimeow.Elpida.entity.tushare.TuNewStockEntity;
import com.aimeow.Elpida.entity.tushare.TuStockBasicEntity;
import com.aimeow.Elpida.entity.tushare.TuStockListEntity;
import com.aimeow.Elpida.tools.CsvUtil;
import com.aimeow.Elpida.tools.DateUtil;
import com.aimeow.Elpida.tools.RedisUtil;
import com.aimeow.Elpida.wrapper.StockRequestWrapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.NonNull;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.aimeow.Elpida.tools.DateUtil.DATE_FORMAT_FULL;
import static com.aimeow.Elpida.tools.DateUtil.DATE_FORMAT_YMD;

@Component
public class StockRequestWrapperImpl implements StockRequestWrapper {

    private static Logger logger = LoggerFactory.getLogger(StockRequestWrapper.class);

    private static final String TU_API_URI = "http://api.waditu.com";
    private static final String TU_STOCK_LIST = "stock_basic";
    private static final String TU_DAILY_STOCK = "daily";
    private static final String TU_NEW_STOCK = "new_share";
    private static final String TU_BASIC_STOCK_INFO = "daily_basic";
    private static final String TU_TRADE_CALENDAR = "trade_cal";
    private static final String TU_INDEX_DAILY = "index_daily";
    private static final String TU_NEWS = "news";
    private static final String TU_STK_HOLDER_TRADE = "stk_holdertrade";
    private static final String TU_MONEY_FLOW_HSGT = "moneyflow_hsgt";
    private static final String TU_TOKEN = "2e70679ed6dcf7f5adf2747f8caa6721c27dc77c910c6954e4936229";

    private static final String JOINQUANT_ACCOUNT1_MOBILE = "18814888787";
    private static final String JOINQUANT_ACCOUNT1_PASSWORD = "pKtWa4kZs3A@@!>\\q2zZ";

    private static final String JOINQUANT_ACCOUNT2_MOBILE = "18814868787";
    private static final String JOINQUANT_ACCOUNT2_PASSWORD = "RD7Kkh3YdOiXPnTvhZ";

    private static final String JOINQUANT_ACCOUNT3_MOBILE = "18601643730";
    private static final String JOINQUANT_ACCOUNT3_PASSWORD = "Fw6]i7W?]Whs8W-Snf";

    private static final String JOINQUANT_ACCOUNT1_TOKEN_PRE = "JOINQUANT_ACCOUNT1_TOKEN";
    private static final String JOINQUANT_ACCOUNT2_TOKEN_PRE = "JOINQUANT_ACCOUNT2_TOKEN";
    private static final String JOINQUANT_ACCOUNT3_TOKEN_PRE = "JOINQUANT_ACCOUNT3_TOKEN";

    private static final String JOINQUANT_API_URI = "https://dataapi.joinquant.com/apis";
    private static final String JOINQUANT_ALL_SECURITIES_PRE = "JOINQUANT_ALL_SECURITIES_";

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CsvUtil csvUtil;

    @Override
    public List<TuDailyStockEntity> tuRequestDailyStockInfoWithTradeDate(@NonNull Date tradeDate) throws Exception {
        JSONObject params = new JSONObject();
        params.put("trade_date", DateUtil.formatDateToString(tradeDate, "yyyyMMdd"));

        JSONObject result = tuRequest(TU_DAILY_STOCK, params);
        //parse JSONObject to Entity;
        List<TuDailyStockEntity> dailyStockEntities = new ArrayList<>();

        JSONObject data = result.getJSONObject("data");
        if (data != null) {
            JSONArray array = data.getJSONArray("items");
            if (array != null) {
                array.stream().forEach(
                        obj -> {
                            JSONArray stockData = JSONArray.parseArray(JSON.toJSONString(obj));
                            TuDailyStockEntity tuDailyStockEntity = new TuDailyStockEntity();
                            tuDailyStockEntity.setStockCode(stockData.getString(0));
                            tuDailyStockEntity.setTradeDate(DateUtil.formatStringToDate(
                                    stockData.getString(1), "yyyyMMdd"));
                            tuDailyStockEntity.setOpenPrice(stockData.getFloat(2));
                            tuDailyStockEntity.setHighPrice(stockData.getFloat(3));
                            tuDailyStockEntity.setLowPrice(stockData.getFloat(4));
                            tuDailyStockEntity.setClosePrice(stockData.getFloat(5));
                            tuDailyStockEntity.setPrePrice(stockData.getFloat(6));
                            tuDailyStockEntity.setChangePrice(stockData.getFloat(7));
                            tuDailyStockEntity.setChangeRate(stockData.getFloat(8));
                            tuDailyStockEntity.setVolume(stockData.getFloat(9));
                            tuDailyStockEntity.setAmount(stockData.getFloat(10));
                            dailyStockEntities.add(tuDailyStockEntity);
                        }
                );
            }
        }

        return dailyStockEntities;

    }

    @Override
    public List<TuDailyStockEntity> tuRequestDailyStockInfoWithStockCode(String stockCode, Date startDate, Date endDate) throws Exception {
        JSONObject params = new JSONObject();
        params.put("start_date", DateUtil.formatDateToString(startDate, "yyyyMMdd"));
        params.put("end_date", DateUtil.formatDateToString(endDate, "yyyyMMdd"));
        params.put("ts_code", stockCode);

        JSONObject result = tuRequest(TU_DAILY_STOCK, params);
        //parse JSONObject to Entity;
        List<TuDailyStockEntity> dailyStockEntities = new ArrayList<>();

        JSONObject data = result.getJSONObject("data");
        if (data != null) {
            JSONArray array = data.getJSONArray("items");
            if (array != null) {
                array.stream().forEach(
                        obj -> {
                            JSONArray stockData = JSONArray.parseArray(JSON.toJSONString(obj));
                            TuDailyStockEntity tuDailyStockEntity = new TuDailyStockEntity();
                            tuDailyStockEntity.setStockCode(stockData.getString(0));
                            tuDailyStockEntity.setTradeDate(DateUtil.formatStringToDate(
                                    stockData.getString(1), "yyyyMMdd"));
                            tuDailyStockEntity.setOpenPrice(stockData.getFloat(2));
                            tuDailyStockEntity.setHighPrice(stockData.getFloat(3));
                            tuDailyStockEntity.setLowPrice(stockData.getFloat(4));
                            tuDailyStockEntity.setClosePrice(stockData.getFloat(5));
                            tuDailyStockEntity.setPrePrice(stockData.getFloat(6));
                            tuDailyStockEntity.setChangePrice(stockData.getFloat(7));
                            tuDailyStockEntity.setChangeRate(stockData.getFloat(8));
                            tuDailyStockEntity.setVolume(stockData.getFloat(9));
                            tuDailyStockEntity.setAmount(stockData.getFloat(10));
                            dailyStockEntities.add(tuDailyStockEntity);
                        }
                );
            }
        }

        Comparator<TuDailyStockEntity> comparator = new Comparator<TuDailyStockEntity>() {
            @Override
            public int compare(TuDailyStockEntity o1, TuDailyStockEntity o2) {
                if (o1 == null || o2 == null) {
                    return 0;
                } else {
                    return -o1.getTradeDate().compareTo(o2.getTradeDate());
                }
            }
        };

        Collections.sort(dailyStockEntities, comparator);

        return dailyStockEntities;

    }

    @Override
    public List<TuNewStockEntity> tuRequestNewStockInfo(Date startDate, Date endDate) throws Exception {

        JSONObject params = new JSONObject();
        params.put("start_date", DateUtil.formatDateToString(startDate, "yyyyMMdd"));
        params.put("end_date", DateUtil.formatDateToString(endDate, "yyyyMMdd"));

        JSONObject result = tuRequest(TU_NEW_STOCK, params);
        System.out.println(JSONObject.toJSONString(result));
        //parse JSONObject to Entity;
        List<TuNewStockEntity> newStockEntities = new ArrayList<>();

        JSONObject data = result.getJSONObject("data");
        if (data != null) {
            JSONArray array = data.getJSONArray("items");
            if (array != null) {
                array.stream().forEach(
                        obj -> {
                            JSONArray stockData = JSONArray.parseArray(JSON.toJSONString(obj));
                            TuNewStockEntity tuNewStockEntity = new TuNewStockEntity();
                            tuNewStockEntity.setStockCode(stockData.getString(0));
                            tuNewStockEntity.setStockName(stockData.getString(2));
                            tuNewStockEntity.setIpoDate(DateUtil.formatStringToDate(stockData.getString(3), "yyyyMMdd"));
                            if (stockData.getString(4) != null && !stockData.getString(4).equals("nan")) {
                                tuNewStockEntity.setIssueDate(DateUtil.formatStringToDate(stockData.getString(4), "yyyyMMdd"));
                                newStockEntities.add(tuNewStockEntity);
                            }
                        }
                );
            }
        }

        return newStockEntities;
    }

    @Override
    public List<TuStockBasicEntity> tuRequestBasicStockInfoWithTradeDate(Date tradeDate) throws Exception {
        JSONObject params = new JSONObject();
        params.put("trade_date", DateUtil.formatDateToString(tradeDate, "yyyyMMdd"));

        JSONObject result = tuRequest(TU_BASIC_STOCK_INFO, params);
        List<TuStockBasicEntity> stockBasicEntities = new ArrayList<>();

        JSONObject data = result.getJSONObject("data");
        if (data != null) {
            JSONArray array = data.getJSONArray("items");
            if (array != null) {
                array.stream().forEach(
                        obj -> {
                            JSONArray stockData = JSONArray.parseArray(JSON.toJSONString(obj));
                            TuStockBasicEntity tuStockBasicEntity = new TuStockBasicEntity();
                            tuStockBasicEntity.setStockCode(stockData.getString(0));
                            tuStockBasicEntity.setTradeDate(DateUtil.formatStringToDate(
                                    stockData.getString(1), "yyyyMMdd"));
                            tuStockBasicEntity.setTurnOverRate(stockData.getFloat(3));
                            tuStockBasicEntity.setTurnOverRateF(stockData.getFloat(4));
                            tuStockBasicEntity.setVolumeRatio(stockData.getFloat(5));
                            tuStockBasicEntity.setPe(stockData.getFloat(6));
                            tuStockBasicEntity.setPb(stockData.getFloat(8));
                            tuStockBasicEntity.setPs(stockData.getFloat(9));
                            tuStockBasicEntity.setTotalShare(stockData.getFloat(11));
                            tuStockBasicEntity.setFloatShare(stockData.getFloat(12));
                            tuStockBasicEntity.setFreeShare(stockData.getFloat(13));
                            tuStockBasicEntity.setTotalMv(stockData.getFloat(14));
                            tuStockBasicEntity.setCircMv(stockData.getFloat(15));

                            stockBasicEntities.add(tuStockBasicEntity);
                        }
                );
            }
        }


        return stockBasicEntities;
    }

    @Override
    public TuDailyStockEntity tuRequestIndexStockInfoWithCodeAndTradeDate(String tsCode, Date tradeDate) throws Exception {
        JSONObject params = new JSONObject();
        params.put("ts_code", tsCode);
        params.put("trade_date", DateUtil.formatDateToString(tradeDate, "yyyyMMdd"));

        JSONObject result = tuRequest(TU_INDEX_DAILY, params);
        TuDailyStockEntity tuDailyStockEntity = new TuDailyStockEntity();


        JSONObject data = result.getJSONObject("data");
        if (data != null) {
            JSONArray array = data.getJSONArray("items");
            if (array != null) {
                array.stream().forEach(
                        obj -> {
                            JSONArray stockData = JSONArray.parseArray(JSON.toJSONString(obj));
                            tuDailyStockEntity.setStockCode(stockData.getString(0));
                            tuDailyStockEntity.setTradeDate(DateUtil.formatStringToDate(
                                    stockData.getString(1), "yyyyMMdd"));
                            tuDailyStockEntity.setOpenPrice(stockData.getFloat(2));
                            tuDailyStockEntity.setHighPrice(stockData.getFloat(3));
                            tuDailyStockEntity.setLowPrice(stockData.getFloat(4));
                            tuDailyStockEntity.setClosePrice(stockData.getFloat(5));
                            tuDailyStockEntity.setPrePrice(stockData.getFloat(6));
                            tuDailyStockEntity.setChangePrice(stockData.getFloat(7));
                            tuDailyStockEntity.setChangeRate(stockData.getFloat(8));
                            tuDailyStockEntity.setVolume(stockData.getFloat(9));
                            tuDailyStockEntity.setAmount(stockData.getFloat(10));
                        }
                );
            }
        }


        return tuDailyStockEntity;
    }

    @Override
    public List<NewsEntity> tuRequestNewsWithDate(Date startDate, Date endDate, String src) throws Exception {
        JSONObject params = new JSONObject();
        params.put("start_date", DateUtil.formatDateToString(startDate, "yyyyMMdd"));
        params.put("end_date", DateUtil.formatDateToString(endDate, "yyyyMMdd"));
        params.put("src", src);

        List<NewsEntity> newsEntities = new ArrayList<>();

        JSONObject result = tuRequest(TU_NEWS, params);
        JSONObject data = result.getJSONObject("data");
        if (data != null) {
            JSONArray array = data.getJSONArray("items");
            if (array != null) {
                array.stream().forEach(
                        news -> {
                            JSONArray stockData = JSONArray.parseArray(JSON.toJSONString(news));
                            NewsEntity newsEntity = new NewsEntity();
                            newsEntity.setDateTime(DateUtil.formatStringToDate(stockData.getString(0), DATE_FORMAT_FULL));
                            newsEntity.setContent(stockData.getString(1));
                            newsEntity.setTitle(stockData.getString(2));
                            newsEntity.setSrc(src);
                            newsEntities.add(newsEntity);
                        }
                );
            }

        }

        return newsEntities;
    }

    @Override
    public List<HoldingSharesEntity> tuRequestHoldingSharesChangeWithTradeDate(Date tradeDate) throws Exception {
        JSONObject params = new JSONObject();
        params.put("ann_date", DateUtil.formatDateToString(tradeDate, "yyyyMMdd"));
        List<HoldingSharesEntity> holdingSharesEntities = new ArrayList<>();
        JSONObject result = tuRequest(TU_STK_HOLDER_TRADE, params);

        JSONObject data = result.getJSONObject("data");
        if (data != null) {
            JSONArray array = data.getJSONArray("items");
            if (array != null) {
                array.stream().forEach(
                        obj -> {
                            JSONArray stockData = JSONArray.parseArray(JSON.toJSONString(obj));

                            HoldingSharesEntity holdingSharesEntity = new HoldingSharesEntity();
                            holdingSharesEntity.setStockCode(stockData.getString(0));
                            holdingSharesEntity.setAnnDate(DateUtil.formatStringToDate(stockData.getString(1), "yyyyMMdd"));
                            holdingSharesEntity.setHolderName(stockData.getString(2));
                            holdingSharesEntity.setHolderType(stockData.getString(3));
                            holdingSharesEntity.setType(stockData.getString(4));
                            holdingSharesEntity.setChangeVol(stockData.getFloat(5));
                            holdingSharesEntity.setChangeRatio(stockData.getFloat(6));

                            //去重
                            Boolean hasEn = false;
                            for (HoldingSharesEntity holdingSharesEntityTemp : holdingSharesEntities) {
                                if (holdingSharesEntityTemp.getStockCode().equals(holdingSharesEntity.getStockCode())) {
                                    hasEn = true;
                                    //TODO: Combine
                                }
                            }

                            if (!hasEn) {
                                holdingSharesEntities.add(holdingSharesEntity);
                            }
                        }
                );
            }
        }

        List<TuStockListEntity> tuStockListEntityList = tuRequestStockList("L");
        for (HoldingSharesEntity holdingSharesEntity : holdingSharesEntities) {
            for (TuStockListEntity tuStockListEntity : tuStockListEntityList) {
                if (tuStockListEntity.getStockCode().equals(holdingSharesEntity.getStockCode())) {
                    holdingSharesEntity.setStockName(tuStockListEntity.getStockName());
                }
                continue;
            }
        }

        return holdingSharesEntities;
    }

    @Override
    public MoneyFlowEntity tuRequestMoneyFlowWithTradeDate(Date tradeDate) throws Exception {
        JSONObject params = new JSONObject();
        params.put("trade_date", DateUtil.formatDateToString(tradeDate, "yyyyMMdd"));

        JSONObject result = tuRequest(TU_MONEY_FLOW_HSGT, params);
        List<MoneyFlowEntity> moneyFlowEntities = new ArrayList<>();

        JSONObject data = result.getJSONObject("data");
        if (data != null) {
            JSONArray array = data.getJSONArray("items");
            if (array != null) {
                array.stream().forEach(
                        obj -> {
                            MoneyFlowEntity moneyFlowEntity = new MoneyFlowEntity();
                            JSONArray stockData = JSONArray.parseArray(JSON.toJSONString(obj));
                            moneyFlowEntity.setGgtSs(stockData.getFloat(1));
                            moneyFlowEntity.setGgtSz(stockData.getFloat(2));
                            moneyFlowEntity.setHgt(stockData.getFloat(3));
                            moneyFlowEntity.setSgt(stockData.getFloat(4));
                            moneyFlowEntity.setNorthMoney(stockData.getFloat(5));
                            moneyFlowEntity.setSouthMoney(stockData.getFloat(6));
                            moneyFlowEntities.add(moneyFlowEntity);
                        }
                );
            }
        }

        if (moneyFlowEntities.size() > 0) {
            return moneyFlowEntities.get(0);
        } else {
            return null;
        }

    }

    @Override
    public List<TuStockListEntity> tuRequestStockList(@NonNull String status) throws Exception {
        JSONObject params = new JSONObject();
        params.put("list_status", status);
        JSONObject result = tuRequest(TU_STOCK_LIST, params);

        List<TuStockListEntity> stockListEntities = new ArrayList<>();

        JSONObject data = result.getJSONObject("data");
        if (data != null) {
            JSONArray array = data.getJSONArray("items");
            if (array != null) {
                array.stream().forEach(
                        obj -> {
                            JSONArray stockData = JSONArray.parseArray(obj.toString());
                            TuStockListEntity tuStockListEntity = new TuStockListEntity();
                            tuStockListEntity.setStockCode(stockData.getString(0));
                            tuStockListEntity.setStockName(stockData.getString(2));
                            tuStockListEntity.setIndustry(stockData.getString(4));
                            tuStockListEntity.setMarket(stockData.getString(5));
                            stockListEntities.add(tuStockListEntity);
                        }
                );
            }
        }

        return stockListEntities;
    }

    @Override
    public List<TradeCalendarEntity> tuRequestTradeCalendar(@NonNull Date startDate, @NonNull Date endDate) throws Exception {
        String startDateStr = DateUtil.formatDateToString(startDate, "yyyyMMdd");
        String endDateStr = DateUtil.formatDateToString(endDate, "yyyyMMdd");

        JSONObject params = new JSONObject();
        params.put("start_date", startDateStr);
        params.put("end_date", endDateStr);

        JSONObject result = tuRequest(TU_TRADE_CALENDAR, params);
        List<TradeCalendarEntity> tradeCalendarEntities = new ArrayList<>();

        JSONObject data = result.getJSONObject("data");
        if (data != null) {
            JSONArray array = data.getJSONArray("items");
            if (array != null) {
                array.stream().forEach(
                        obj -> {
                            JSONArray tradeCalArray = JSONArray.parseArray(obj.toString());
                            TradeCalendarEntity tradeCalendarEntity = new TradeCalendarEntity();
                            tradeCalendarEntity.setCalDate(DateUtil.formatStringToDate(tradeCalArray.getString(1), "yyyyMMdd"));
                            tradeCalendarEntity.setExchange(tradeCalArray.getString(0));
                            tradeCalendarEntity.setIsOpen(tradeCalArray.getBoolean(2));
                            tradeCalendarEntities.add(tradeCalendarEntity);
                        }
                );
            }
        }
        return tradeCalendarEntities;
    }


    @Override
    public List<JoinQuantSecurityEntity> joinQuantGetAllSecurities(String type) throws Exception {

        String str = redisUtil.get(JOINQUANT_ALL_SECURITIES_PRE + type);
        if (null == str) {
            JSONObject object = new JSONObject();
            object.put("code", type);
            object.put("date", DateUtil.formatDateToString(new Date(), "yyyy-MM-dd"));
            str = joinQuantRequest("get_all_securities", object);

            redisUtil.setEx(JOINQUANT_ALL_SECURITIES_PRE + type, csvUtil.getJSON(str, ","), 15, TimeUnit.HOURS);
        }

        List<JoinQuantSecurityEntity> joinQuantSecurityEntities = new ArrayList<>();

        JSONArray jsonArray = JSONArray.parseArray(str);
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++ ) {
                JSONObject data = JSONObject.parseObject(JSONObject.toJSONString(jsonArray.get(i)));
                JoinQuantSecurityEntity joinQuantSecurityEntity = new JoinQuantSecurityEntity();
                joinQuantSecurityEntity.setNumber(i);
                joinQuantSecurityEntity.setStockCode(data.getString("code"));
                joinQuantSecurityEntity.setDisplayName(data.getString("display_name"));
                joinQuantSecurityEntity.setType(data.getString("type"));
                joinQuantSecurityEntity.setStartDate(DateUtil.formatStringToDate(data.getString("start_date"), "yyyy-MM-dd"));
                joinQuantSecurityEntity.setEndDate(DateUtil.formatStringToDate(data.getString("end_date"), "yyyy-MM-dd"));
                joinQuantSecurityEntities.add(joinQuantSecurityEntity);
            }
        }


        return joinQuantSecurityEntities;
    }

    @Override
    public List<JoinQuantStockEntity> joinQuantGetStock(JoinQuantSecurityEntity securityEntity, Date tradeDate, String unit, Integer accountCode) throws Exception {
        JSONObject object = new JSONObject();
        object.put("code", securityEntity.getStockCode());
        object.put("date", DateUtil.formatDateToString(tradeDate, "yyyy-MM-dd"));
        object.put("end_date", DateUtil.formatDateToString(tradeDate, "yyyy-MM-dd"));
        object.put("unit", unit);
        String str = joinQuantRequest("get_price_period", object, accountCode);
//        System.out.println(csvUtil.getJSON(str, ","));
        return null;
    }

    @Override
    public Long joinQuantGetQueryCount() throws Exception {
        return Long.valueOf(joinQuantRequest("get_query_count", new JSONObject()));
    }

    @Override
    public void joinQuantRequestToken() throws Exception {
        JSONObject rawObject1 = new JSONObject();
        rawObject1.put("method", "get_current_token");
        rawObject1.put("mob", JOINQUANT_ACCOUNT1_MOBILE);
        rawObject1.put("pwd", JOINQUANT_ACCOUNT1_PASSWORD);

        JSONObject rawObject2 = new JSONObject();
        rawObject2.put("method", "get_current_token");
        rawObject2.put("mob", JOINQUANT_ACCOUNT2_MOBILE);
        rawObject2.put("pwd", JOINQUANT_ACCOUNT2_PASSWORD);

        JSONObject rawObject3 = new JSONObject();
        rawObject3.put("method", "get_current_token");
        rawObject3.put("mob", JOINQUANT_ACCOUNT3_MOBILE);
        rawObject3.put("pwd", JOINQUANT_ACCOUNT3_PASSWORD);

        String content1 = request(JOINQUANT_API_URI, rawObject1);
        String content2 = request(JOINQUANT_API_URI, rawObject2);
        String content3 = request(JOINQUANT_API_URI, rawObject3);

        redisUtil.setEx(JOINQUANT_ACCOUNT1_TOKEN_PRE, content1, 23, TimeUnit.HOURS);
        redisUtil.setEx(JOINQUANT_ACCOUNT2_TOKEN_PRE, content2, 23, TimeUnit.HOURS);
        redisUtil.setEx(JOINQUANT_ACCOUNT3_TOKEN_PRE, content3, 23, TimeUnit.HOURS);
    }

    private JSONObject tuRequest(String apiName , JSONObject params) throws Exception {
        JSONObject rawObject = new JSONObject();
        rawObject.put("api_name", apiName);
        rawObject.put("token", TU_TOKEN);
        rawObject.put("field", "");
        rawObject.put("params", params);
        String content = request(TU_API_URI, rawObject);
        return JSONObject.parseObject(content);
    }

    private String joinQuantRequest(String apiName, JSONObject params) throws Exception {
        return joinQuantRequest(apiName, params, 1);
    }

    private String joinQuantRequest(String apiName , JSONObject params, Integer accountCode) throws Exception {
        String account;
        if (accountCode == 1) {
            account = JOINQUANT_ACCOUNT1_TOKEN_PRE;
        } else if (accountCode == 2) {
            account = JOINQUANT_ACCOUNT2_TOKEN_PRE;
        } else {
            account = JOINQUANT_ACCOUNT3_TOKEN_PRE;
        }

        String token = redisUtil.get(account);

        if (null == token) {
            joinQuantRequestToken();
            token = redisUtil.get(account);
        }

        JSONObject rawObject = new JSONObject();
        rawObject.put("method", apiName);
        rawObject.put("token", token);
        for (String key : params.keySet()) {
            rawObject.put(key, params.getString(key));
        }

        String content = request(JOINQUANT_API_URI, rawObject);

        if (content.contains("error: token过期，请重新获取")) {
            joinQuantRequestToken();
            content = request(JOINQUANT_API_URI, rawObject);
        }

//        System.out.println(content);

        return content;
    }

    private String request(String apiUri, JSONObject param) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(apiUri);
        httpPost.setHeader("Content-type", "application/json");

        String raw = param.toJSONString();
        httpPost.setEntity(new StringEntity(raw, ContentType.DEFAULT_TEXT));

        CloseableHttpResponse response = httpclient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() != 200) {
            if (response != null) {
                response.close();
            }
            httpclient.close();
            return null;
        }

        String content = EntityUtils.toString(response.getEntity(), "UTF-8");

//        logger.error(content);

        if (response != null) {
            response.close();
        }
        httpclient.close();
        return content;
    }
}
