package com.aimeow.Elpida.request;

import lombok.Data;

import java.util.Date;

@Data
public class GetTradeCalendarRequest {
    private String exchange;
    private Date startDate;
    private Date endDate;
    private Boolean isOpen;
}
