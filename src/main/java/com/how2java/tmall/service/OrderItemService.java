/**
 * @Title: OrderItemItemService
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/10 17:36
 */
package com.how2java.tmall.service;

import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;

import java.util.List;

public interface OrderItemService {

    void add(OrderItem c);

    void delete(int id);

    void update(OrderItem c);

    OrderItem get(int id);

    List list();

    void fill(List<Order> os);

    void fill(Order o);

    int getSaleCount(int pid);

    List<OrderItem> listByUser(int uid);
}
