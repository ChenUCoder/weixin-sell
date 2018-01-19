package com.cxl.soft.sell.service;

import com.cxl.soft.sell.dataobject.ProductCategory;

import java.util.List;

/**
 * 类目service
 */
public interface CategoryService {
    List<ProductCategory> findCategoryTypeIn(List<Integer> categoryTypes);

}
