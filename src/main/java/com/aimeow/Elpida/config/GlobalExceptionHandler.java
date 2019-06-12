package com.aimeow.Elpida.config;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class})
    public String exception(Exception e) {
        System.out.println(e.getMessage());
        return "出错了:" + e.getMessage();
    }
}
