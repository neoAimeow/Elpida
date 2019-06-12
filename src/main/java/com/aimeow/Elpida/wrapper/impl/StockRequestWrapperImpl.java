package com.aimeow.Elpida.wrapper.impl;

import com.aimeow.Elpida.entity.DailyStockEntity;
import com.aimeow.Elpida.entity.StockListEntity;
import com.aimeow.Elpida.entity.TradeCalendarEntity;
import com.aimeow.Elpida.tools.DateUtil;
import com.aimeow.Elpida.tools.RedisUtil;
import com.aimeow.Elpida.wrapper.StockRequestWrapper;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class StockRequestWrapperImpl implements StockRequestWrapper {

    private static final String API_URI = "http://api.waditu.com";
    private static final String STOCK_LIST = "stock_basic";
    private static final String DAILY_STOCK = "daily";
    private static final String TRADE_CALENDAR = "trade_cal";
    private static final String TOKEN = "2e70679ed6dcf7f5adf2747f8caa6721c27dc77c910c6954e4936229";

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<DailyStockEntity> requestDailyStockInfoWithTradeDate(@NonNull Date tradeDate) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(tradeDate);

        JSONObject params = new JSONObject();
        params.put("trade_date", dateString);

        try {
            JSONObject result = request(DAILY_STOCK, params);
            //parse JSONObject to Entity;

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
        return null;

    }

    @Override
    public List<StockListEntity> requestStockList(@NonNull String status) throws Exception {
        JSONObject params = new JSONObject();
        params.put("list_status", status);
        JSONObject object = request(STOCK_LIST, params);

        List<StockListEntity> stockListEntities = new ArrayList<>();
        object.getJSONObject("data").getJSONArray("items").stream().forEach(jsonObject -> {
            JSONArray stockData = JSONArray.parseArray(jsonObject.toString());
            StockListEntity stockListEntity = new StockListEntity();
            stockListEntity.setStockCode(stockData.getString(0));
            stockListEntity.setStockName(stockData.getString(2));
            stockListEntity.setIndustry(stockData.getString(4));
            stockListEntity.setMarket(stockData.getString(5));
            stockListEntities.add(stockListEntity);
        });

        return stockListEntities;
    }

    @Override
    public List<TradeCalendarEntity> requestTradeCalendar(@NonNull Date startDate, @NonNull Date endDate) throws Exception {
        String startDateStr = DateUtil.formatDateToString(startDate, "yyyyMMdd");
        String endDateStr = DateUtil.formatDateToString(endDate, "yyyyMMdd");

        JSONObject params = new JSONObject();
        params.put("start_date", startDateStr);
        params.put("end_date", endDateStr);

        JSONObject object = request(TRADE_CALENDAR, params);
        List<TradeCalendarEntity> tradeCalendarEntities = new ArrayList<>();
        object.getJSONObject("data").getJSONArray("items").stream().forEach(jsonObject -> {
            JSONArray tradeCalArray = JSONArray.parseArray(jsonObject.toString());
            TradeCalendarEntity tradeCalendarEntity = new TradeCalendarEntity();
            tradeCalendarEntity.setCalDate(DateUtil.formatStringToDate(tradeCalArray.getString(1), "yyyyMMdd"));
            tradeCalendarEntity.setExchange(tradeCalArray.getString(0));
            tradeCalendarEntity.setIsOpen(tradeCalArray.getBoolean(2));
            tradeCalendarEntities.add(tradeCalendarEntity);
        });

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
        System.out.println(raw);
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
