package com.cxl.soft.sell.service;

import com.cxl.soft.sell.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 订单service
 */
public interface OrderService {
    /**
     * 创建订单
     * @param orderDTO
     * @return
     */
    OrderDto create(OrderDto orderDTO);

    /**查找单个订单.*/
    OrderDto findOne(String orderId);

    /**查找订单.**/
    Page<OrderDto> findList(String buyerOpenid , Pageable pageable);

    /**取消订单.**/
    OrderDto cancel(OrderDto orderDTO);

    /**完结订单.**/
    OrderDto finish(OrderDto orderDTO);

    /**支付订单.**/
    OrderDto pay(OrderDto orderDTO);

    /**查找订单.**/
    Page<OrderDto> findList(Pageable pageable);




}
