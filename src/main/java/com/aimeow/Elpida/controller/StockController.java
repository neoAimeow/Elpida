package com.aimeow.Elpida.controller;

import com.aimeow.Elpida.entity.TradeCalendarEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public interface StockController {
    List<TradeCalendarEntity> getTradeCalendarEntity();
}
