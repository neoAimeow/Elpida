package com.aimeow.Elpida.wrapper;

import com.aimeow.Elpida.entity.*;
import com.aimeow.Elpida.entity.joinQuant.JoinQuantSecurityEntity;
import com.aimeow.Elpida.entity.joinQuant.JoinQuantStockEntity;
import com.aimeow.Elpida.entity.tushare.TuDailyStockEntity;
import com.aimeow.Elpida.entity.tushare.TuStockBasicEntity;
import com.aimeow.Elpida.entity.tushare.TuStockListEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public interface StockRequestWrapper {

    /** tushare股票列表,获取基础信息数据，包括股票代码、名称、上市日期、退市日期等 */
    List<TuStockListEntity> tuRequestStockList(String status) throws Exception;

    /** tushare通过交易时间来获取当时所有股票的日线行情 */
    List<TuDailyStockEntity> tuRequestDailyStockInfoWithTradeDate(Date tradeDate) throws Exception;

    /** tushare通过交易时间来 获取当时所有股票的基础信息数据 */
    List<TuStockBasicEntity> tuRequestBasicStockInfoWithTradeDate(Date tradeDate) throws Exception;

    /** tushare通过指数代码与交易时间来获取指数数据 */
    TuDailyStockEntity tuRequestIndexStockInfoWithCodeAndTradeDate(String tsCode, Date tradeDate) throws Exception;

    /** tushare通过开始时间与结束时间来获取新闻，src包含sina、wallstreetcn、10jqka、eastmoney、yuncaijing */
    List<NewsEntity> tuRequestNewsWithDate(Date startDate, Date endDate, String src) throws Exception;

    /** tushare增减持 */
    List<HoldingSharesEntity> tuRequestHoldingSharesChangeWithTradeDate(Date tradeDate) throws Exception;

    /** tushare获取沪深港通资金流向 */
    MoneyFlowEntity tuRequestMoneyFlowWithTradeDate(Date tradeDate) throws Exception;

    /** tushare获取各大交易所交易日历数据,默认提取的是上交所 */
    List<TradeCalendarEntity> tuRequestTradeCalendar(Date startDate, Date endDate) throws Exception;

    /** 获取所有标的信息， 类型可选
     * stock, fund, index, futures,
     * etf, lof, fja, fjb, QDII_fund, open_fund,
     * bond_fund, stock_fund, money_market_fund,
     * mixture_fund */
    List<JoinQuantSecurityEntity> joinQuantGetAllSecurities(String type) throws Exception;

    List<JoinQuantStockEntity> joinQuantGetStock(JoinQuantSecurityEntity securityEntity, Date tradeDate, String unit, Integer accountCode) throws Exception;

    /** 查询今天剩余调用数 */
    Long joinQuantGetQueryCount() throws Exception;

    /** joinQuant通过手机号和密码获取token */
    void joinQuantRequestToken() throws Exception;



}
