package com.cxl.soft.sell.service.impl;

import com.cxl.soft.sell.dataobject.OrderDetail;
import com.cxl.soft.sell.dataobject.OrderMaster;
import com.cxl.soft.sell.dataobject.ProductInfo;
import com.cxl.soft.sell.dto.CartDto;
import com.cxl.soft.sell.dto.OrderDto;
import com.cxl.soft.sell.enums.ExceptionCodeEnums;
import com.cxl.soft.sell.enums.OrderStatusEnums;
import com.cxl.soft.sell.enums.PayStatusEnums;
import com.cxl.soft.sell.exception.SellException;
import com.cxl.soft.sell.repository.OrderDetailRepository;
import com.cxl.soft.sell.repository.OrderMasterRepository;
import com.cxl.soft.sell.service.OrderService;
import com.cxl.soft.sell.service.ProductInfoService;
import com.cxl.soft.sell.utils.KeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMasterRepository masterRepository;
    @Autowired
    private ProductInfoService productInfoService;
    @Autowired
    private OrderDetailRepository detailRepository;



    @Override
    public OrderDto create(OrderDto orderDto) {
        //订单总价
        BigDecimal orderAmount=new BigDecimal(0);


        String orderId= KeyUtil.gen();
        for (OrderDetail orderDetail : orderDto.getOrderDetailList()) {
            //判断是否有这个商品
            String productId = orderDetail.getProductId();
            ProductInfo productInfo = productInfoService.findOne(productId);
            if (null == productInfo){
                throw new SellException(ExceptionCodeEnums.PRODUCT_NOT_FOUND);
            }


            //计算本单总价格
            BigDecimal productPrice = productInfo.getProductPrice();
            orderAmount=productPrice.multiply(new BigDecimal(orderDetail.getProductQuantity())).add(orderAmount);//a*b+c

            //存订单详情数据
            BeanUtils.copyProperties(productInfo,orderDetail);
            orderDetail.setDetailId(KeyUtil.gen());
            orderDetail.setOrderId(orderId);
            detailRepository.save(orderDetail);
        }

        //存订单主表
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties(orderDto,orderMaster);
        orderMaster.setOrderId(orderId);
        orderMaster.setOrderStatus(OrderStatusEnums.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnums.NEW.getCode());
        orderMaster.setOrderAmount(orderAmount);
        masterRepository.save(orderMaster);

        //存购物车
        List<CartDto> cartDtoList = orderDto.getOrderDetailList().stream()
                .map(e -> new CartDto(e.getProductId(), e.getProductQuantity()))
                .collect(Collectors.toList());
        productInfoService.decreaseStock(cartDtoList);
        //
        return orderDto;
    }

    @Override
    public OrderDto findOne(String orderId) {
        //查找订单主表
        OrderMaster orderMaster = masterRepository.findOne(orderId);
        if (null == orderMaster){
            throw new SellException(ExceptionCodeEnums.ORDER_NOT_FOUND);
        }
        //查找订单详情表
        List<OrderDetail> orderDetailList = detailRepository.findByOrderId(orderId);
        if (CollectionUtils.isEmpty(orderDetailList)){
            throw new SellException(ExceptionCodeEnums.ORDER_DETAIL_NOT_FOUND);
        }
        //最后set到Dto中去
        OrderDto orderDto = new OrderDto();
        BeanUtils.copyProperties(orderMaster,orderDto);
        orderDto.setOrderDetailList(orderDetailList);
        return orderDto;
    }

    @Override
    public Page<OrderDto> findList(String buyerOpenid, Pageable pageable) {
        return null;
    }

    @Override
    public OrderDto cancel(OrderDto orderDTO) {
        return null;
    }

    @Override
    public OrderDto finish(OrderDto orderDTO) {
        return null;
    }

    @Override
    public OrderDto pay(OrderDto orderDTO) {
        return null;
    }

    @Override
    public Page<OrderDto> findList(Pageable pageable) {
        return null;
    }
}