package com.cxl.soft.sell.service;

import com.cxl.soft.sell.dataobject.ProductInfo;
import com.cxl.soft.sell.dto.CartDto;

import java.util.List;

/**
 * 商品信息service
 */
public interface ProductInfoService {

    List<ProductInfo> findAll();
    //查询一件商品
    ProductInfo findOne(String productId);
    //减少库存
    void increaseStock(List<CartDto> cartDtos);
    //增加库存
    void decreaseStock(List<CartDto> cartDtos);


}
