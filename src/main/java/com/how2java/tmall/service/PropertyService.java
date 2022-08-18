/**
 * @Title: PropertyService
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/10 10:22
 */
package com.how2java.tmall.service;

import com.how2java.tmall.pojo.Property;

import java.util.List;

public interface PropertyService {

    void add(Property c);

    void delete(int id);

    void update(Property c);

    Property get(int id);

    List list(int cid);

}
