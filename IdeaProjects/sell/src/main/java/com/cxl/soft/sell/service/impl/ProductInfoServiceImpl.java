package com.cxl.soft.sell.service.impl;

import com.cxl.soft.sell.dataobject.ProductInfo;
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
}
