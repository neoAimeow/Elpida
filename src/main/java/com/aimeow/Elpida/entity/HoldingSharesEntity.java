package com.aimeow.Elpida.entity;

import lombok.Data;

import java.util.Date;

@Data
public class HoldingSharesEntity {
    private String stockCode;
    private String stockName;
    private Date annDate;
    private String holderName;
    private String holderType;
    private String type;
    private Float changeVol;
    private Float changeRatio;
}
