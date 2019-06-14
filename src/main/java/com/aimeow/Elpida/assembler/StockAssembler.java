package com.aimeow.Elpida.assembler;

import com.aimeow.Elpida.entity.DailyStockEntity;
import com.aimeow.Elpida.entity.FullStockEntity;
import com.aimeow.Elpida.entity.StockBasicEntity;
import com.aimeow.Elpida.entity.StockListEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockAssembler {

    public static FullStockEntity assemblerStock(StockBasicEntity stockBasicEntity, DailyStockEntity dailyStockEntity, StockListEntity stockListEntity) {
        FullStockEntity fullStockEntity = new FullStockEntity();
        fullStockEntity.setStockCode(dailyStockEntity.getStockCode());
        fullStockEntity.setStockName(stockListEntity.getStockName());
        fullStockEntity.setTradeDate(dailyStockEntity.getTradeDate());
        fullStockEntity.setOpenPrice(dailyStockEntity.getOpenPrice());
        fullStockEntity.setHighPrice(dailyStockEntity.getHighPrice());
        fullStockEntity.setLowPrice(dailyStockEntity.getLowPrice());
        fullStockEntity.setClosePrice(dailyStockEntity.getClosePrice());
        fullStockEntity.setPrePrice(dailyStockEntity.getPrePrice());
        fullStockEntity.setChangePrice(dailyStockEntity.getChangePrice());
        fullStockEntity.setChangeRate(dailyStockEntity.getChangeRate());
        fullStockEntity.setVolume(dailyStockEntity.getVolume());
        fullStockEntity.setAmount(dailyStockEntity.getAmount());
        fullStockEntity.setTurnOverRate(stockBasicEntity.getTurnOverRate());
        fullStockEntity.setTurnOverRateF(stockBasicEntity.getTurnOverRateF());
        fullStockEntity.setVolumeRatio(stockBasicEntity.getVolumeRatio());
        fullStockEntity.setPe(stockBasicEntity.getPe());
        fullStockEntity.setPb(stockBasicEntity.getPb());
        fullStockEntity.setPs(stockBasicEntity.getPs());
        fullStockEntity.setTotalShare(stockBasicEntity.getTotalShare());
        fullStockEntity.setFloatShare(stockBasicEntity.getFloatShare());
        fullStockEntity.setFreeShare(stockBasicEntity.getFreeShare());
        fullStockEntity.setTotalMv(stockBasicEntity.getTotalMv());
        fullStockEntity.setCircMv(stockBasicEntity.getCircMv());
        return fullStockEntity;
    }

    public static List<FullStockEntity> assemblerStocks(List<StockBasicEntity> stockBasicEntities,
                                          List<DailyStockEntity> dailyStockEntities,
                                          List<StockListEntity> stockListEntities) {
        List<FullStockEntity> fullStockEntities = new ArrayList<>();
        for (DailyStockEntity dailyStockEntity : dailyStockEntities) {
            for (StockBasicEntity stockBasicEntity : stockBasicEntities) {
                if (stockBasicEntity.getStockCode().equals(dailyStockEntity.getStockCode())) {
                    for (StockListEntity stockListEntity : stockListEntities) {
                        if (stockListEntity.getStockCode().equals(stockBasicEntity.getStockCode())) {
                            FullStockEntity fullStockEntity = assemblerStock(stockBasicEntity, dailyStockEntity, stockListEntity);
                            fullStockEntities.add(fullStockEntity);
                        }
                    }
                }
            }
        }
        return fullStockEntities;
    }
}
