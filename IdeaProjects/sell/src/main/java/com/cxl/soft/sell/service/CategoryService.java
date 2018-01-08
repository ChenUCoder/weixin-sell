package com.cxl.soft.sell.service;

import com.cxl.soft.sell.dataobject.ProductCategory;

import java.util.List;

public interface CategoryService {
    List<ProductCategory> findCategoryTypeIn(List<Integer> categoryTypes);

}
