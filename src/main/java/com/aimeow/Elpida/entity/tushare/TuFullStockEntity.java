package com.aimeow.Elpida.entity.tushare;

import lombok.Data;

import java.util.Date;

@Data
public class TuFullStockEntity {
    /** 股票代码 */
    private String stockCode;
    /** 股票名称 */
    private String stockName;
    /** 交易日期 */
    private Date tradeDate;
    /** 开盘价 */
    private Float openPrice;
    /** 最高价 */
    private Float highPrice;
    /** 最低价 */
    private Float lowPrice;
    /** 收盘价 */
    private Float closePrice;
    /** 昨收价 */
    private Float prePrice;
    /** 涨跌额 */
    private Float changePrice;
    /** 涨跌幅 */
    private Float changeRate;
    /** 成交量 */
    private Float volume;
    /** 成交额 */
    private Float amount;
    /** 换手率 */
    private Float turnOverRate;
    /** 换手率（自由流通股） */
    private Float turnOverRateF;
    /** 量比 */
    private Float volumeRatio;
    /** 市盈率 */
    private Float pe;
    /** 市净率 */
    private Float pb;
    /** 市销率 */
    private Float ps;
    /** 总股本 */
    private Float totalShare;
    /** 流通股本 */
    private Float floatShare;
    /** 自由流通股本 */
    private Float freeShare;
    /** 总市值 */
    private Float totalMv;
    /** 流通市值 */
    private Float circMv;
    /** 是不是新股 */
    private Boolean isNew = false;
}
