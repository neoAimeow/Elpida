package com.aimeow.Elpida.entity.tushare;

import lombok.Data;

@Data
public class TuStockListEntity {
    /** 股票代码 */
    private String stockCode;
    /** 股票名字 */
    private String stockName;
    /** 所属行业 */
    private String industry;
    /** 市场类型 （主板/中小板/创业板） */
    private String market;
}
