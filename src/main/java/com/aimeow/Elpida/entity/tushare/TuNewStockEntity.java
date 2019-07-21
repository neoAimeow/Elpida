package com.aimeow.Elpida.entity.tushare;

import lombok.Data;

import java.util.Date;

@Data
public class TuNewStockEntity {
    /** 股票代码 */
    private String stockCode;
    /** 股票名字 */
    private String stockName;
    /** ipo日期 */
    private Date ipoDate;
    /** 上市日期 */
    private Date issueDate;
}
