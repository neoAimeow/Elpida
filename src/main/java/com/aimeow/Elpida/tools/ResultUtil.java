package com.aimeow.Elpida.tools;

public class ResultUtil {
    public static <T> Result<T> buildSuccessResult(Result<T> result, T t) {
        result.setSuccess(true);
        result.setModel(t);
        result.setErrorMsg("");
        result.setErrCode("");
        return result;
    }

    public static <T> Result<T> setErrMsg(Result<T> result, String errorMessage) {
        result.setSuccess(false);
        result.setErrorMsg(errorMessage);
        return result;
    }


    public static <T> Result<T> setErrMsgAndCode(Result<T> result, String errorMessage,String errorCode){
        setErrMsg(result, errorMessage);
        result.setErrCode(errorCode);
        return result;
    }

    public static Result getFailureResult(String errorMessage,String errorCode){
        Result resultDO = new Result();
        resultDO.setSuccess(false);
        resultDO.setErrorMsg(errorMessage);
        resultDO.setErrCode(errorCode);
        return resultDO;
    }

    public static Result getFailureResult(String errorMessage){
        return getFailureResult(errorMessage,null);
    }
}
