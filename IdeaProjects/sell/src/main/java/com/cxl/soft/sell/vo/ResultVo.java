package com.cxl.soft.sell.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResultVo<T> implements Serializable{
    private Integer code;
    private String msg;
    private T data;
}
