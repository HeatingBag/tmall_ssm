/**
 * @Title: PropertyValueService
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/10 16:40
 */
package com.how2java.tmall.service;

import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.PropertyValue;

import java.util.List;

public interface PropertyValueService {

    void init(Product p);

    void update(PropertyValue pv);

    PropertyValue get(int ptid, int pid);

    List<PropertyValue> list(int pid);

}
