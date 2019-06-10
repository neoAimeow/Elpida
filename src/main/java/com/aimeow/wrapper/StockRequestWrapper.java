package com.aimeow.wrapper;

import com.aimeow.entity.DailyStockEntity;
import com.aimeow.entity.StockListEntity;

import java.util.List;

public interface StockRequestWrapper {
    List<StockListEntity> requestStockList();
    DailyStockEntity requestDailyStockInfo();
}
