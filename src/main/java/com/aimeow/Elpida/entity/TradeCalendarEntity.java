package com.aimeow.Elpida.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TradeCalendarEntity {
    private String exchange;
    private Date calDate;
    private Boolean isOpen;
}
