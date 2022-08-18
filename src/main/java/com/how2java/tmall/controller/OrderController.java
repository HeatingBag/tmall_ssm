/**
 * @Title: OrderController
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/10 19:51
 */
package com.how2java.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.service.OrderItemService;
import com.how2java.tmall.service.OrderService;
import com.how2java.tmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderItemService orderItemService;

    /**
     * admin_order_list 导致OrderController.list()方法被调用
     * 1. 获取分页对象 PageHelper
     * 2. 查询订单集合
     * 3. 获取订单总数并设置在分页对象上
     * 4. 借助orderItemService.fill()方法为这些订单填充上orderItems信息
     * 5. 把订单集合和分页对象设置在model上
     * 6. 服务端跳转到admin/listOrder.jsp页面
     * 7. 在listOrder.jsp借助c:forEach把订单集合遍历出来
     * <c:forEach items="${os}" var="o">
     * 8. 遍历订单的时候，再把当前订单的orderItem订单项集合遍历出来
     *  <c:forEach items="${o.orderItems}" var="oi">
     * */

    @RequestMapping("admin_order_list")
    public String list(Model model, Page page) {

        PageHelper.offsetPage(page.getStart(), page.getCount());
        List<Order> os = orderService.list();
        int total = (int) new PageInfo<>(os).getTotal();
        page.setTotal(total);
        orderItemService.fill(os);
        model.addAttribute("os", os);
        model.addAttribute("page", page);

        return "admin/listOrder";
    }

    /**
     * 1. 发货按钮链接跳转到admin_order_delivery
     * 2. OrderController.delivery()方法被调用
     * 2.1 注入订单对象
     * 2.2 修改发货时间，设置发货状态
     * 2.3 更新到数据库
     * 2.4 客户端跳转到admin_order_list页面
     * */

    @RequestMapping("admin_order_delivery")
    public String delivery(Order o) throws IOException {
        o.setDeliveryDate(new Date());
        o.setStatus(OrderService.waitConfirm);
        orderService.update(o);

        return "redirect:admin_order_list";
    }


}
