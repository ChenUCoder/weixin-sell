package com.cxl.soft.sell.service.impl;

import com.cxl.soft.sell.dataobject.ProductCategory;
import com.cxl.soft.sell.repository.CategoryRepository;
import com.cxl.soft.sell.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository repository;
    @Override
    public List<ProductCategory> findCategoryTypeIn(List<Integer> categoryTypes) {
        return repository.findByCategoryTypeIn(categoryTypes);
    }
}
