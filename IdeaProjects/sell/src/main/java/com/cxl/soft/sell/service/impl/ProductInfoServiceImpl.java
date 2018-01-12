package com.cxl.soft.sell.service.impl;

import com.cxl.soft.sell.dataobject.ProductInfo;
import com.cxl.soft.sell.dto.CartDto;
import com.cxl.soft.sell.enums.ExceptionCodeEnums;
import com.cxl.soft.sell.enums.ProductStatusEnums;
import com.cxl.soft.sell.exception.SellException;
import com.cxl.soft.sell.repository.ProductRepository;
import com.cxl.soft.sell.service.ProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductInfoServiceImpl implements ProductInfoService {
    @Autowired
    private ProductRepository repository;
    @Override
    public List<ProductInfo> findAll() {
        return repository.findAll();
    }

    @Override
    public ProductInfo findOne(String productId) {
        return repository.findOne(productId);
    }

    @Override
    public void increaseStock(List<CartDto> cartDtos) {
        for (CartDto cartDto : cartDtos) {
            ProductInfo productInfo = repository.findOne(cartDto.getProductId());
            if (null == productInfo){
                throw new SellException(ExceptionCodeEnums.PRODUCT_NOT_FOUND);
            }
            productInfo.setProductStock(productInfo.getProductStock()+cartDto.getProductQuantity());
            repository.save(productInfo);
        }
    }

    @Override
    public void decreaseStock(List<CartDto> cartDtos) {
        for (CartDto cartDto : cartDtos) {
            ProductInfo productInfo = repository.findOne(cartDto.getProductId());

            if (null == productInfo){
                throw new SellException(ExceptionCodeEnums.PRODUCT_NOT_FOUND);
            }
            int stock = productInfo.getProductStock() - cartDto.getProductQuantity();
            if (stock < 0){
                throw new SellException(ExceptionCodeEnums.PRODUCT_STOCK_ERROR);
            }

            productInfo.setProductStock(stock);
            repository.save(productInfo);
        }
    }
}
