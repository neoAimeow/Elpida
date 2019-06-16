package com.aimeow.Elpida.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AnalyzeEntity {
    private Date tradeDate;

    /** 当前市场情绪 */
    private String emotion;
    /** 涨停数 */
    private Integer limitUpAmount;
    /** 跌停数 */
    private Integer limitDownAmount;
    /** 炸板数 */
    private Integer explodeAmount;

    /** 一字板个数 */
    private Integer topAmount;

    /** 涨数 */
    private Integer upAmount;
    /** 跌数 */
    private Integer downAmount;

    /** 沪深港通资金流向 */
    private MoneyFlowEntity moneyFlow;

    /** 涨停股 */
    private List<FullStockEntity> limitUpStocks;
    /** 一字板股 */
    private List<FullStockEntity> topStocks;
    /** 跌停股 */
    private List<FullStockEntity> limitDownStocks;
    /** 炸板股 */
    private List<FullStockEntity> explodeStocks;
    /** 指数 */
    private List<DailyStockEntity> indexStocks;
    /** 增减持信息 */
    private List<HoldingSharesEntity> holdingSharesList;

}
