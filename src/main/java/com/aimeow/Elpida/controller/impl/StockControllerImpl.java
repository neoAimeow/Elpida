package com.aimeow.Elpida.controller.impl;

import com.aimeow.Elpida.controller.StockController;
import com.aimeow.Elpida.entity.TradeCalendarEntity;
import com.aimeow.Elpida.request.GetTradeCalendarRequest;
import com.aimeow.Elpida.tools.DateUtil;
import com.aimeow.Elpida.wrapper.StockRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.aimeow.Elpida.tools.DateUtil.DATE_FORMAT_FULL;

@Component
public class StockControllerImpl implements StockController {

    @Autowired private StockRequestWrapper stockRequestWrapper;

    @Override
    public List<TradeCalendarEntity> getTradeCalendarEntity() {
        GetTradeCalendarRequest getTradeCalendarRequest = new GetTradeCalendarRequest();
        getTradeCalendarRequest.setStartDate(DateUtil.formatStringToDate("2018-01-01 00:00:00",DATE_FORMAT_FULL));
        getTradeCalendarRequest.setEndDate(DateUtil.formatStringToDate("2019-01-01 00:00:00",DATE_FORMAT_FULL));
        return stockRequestWrapper.requestTradeCalendar(getTradeCalendarRequest);
    }
}
