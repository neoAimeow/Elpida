package com.aimeow.entity;

import lombok.Data;

@Data
public class StockListEntity {
    /** 股票代码 */
    private String stockCode;
    /** 股票名字 */
    private String stockName;
    /** 所属行业 */
    private String industry;
    /** 股票全称*/
    private String stockFullName;
    /** 市场类型 （主板/中小板/创业板） */
    private String market;
    /** 上市状态： L上市 D退市 P暂停上市 */
    private String status;
}
