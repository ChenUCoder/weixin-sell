package com.cxl.soft.sell.controller;

import com.cxl.soft.sell.dataobject.ProductCategory;
import com.cxl.soft.sell.dataobject.ProductInfo;
import com.cxl.soft.sell.service.impl.CategoryServiceImpl;
import com.cxl.soft.sell.service.impl.ProductInfoServiceImpl;
import com.cxl.soft.sell.utils.serializer.ResultUtil;
import com.cxl.soft.sell.vo.ProductInfoVo;
import com.cxl.soft.sell.vo.ProductVo;
import com.cxl.soft.sell.vo.ResultVo;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/buyer/product")
public class BuyerProductController {
    @Autowired
    private ProductInfoServiceImpl productInfoService;
    @Autowired
    private CategoryServiceImpl categoryService;
    @GetMapping("/list")
    public ResultVo list(){
        List<ProductInfo> productInfos = productInfoService.findAll();
        /*List<Integer> categoryTypes=new ArrayList<Integer>();
        for (ProductInfo productInfo:productInfos ) {
            categoryTypes.add(productInfo.getCategoryType());
        }*/
        List<Integer> categoryTypes = productInfos.stream().map(e -> e.getCategoryType()).collect(Collectors.toList());


        List<ProductCategory> categoryTypeIn = categoryService.findCategoryTypeIn(categoryTypes);

        //拼接VO
        List<ProductVo> productInfoVos = new ArrayList<>();
        for (ProductCategory productCategory:categoryTypeIn) {
            ProductVo productVo = new ProductVo();
            productVo.setCategoryName(productCategory.getCategoryName());
            productVo.setCategoryType(productCategory.getCategoryType());

            ArrayList<ProductInfoVo> productInfosList = new ArrayList<>();
            for (ProductInfo productInfo:productInfos) {
                if (productCategory.getCategoryType().equals(productInfo.getCategoryType())){
                    ProductInfoVo productInfoVo = new ProductInfoVo();
                    BeanUtils.copyProperties(productInfo,productInfoVo);
                    productInfosList.add(productInfoVo);
                }
            }
            productVo.setProductInfos(productInfosList);
            productInfoVos.add(productVo);
        }
        return ResultUtil.success(productInfoVos);
    }

}
