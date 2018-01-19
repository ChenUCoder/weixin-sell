package com.cxl.soft.sell.service.impl;

import com.cxl.soft.sell.convert.ConvertOrderMaster2OrderDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMasterRepository masterRepository;
    @Autowired
    private ProductInfoService productInfoService;
    @Autowired
    private OrderDetailRepository detailRepository;



    @Override
    @Transactional
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
        orderDto.setOrderId(orderId);
        BeanUtils.copyProperties(orderDto,orderMaster);
        orderMaster.setOrderStatus(OrderStatusEnums.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnums.NEW.getCode());
        orderMaster.setOrderAmount(orderAmount);
        masterRepository.save(orderMaster);

        //存购物车
        List<CartDto> cartDtoList = orderDto.getOrderDetailList().stream()
                .map(e -> new CartDto(e.getProductId(), e.getProductQuantity()))
                .collect(Collectors.toList());
        productInfoService.decreaseStock(cartDtoList);
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
        Page<OrderMaster> byBuyerOpenid = masterRepository.findByBuyerOpenid(buyerOpenid, pageable);
        List<OrderDto> convert = ConvertOrderMaster2OrderDto.convert(byBuyerOpenid.getContent());
        return new PageImpl<OrderDto>(convert,pageable,byBuyerOpenid.getTotalElements());
    }

    @Override
    public OrderDto cancel(OrderDto orderDTO) {
        if (! OrderStatusEnums.NEW.getCode().equals(orderDTO.getOrderStatus())){
            log.error("【取消订单】订单状态不正确：orderId={},orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ExceptionCodeEnums.ORDER_STATUS_ERROR);
        }
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderStatus(OrderStatusEnums.CANCEL.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster updateMaster = masterRepository.save(orderMaster) ;
        if (null == updateMaster){
            log.error("取消订单失败：orderMaster={}",orderMaster);
            throw new SellException(ExceptionCodeEnums.ORDER_STATUS_UPDATE_FAIL);
        }
        if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())){
            log.error("订单中没有商品:orderDto={}",orderDTO);
            throw new SellException(ExceptionCodeEnums.PRODUCT_NOT_FOUND);
        }
        List<CartDto> cartDtos = orderDTO.getOrderDetailList().stream()
                .map(e -> new CartDto(e.getProductId(), e.getProductQuantity()))
                .collect(Collectors.toList());
        productInfoService.increaseStock(cartDtos);



        //退款
        if (orderDTO.getPayStatus().equals(PayStatusEnums.FINISH.getCode())){
            //ToDo
        }









        return orderDTO;
    }

    @Override
    public OrderDto finish(OrderDto orderDTO) {
        //订单不是新建订单
        if (!OrderStatusEnums.NEW.getCode().equals(orderDTO.getOrderStatus())){
            log.error("订单状态不正确:orderId={},orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ExceptionCodeEnums.ORDER_PAY_STATUS_ERROR);
        }
        //更新订单
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderStatus(OrderStatusEnums.FINISH.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster save = masterRepository.save(orderMaster);
        if (null==save){
            log.error("更新失败，orderId={},orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ExceptionCodeEnums.ORDER_STATUS_UPDATE_FAIL);
        }
        return orderDTO;
    }

    @Override
    public OrderDto pay(OrderDto orderDTO) {
        //是否新建支付
        if (!PayStatusEnums.NEW.getCode().equals(orderDTO.getPayStatus())) {
            log.error("订单更新失败  订单状态不正常：orderId={},orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ExceptionCodeEnums.ORDER_STATUS_UPDATE_FAIL);
        }


        //更新订单
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderStatus(OrderStatusEnums.FINISH.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster save = masterRepository.save(orderMaster);
        if (null==save){
            log.error("订单支付更新失败，orderId={},orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellException(ExceptionCodeEnums.ORDER_STATUS_UPDATE_FAIL);
        }
        return orderDTO;
    }

    @Override
    public Page<OrderDto> findList(Pageable pageable) {
        return null;
    }
}
