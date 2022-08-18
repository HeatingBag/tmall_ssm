/**
 * @Title: ProductImageService
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/10 15:37
 */
package com.how2java.tmall.service;

import com.how2java.tmall.pojo.ProductImage;

import java.util.List;

public interface ProductImageService {

    String type_single = "type_single";
    String type_detail = "type_detail";

    void add(ProductImage pi);

    void delete(int id);

    void update(ProductImage pi);

    ProductImage get(int id);

    List list(int cid, String type);
}
