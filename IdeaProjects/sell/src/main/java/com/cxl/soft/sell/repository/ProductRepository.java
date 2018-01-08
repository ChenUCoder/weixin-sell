package com.cxl.soft.sell.repository;

import com.cxl.soft.sell.dataobject.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductInfo,String>{


}
