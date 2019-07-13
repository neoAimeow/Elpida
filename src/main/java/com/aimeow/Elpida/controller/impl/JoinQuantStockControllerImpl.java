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

        List<List<JoinQuantSecurityEntity>> splitArray = new ArrayList<>();

        splitArray.add(new ArrayList<>());
        splitArray.add(new ArrayList<>());
        splitArray.add(new ArrayList<>());

        for (int i = 0; i < joinQuantSecurityEntities.size(); i++ ) {
            List<JoinQuantSecurityEntity> temp = splitArray.get(i / 1500);
            temp.add(joinQuantSecurityEntities.get(i));
        }

        for (int i = 0; i < 3; i++) {
            List<JoinQuantSecurityEntity> array = splitArray.get(i);
            System.out.println("joinQuantSecurityEntity size is " + array.size());
            final Integer num = i;
            array.parallelStream().forEach(
                    obj -> {
                        try {
                            stockRequestWrapper.joinQuantGetStock(obj,
                                    DateUtil.formatStringToDate("2019-07-09", "yyyy-MM-dd"), "1d", num);
                        } catch (Exception ex) {

                        }
                    }
            );
        }

        return ResultUtil.buildSuccessResult(new Result<>(), null);
    }
}
