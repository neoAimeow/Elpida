package com.aimeow.Elpida.tools;

import java.util.Date;

import static com.aimeow.Elpida.tools.DateUtil.DATE_FORMAT_FULL;

public class ResultUtil {
    public static <T> Result<T> buildSuccessResult(Result<T> result, T t) {
        result.setSuccess(true);
        result.setModel(t);
        result.setErrorMsg("");
        result.setErrCode("");
        result.setTimestamp(DateUtil.formatDateToString(new Date(), DATE_FORMAT_FULL));
        return result;
    }

    public static <T> Result<T> setErrMsg(Result<T> result, String errorMessage) {
        result.setSuccess(false);
        result.setErrorMsg(errorMessage);
        result.setTimestamp(DateUtil.formatDateToString(new Date(), DATE_FORMAT_FULL));
        return result;
    }


    public static <T> Result<T> setErrMsgAndCode(Result<T> result, String errorMessage,String errorCode){
        setErrMsg(result, errorMessage);
        result.setErrCode(errorCode);
        return result;
    }

    public static Result getFailureResult(String errorMessage,String errorCode){
        Result result = new Result();
        result.setSuccess(false);
        result.setErrorMsg(errorMessage);
        result.setErrCode(errorCode);
        result.setTimestamp(DateUtil.formatDateToString(new Date(), DATE_FORMAT_FULL));
        return result;
    }

    public static Result getFailureResult(String errorMessage){
        return getFailureResult(errorMessage,null);
    }
}
