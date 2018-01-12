package com.cxl.soft.sell.convert;

import com.cxl.soft.sell.dataobject.OrderMaster;
import com.cxl.soft.sell.dto.OrderDto;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ConvertOrderMaster2OrderDto {
    public static OrderDto convert(OrderMaster orderMaster){
        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(orderMaster,orderDto);
        return orderDto;
    }
    public static List<OrderDto> convert(List<OrderMaster> orderMasters){
        return orderMasters.stream().map(e -> convert(e)).collect(Collectors.toList());
    }
}
