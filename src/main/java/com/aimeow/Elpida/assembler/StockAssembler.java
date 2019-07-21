package com.aimeow.Elpida.assembler;

import com.aimeow.Elpida.entity.tushare.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StockAssembler {

    public static TuFullStockEntity assemblerStock(TuStockBasicEntity tuStockBasicEntity, TuDailyStockEntity tuDailyStockEntity, TuStockListEntity tuStockListEntity) {
        TuFullStockEntity tuFullStockEntity = new TuFullStockEntity();
        tuFullStockEntity.setStockCode(tuDailyStockEntity.getStockCode());
        tuFullStockEntity.setStockName(tuStockListEntity.getStockName());
        tuFullStockEntity.setTradeDate(tuDailyStockEntity.getTradeDate());
        tuFullStockEntity.setOpenPrice(tuDailyStockEntity.getOpenPrice());
        tuFullStockEntity.setHighPrice(tuDailyStockEntity.getHighPrice());
        tuFullStockEntity.setLowPrice(tuDailyStockEntity.getLowPrice());
        tuFullStockEntity.setClosePrice(tuDailyStockEntity.getClosePrice());
        tuFullStockEntity.setPrePrice(tuDailyStockEntity.getPrePrice());
        tuFullStockEntity.setChangePrice(tuDailyStockEntity.getChangePrice());
        tuFullStockEntity.setChangeRate(tuDailyStockEntity.getChangeRate());
        tuFullStockEntity.setVolume(tuDailyStockEntity.getVolume());
        tuFullStockEntity.setAmount(tuDailyStockEntity.getAmount());
        tuFullStockEntity.setTurnOverRate(tuStockBasicEntity.getTurnOverRate());
        tuFullStockEntity.setTurnOverRateF(tuStockBasicEntity.getTurnOverRateF());
        tuFullStockEntity.setVolumeRatio(tuStockBasicEntity.getVolumeRatio());
        tuFullStockEntity.setPe(tuStockBasicEntity.getPe());
        tuFullStockEntity.setPb(tuStockBasicEntity.getPb());
        tuFullStockEntity.setPs(tuStockBasicEntity.getPs());
        tuFullStockEntity.setTotalShare(tuStockBasicEntity.getTotalShare());
        tuFullStockEntity.setFloatShare(tuStockBasicEntity.getFloatShare());
        tuFullStockEntity.setFreeShare(tuStockBasicEntity.getFreeShare());
        tuFullStockEntity.setTotalMv(tuStockBasicEntity.getTotalMv());
        tuFullStockEntity.setCircMv(tuStockBasicEntity.getCircMv());
        return tuFullStockEntity;
    }

    public static List<TuFullStockEntity> assemblerStocks(List<TuStockBasicEntity> stockBasicEntities,
                                                          List<TuDailyStockEntity> dailyStockEntities,
                                                          List<TuStockListEntity> stockListEntities,
                                                          List<TuNewStockEntity> newStockEntities) {
        List<TuFullStockEntity> fullStockEntities = new ArrayList<>();
        for (TuDailyStockEntity tuDailyStockEntity : dailyStockEntities) {
            for (TuStockBasicEntity tuStockBasicEntity : stockBasicEntities) {
                if (tuStockBasicEntity.getStockCode().equals(tuDailyStockEntity.getStockCode())) {
                    for (TuStockListEntity tuStockListEntity : stockListEntities) {
                        if (tuStockListEntity.getStockCode().equals(tuStockBasicEntity.getStockCode())) {
                            TuFullStockEntity tuFullStockEntity = assemblerStock(tuStockBasicEntity, tuDailyStockEntity, tuStockListEntity);

                            for (TuNewStockEntity newStockEntity : newStockEntities) {
                                if (tuFullStockEntity.getStockCode().equals(newStockEntity.getStockCode())) {
                                    tuFullStockEntity.setIsNew(true);
                                }
                            }
                            fullStockEntities.add(tuFullStockEntity);
                        }
                    }
                }
            }
        }

        Iterator<TuFullStockEntity> it = fullStockEntities.iterator();
        while (it.hasNext()) {
            TuFullStockEntity tuFullStockEntity = it.next();
            if (tuFullStockEntity.getIsNew()) {
                it.remove();
            }
        }

        return fullStockEntities;
    }
}
