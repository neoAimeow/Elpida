package com.aimeow.Elpida.tools;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 3440085496479143237L;
    protected boolean success = true;
    protected T model;
    protected String errCode;
    protected String errorMsg;
}
