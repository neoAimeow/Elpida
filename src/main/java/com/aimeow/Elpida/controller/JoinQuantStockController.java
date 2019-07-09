package com.aimeow.Elpida.controller;

import com.aimeow.Elpida.tools.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/stock")
public interface JoinQuantStockController {

    @RequestMapping(value = "/joinTest", method = RequestMethod.GET)
    Result test() throws Exception;

    @RequestMapping(value = "/joinTest2", method = RequestMethod.GET)
    Result test2() throws Exception;

    @RequestMapping(value = "/joinTest3", method = RequestMethod.GET)
    Result test3() throws Exception;

}
