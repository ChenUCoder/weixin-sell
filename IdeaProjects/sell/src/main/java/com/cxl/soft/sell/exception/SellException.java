package com.cxl.soft.sell.exception;

import com.cxl.soft.sell.enums.ExceptionCodeEnums;

/**
 * 异常状态码
 * */
public class SellException extends RuntimeException {
    private Integer code;

    public SellException (ExceptionCodeEnums exceptionCodeEnums){
        super(exceptionCodeEnums.getMsg());
        this.code=exceptionCodeEnums.getCode();
    }

    public SellException (ExceptionCodeEnums exceptionCodeEnums,String msg){
        super(msg);
        this.code=exceptionCodeEnums.getCode();
    }


}
