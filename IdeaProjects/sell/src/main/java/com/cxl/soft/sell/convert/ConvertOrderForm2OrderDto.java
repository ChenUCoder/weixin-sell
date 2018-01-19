package com.cxl.soft.sell.convert;

import com.cxl.soft.sell.dataobject.OrderDetail;
import com.cxl.soft.sell.dto.OrderDto;
import com.cxl.soft.sell.enums.ExceptionCodeEnums;
import com.cxl.soft.sell.exception.SellException;
import com.cxl.soft.sell.form.OrderForm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
@Slf4j
public class ConvertOrderForm2OrderDto {
    public static OrderDto convert(OrderForm orderForm){
        OrderDto orderDto = new OrderDto();
        orderDto.setBuyerName(orderForm.getName());
        orderDto.setBuyerAddress(orderForm.getAddress());
        orderDto.setBuyerPhone(orderForm.getPhone());
        orderDto.setBuyerOpenid(orderForm.getOpenid());
        Gson gson=new Gson();
        List<OrderDetail> orderDetails = new ArrayList<>();
        try{
            orderDetails=gson.fromJson(orderForm.getItems(),new TypeToken<List<OrderDetail>>(){}.getType());

        }catch (Exception e){
            log.error("form2orderDto转换失败:string={}",orderForm.getItems());
            throw new SellException(ExceptionCodeEnums.PARAM_ERROR);
        }
        orderDto.setOrderDetailList(orderDetails);
        return orderDto;
    }
}
