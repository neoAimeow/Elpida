package com.aimeow.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DailyStockEntity {
    /** 股票代码 */
    private String stockCode;
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
}
