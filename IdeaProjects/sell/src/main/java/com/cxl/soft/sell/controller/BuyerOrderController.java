package com.cxl.soft.sell.controller;

import com.cxl.soft.sell.convert.ConvertOrderForm2OrderDto;
import com.cxl.soft.sell.dto.OrderDto;
import com.cxl.soft.sell.enums.ExceptionCodeEnums;
import com.cxl.soft.sell.exception.SellException;
import com.cxl.soft.sell.form.OrderForm;
import com.cxl.soft.sell.service.impl.OrderServiceImpl;
import com.cxl.soft.sell.utils.ResultUtil;
import com.cxl.soft.sell.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/buyer/order")
@Slf4j
public class BuyerOrderController {

    @Autowired
    private OrderServiceImpl orderService;



    @PostMapping("/create")
    public ResultVo<Map<String,String>> create(@Valid OrderForm orderForm, BindingResult bindingResult){
        //验证表单数据是否可用
        if (bindingResult.hasErrors()){
            log.error("订单创建信息不正确：orderForm={}",orderForm);
            throw new SellException(ExceptionCodeEnums.PARAM_ERROR,bindingResult.getFieldError().getDefaultMessage());
        }
        OrderDto orderDto = ConvertOrderForm2OrderDto.convert(orderForm);
        if (CollectionUtils.isEmpty(orderDto.getOrderDetailList())){
            log.error("购物车不能为空：orderDto={}",orderDto);
            throw new SellException(ExceptionCodeEnums.Order_CART_EMPTY);
        }
        OrderDto result = orderService.create(orderDto);
        Map<String, String> map = new HashMap<>();
        map.put("orderId",result.getOrderId());
        return ResultUtil.success(map);
    }




    //list
    @GetMapping("/list")
    public ResultVo<List<OrderDto>> list(@RequestParam("openid") String openid,
                                         @RequestParam(value = "page",defaultValue = "0") Integer page,
                                         @RequestParam(value = "size",defaultValue = "10") Integer size){
        if (StringUtils.isEmpty(openid)){
            log.error("openid为空");
            throw  new SellException(ExceptionCodeEnums.PARAM_ERROR);
        }

        PageRequest request = new PageRequest(page, size);
        Page<OrderDto> list = orderService.findList(openid, request);


        return ResultUtil.success(list.getContent());
    }








}
