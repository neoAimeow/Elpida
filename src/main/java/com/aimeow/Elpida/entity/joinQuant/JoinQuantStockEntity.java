package com.aimeow.Elpida.entity.joinQuant;

import lombok.Data;

import java.util.Date;

@Data
public class JoinQuantStockEntity {
    private String stockCode;
    private String stockName;
    private Date date;
    private Float open;
    private Float close;
    private Float high;
    private Float low;
    private Float volume;
    private Float money;
    private Integer paused;
    private Float highLimit;
    private Float lowLimit;
}
