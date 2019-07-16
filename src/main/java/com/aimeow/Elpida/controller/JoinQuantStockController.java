package com.aimeow.Elpida.controller;

import com.aimeow.Elpida.entity.joinQuant.JoinQuantSecurityEntity;
import com.aimeow.Elpida.tools.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/stock")
public interface JoinQuantStockController {

    @RequestMapping(value = "jqGetAllSecurities", method = RequestMethod.GET)
    /** 获取所有标的信息， 类型可选
     * stock, fund, index, futures,
     * etf, lof, fja, fjb, QDII_fund, open_fund,
     * bond_fund, stock_fund, money_market_fund,
     * mixture_fund */
    Result<List<JoinQuantSecurityEntity>> joinQuantGetAllSecurities(String type) throws Exception ;

    @RequestMapping(value = "/joinTest", method = RequestMethod.GET)
    Result test() throws Exception;

    @RequestMapping(value = "/joinTest2", method = RequestMethod.GET)
    Result test2() throws Exception;

    @RequestMapping(value = "/joinTest3", method = RequestMethod.GET)
    Result test3() throws Exception;

}
