package com.aimeow.Elpida.wrapper.impl;

import com.aimeow.Elpida.entity.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.aimeow.Elpida.tools.DateUtil.DATE_FORMAT_FULL;

@Component
public class StockRequestWrapperImpl implements StockRequestWrapper {

    private static final String API_URI = "http://api.waditu.com";
    private static final String STOCK_LIST = "stock_basic";
    private static final String DAILY_STOCK = "daily";
    private static final String BASIC_STOCK_INFO = "daily_basic";
    private static final String TRADE_CALENDAR = "trade_cal";
    private static final String INDEX_DAILY = "index_daily";
    private static final String NEWS = "news";
    private static final String STK_HOLDER_TRADE = "stk_holdertrade";
    private static final String TOKEN = "2e70679ed6dcf7f5adf2747f8caa6721c27dc77c910c6954e4936229";

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<DailyStockEntity> requestDailyStockInfoWithTradeDate(@NonNull Date tradeDate) throws Exception {
        JSONObject params = new JSONObject();
        params.put("trade_date", DateUtil.formatDateToString(tradeDate, "yyyyMMdd"));

        JSONObject result = request(DAILY_STOCK, params);
        //parse JSONObject to Entity;
        List<DailyStockEntity> dailyStockEntities = new ArrayList<>();

        JSONObject data = result.getJSONObject("data");
        if (data != null) {
            JSONArray array = data.getJSONArray("items");
            if (array != null) {
                array.stream().forEach(
                        obj -> {
                            JSONArray stockData = JSONArray.parseArray(JSON.toJSONString(obj));
                            DailyStockEntity dailyStockEntity = new DailyStockEntity();
                            dailyStockEntity.setStockCode(stockData.getString(0));
                            dailyStockEntity.setTradeDate(DateUtil.formatStringToDate(
                                    stockData.getString(1), "yyyyMMdd"));
                            dailyStockEntity.setOpenPrice(stockData.getFloat(2));
                            dailyStockEntity.setHighPrice(stockData.getFloat(3));
                            dailyStockEntity.setLowPrice(stockData.getFloat(4));
                            dailyStockEntity.setClosePrice(stockData.getFloat(5));
                            dailyStockEntity.setPrePrice(stockData.getFloat(6));
                            dailyStockEntity.setChangePrice(stockData.getFloat(7));
                            dailyStockEntity.setChangeRate(stockData.getFloat(8));
                            dailyStockEntity.setVolume(stockData.getFloat(9));
                            dailyStockEntity.setAmount(stockData.getFloat(10));
                            dailyStockEntities.add(dailyStockEntity);
                        }
                );
            }
        }

        return dailyStockEntities;

    }

    @Override
    public List<StockBasicEntity> requestBasicStockInfoWithTradeDate(Date tradeDate) throws Exception {
        JSONObject params = new JSONObject();
        params.put("trade_date", DateUtil.formatDateToString(tradeDate, "yyyyMMdd"));

        JSONObject result = request(BASIC_STOCK_INFO, params);
        List<StockBasicEntity> stockBasicEntities = new ArrayList<>();

        JSONObject data = result.getJSONObject("data");
        if (data != null) {
            JSONArray array = data.getJSONArray("items");
            if (array != null) {
                array.stream().forEach(
                        obj -> {
                            JSONArray stockData = JSONArray.parseArray(JSON.toJSONString(obj));
                            StockBasicEntity stockBasicEntity = new StockBasicEntity();
                            stockBasicEntity.setStockCode(stockData.getString(0));
                            stockBasicEntity.setTradeDate(DateUtil.formatStringToDate(
                                    stockData.getString(1), "yyyyMMdd"));
                            stockBasicEntity.setTurnOverRate(stockData.getFloat(3));
                            stockBasicEntity.setTurnOverRateF(stockData.getFloat(4));
                            stockBasicEntity.setVolumeRatio(stockData.getFloat(5));
                            stockBasicEntity.setPe(stockData.getFloat(6));
                            stockBasicEntity.setPb(stockData.getFloat(8));
                            stockBasicEntity.setPs(stockData.getFloat(9));
                            stockBasicEntity.setTotalShare(stockData.getFloat(11));
                            stockBasicEntity.setFloatShare(stockData.getFloat(12));
                            stockBasicEntity.setFreeShare(stockData.getFloat(13));
                            stockBasicEntity.setTotalMv(stockData.getFloat(14));
                            stockBasicEntity.setCircMv(stockData.getFloat(15));

                            stockBasicEntities.add(stockBasicEntity);
                        }
                );
            }
        }


        return stockBasicEntities;
    }

    @Override
    public DailyStockEntity requestIndexStockInfoWithCodeAndTradeDate(String tsCode, Date tradeDate) throws Exception {
        JSONObject params = new JSONObject();
        params.put("ts_code", tsCode);
        params.put("trade_date", DateUtil.formatDateToString(tradeDate, "yyyyMMdd"));

        JSONObject result = request(INDEX_DAILY, params);
        DailyStockEntity dailyStockEntity = new DailyStockEntity();


        JSONObject data = result.getJSONObject("data");
        if (data != null) {
            JSONArray array = data.getJSONArray("items");
            if (array != null) {
                array.stream().forEach(
                        obj -> {
                            JSONArray stockData = JSONArray.parseArray(JSON.toJSONString(obj));
                            dailyStockEntity.setStockCode(stockData.getString(0));
                            dailyStockEntity.setTradeDate(DateUtil.formatStringToDate(
                                    stockData.getString(1), "yyyyMMdd"));
                            dailyStockEntity.setOpenPrice(stockData.getFloat(2));
                            dailyStockEntity.setHighPrice(stockData.getFloat(3));
                            dailyStockEntity.setLowPrice(stockData.getFloat(4));
                            dailyStockEntity.setClosePrice(stockData.getFloat(5));
                            dailyStockEntity.setPrePrice(stockData.getFloat(6));
                            dailyStockEntity.setChangePrice(stockData.getFloat(7));
                            dailyStockEntity.setChangeRate(stockData.getFloat(8));
                            dailyStockEntity.setVolume(stockData.getFloat(9));
                            dailyStockEntity.setAmount(stockData.getFloat(10));
                        }
                );
            }
        }


        return dailyStockEntity;
    }

    @Override
    public List<NewsEntity> requestNewsWithDate(Date startDate, Date endDate, String src) throws Exception {
        JSONObject params = new JSONObject();
        params.put("start_date", DateUtil.formatDateToString(startDate, "yyyyMMdd"));
        params.put("end_date", DateUtil.formatDateToString(endDate, "yyyyMMdd"));
        params.put("src", src);

        List<NewsEntity> newsEntities = new ArrayList<>();

        JSONObject result = request(NEWS, params);
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
    public List<HoldingSharesEntity> requestHoldingSharesChangeWithTradeDate(Date tradeDate) throws Exception {
        JSONObject params = new JSONObject();
        params.put("ann_date", DateUtil.formatDateToString(tradeDate, "yyyyMMdd"));
        List<HoldingSharesEntity> holdingSharesEntities = new ArrayList<>();
        JSONObject result = request(STK_HOLDER_TRADE, params);

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

        List<StockListEntity> stockListEntityList = requestStockList("L");
        for (HoldingSharesEntity holdingSharesEntity : holdingSharesEntities) {
            for (StockListEntity stockListEntity : stockListEntityList) {
                if (stockListEntity.getStockCode().equals(holdingSharesEntity.getStockCode())) {
                    holdingSharesEntity.setStockName(stockListEntity.getStockName());
                }
                continue;
            }
        }

        return holdingSharesEntities;
    }

    @Override
    public List<StockListEntity> requestStockList(@NonNull String status) throws Exception {
        JSONObject params = new JSONObject();
        params.put("list_status", status);
        JSONObject result = request(STOCK_LIST, params);

        List<StockListEntity> stockListEntities = new ArrayList<>();

        JSONObject data = result.getJSONObject("data");
        if (data != null) {
            JSONArray array = data.getJSONArray("items");
            if (array != null) {
                array.stream().forEach(
                        obj -> {
                            JSONArray stockData = JSONArray.parseArray(obj.toString());
                            StockListEntity stockListEntity = new StockListEntity();
                            stockListEntity.setStockCode(stockData.getString(0));
                            stockListEntity.setStockName(stockData.getString(2));
                            stockListEntity.setIndustry(stockData.getString(4));
                            stockListEntity.setMarket(stockData.getString(5));
                            stockListEntities.add(stockListEntity);
                        }
                );
            }
        }

        return stockListEntities;
    }

    @Override
    public List<TradeCalendarEntity> requestTradeCalendar(@NonNull Date startDate, @NonNull Date endDate) throws Exception {
        String startDateStr = DateUtil.formatDateToString(startDate, "yyyyMMdd");
        String endDateStr = DateUtil.formatDateToString(endDate, "yyyyMMdd");

        JSONObject params = new JSONObject();
        params.put("start_date", startDateStr);
        params.put("end_date", endDateStr);

        JSONObject result = request(TRADE_CALENDAR, params);
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

    private JSONObject request(String apiName , JSONObject params) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(API_URI);
        httpPost.setHeader("Content-type", "application/json");

        JSONObject rawObject = new JSONObject();
        rawObject.put("api_name", apiName);
        rawObject.put("token", TOKEN);
        rawObject.put("field", "");
        rawObject.put("params", params);
        String raw = rawObject.toJSONString();
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

        if (response != null) {
            response.close();
        }
        httpclient.close();

        return JSONObject.parseObject(content);
    }
}
