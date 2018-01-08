package com.cxl.soft.sell.repository;

import com.cxl.soft.sell.dataobject.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,String> {
    /**
     * 根据订单id获取订单详情
     * @param orderId
     * @return
     */
    List<OrderDetail> findByOrderId(String orderId);

}
