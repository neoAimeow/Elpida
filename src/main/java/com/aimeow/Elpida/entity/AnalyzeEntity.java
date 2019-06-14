package com.aimeow.Elpida.entity;

import lombok.Data;

import java.util.List;

@Data
public class AnalyzeEntity {
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

    /** 涨停股 */
    private List<FullStockEntity> limitUpStocks;
    /** 一字板股 */
    private List<FullStockEntity> topStocks;
    /** 跌停股 */
    private List<FullStockEntity> limitDownStocks;
    /** 炸板股 */
    private List<FullStockEntity> explodeStocks;

}
