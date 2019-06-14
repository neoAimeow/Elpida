package com.aimeow.Elpida.entity;

import lombok.Data;

import java.util.Date;

@Data
public class StockBasicEntity {
    /** 股票代码 */
    private String stockCode;
    /** 交易日期 */
    private Date tradeDate;
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
}
