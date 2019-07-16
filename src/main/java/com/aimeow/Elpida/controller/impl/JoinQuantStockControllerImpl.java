package com.aimeow.Elpida.controller.impl;

import com.aimeow.Elpida.controller.JoinQuantStockController;
import com.aimeow.Elpida.entity.joinQuant.JoinQuantSecurityEntity;
import com.aimeow.Elpida.entity.joinQuant.JoinQuantStockEntity;
import com.aimeow.Elpida.tools.DateUtil;
import com.aimeow.Elpida.tools.Result;
import com.aimeow.Elpida.tools.ResultUtil;
import com.aimeow.Elpida.wrapper.StockRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class JoinQuantStockControllerImpl implements JoinQuantStockController {

    @Autowired
    StockRequestWrapper stockRequestWrapper;


    @Override
    public Result<List<JoinQuantSecurityEntity>> joinQuantGetAllSecurities(String type) throws Exception {
        return ResultUtil.buildSuccessResult(new Result<>(), stockRequestWrapper.joinQuantGetAllSecurities(type));
    }

    @Override
    public Result test() throws Exception {
        stockRequestWrapper.joinQuantRequestToken();
        return ResultUtil.buildSuccessResult(new Result<>(), stockRequestWrapper.joinQuantGetQueryCount());
    }

    @Override
    public Result test2() throws Exception {
        return ResultUtil.buildSuccessResult(new Result<>(), stockRequestWrapper.joinQuantGetAllSecurities("stock"));
    }

    @Override
    public Result test3() throws Exception {
//        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "3");

        List<JoinQuantSecurityEntity> joinQuantSecurityEntities = stockRequestWrapper
                .joinQuantGetAllSecurities("stock");

        joinQuantSecurityEntities.parallelStream().forEach(
                obj -> {
                    try {
                        stockRequestWrapper.joinQuantGetStock(obj,
                                DateUtil.formatStringToDate("2019-07-09", "yyyy-MM-dd"), "1d", obj.getNumber() / 1500);
                    } catch (Exception ex) {

                    }
                }
        );

        return ResultUtil.buildSuccessResult(new Result<>(), null);
    }
}
