package com.cxl.soft.sell.service.impl;

import com.cxl.soft.sell.dataobject.OrderDetail;
import com.cxl.soft.sell.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class OrderServiceImplTest {
    @Autowired
    private OrderServiceImpl orderService;
    private final String BUYER_OPENID="110110";


    @Test
    public void create() {
        OrderDto orderDTO = new OrderDto();
        orderDTO.setBuyerOpenid(BUYER_OPENID);
        orderDTO.setBuyerPhone("123456789012");
        orderDTO.setBuyerAddress("广州从师科技有限公司");
        orderDTO.setBuyerName("范先生");

        List<OrderDetail> orderDetailList = new ArrayList<>();
        OrderDetail o1 = new OrderDetail();
        o1.setProductId("123458");
        o1.setProductQuantity(2);

        OrderDetail o2 = new OrderDetail();
        o2.setProductId("123456");
        o2.setProductQuantity(5);


        orderDetailList.add(o1);
        orderDetailList.add(o2);
        orderDTO.setOrderDetailList(orderDetailList);

        OrderDto result = orderService.create(orderDTO);
        log.info("【创建订单】result={}" , result);
        Assert.assertNotNull(result);



    }

    @Test
    public void findOne() {
    }

    @Test
    public void findList() {
    }

    @Test
    public void cancel() {
    }

    @Test
    public void finish() {
    }

    @Test
    public void pay() {
    }

    @Test
    public void findList1() {
    }
}