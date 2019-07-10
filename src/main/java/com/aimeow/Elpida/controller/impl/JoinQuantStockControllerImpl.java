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
    public Result test() throws Exception {
        return ResultUtil.buildSuccessResult(new Result<>(), stockRequestWrapper.joinQuantGetQueryCount());
    }

    @Override
    public Result test2() throws Exception {
        return ResultUtil.buildSuccessResult(new Result<>(), stockRequestWrapper.joinQuantGetAllSecurities("stock"));
    }

    @Override
    public Result test3() throws Exception {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "3");

        List<JoinQuantSecurityEntity> joinQuantSecurityEntities = stockRequestWrapper
                .joinQuantGetAllSecurities("stock");

        joinQuantSecurityEntities.parallelStream().forEach(
                obj -> {
                    try {
                        stockRequestWrapper.joinQuantGetStock(obj,
                                DateUtil.formatStringToDate("2019-07-09", "yyyy-MM-dd"), "1d");
                    } catch (Exception ex) {

                    }
                }
        );

//        List<List<JoinQuantSecurityEntity>> splitArray = new ArrayList<>();
//
//        splitArray.add(new ArrayList<>());
//        splitArray.add(new ArrayList<>());
//        splitArray.add(new ArrayList<>());
//
//
//
//        for (int i = 0; i < joinQuantSecurityEntities.size(); i++ ) {
//            List<JoinQuantSecurityEntity> temp = splitArray.get(i / 1000);
//            temp.add(joinQuantSecurityEntities.get(i));
//        }

//        for (List<JoinQuantSecurityEntity> array : splitArray) {
//
//            Thread.sleep(10000);
//        }

        return ResultUtil.buildSuccessResult(new Result<>(), null);
    }
}
