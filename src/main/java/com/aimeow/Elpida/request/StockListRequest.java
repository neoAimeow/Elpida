package com.aimeow.Elpida.request;

import lombok.Data;

@Data
public class StockListRequest {
    /** 上市状态： L上市 D退市 P暂停上市 */
    private String stockStatus;
    /** 交易所 SSE上交所 SZSE深交所 */
    private String exchange;
}
