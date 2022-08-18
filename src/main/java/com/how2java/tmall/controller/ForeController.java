/**
 * @Title: ForeController
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/11 8:30
 */
package com.how2java.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.how2java.tmall.pojo.*;
import com.how2java.tmall.service.*;
import comparator.*;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

@Controller
@RequestMapping("")
public class ForeController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @Autowired
    ProductImageService productImageService;

    @Autowired
    PropertyValueService propertyValueService;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    ReviewService reviewService;

    @RequestMapping("forehome")
    public String home(Model model) {
        List<Category> cs = categoryService.list();
        productService.fill(cs);
        productService.fillByRow(cs);
        model.addAttribute("cs", cs);
        return "fore/home";
    }

    @RequestMapping("foreregister")
    public String register(Model model, User user) {
        String name = user.getName();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);

        if (exist) {
            String m = "用户名已经被使用,不能使用";
            model.addAttribute("msg", m);
//            model.addAttribute("user", null);
            return "fore/register";
        }
        userService.add(user);

        return "redirect:registerSuccessPage";
    }

    @RequestMapping("forelogin")
    public String login(@RequestParam("name") String name, @RequestParam("password") String password, Model model, HttpSession session) {

        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name, password);

        if (null == user) {
            model.addAttribute("msg", "账号密码错误");
            return "fore/login";
        }
        session.setAttribute("user", user);
        return "redirect:forehome";
    }

    @RequestMapping("forelogout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:forehome";
    }

    @RequestMapping("foreproduct")
    public String product(int pid, Model model) {

        Product p = productService.get(pid);

        List<ProductImage> productSingleImages = productImageService.list(p.getId(), ProductImageService.type_single);
        List<ProductImage> productDetailImages = productImageService.list(p.getId(), ProductImageService.type_detail);
        p.setProductSingleImages(productSingleImages);
        p.setProductDetailImages(productDetailImages);

        List<PropertyValue> pvs = propertyValueService.list(p.getId());
        List<Review> reviews = reviewService.list(p.getId());
        productService.setSaleAndReviewNumber(p);
        model.addAttribute("reviews", reviews);
        model.addAttribute("p", p);
        model.addAttribute("pvs", pvs);
        return "fore/product";
    }

    @RequestMapping("forecheckLogin")
    @ResponseBody
    public String checkLogin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null != user)
            return "success";

        return "fail";

    }

    @RequestMapping("foreloginAjax")
    @ResponseBody
    public String loginAjax(@RequestParam("name") String name, @RequestParam("password") String password, HttpSession session) {

        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name, password);

        if (null == user) {
            return "fail";
        }
        session.setAttribute("user", user);
        return "success";
    }

    @RequestMapping("forecategory")
    public String category(int cid, String sort, Model model) {
        Category c = categoryService.get(cid);
        productService.fill(c);
        productService.setSaleAndReviewNumber(c.getProducts());

        if (null != sort) {
            switch (sort) {
                case "review":
                    Collections.sort(c.getProducts(), new ProductReviewComparator());
                    break;

                case "date":
                    Collections.sort(c.getProducts(), new ProductDateComparator());
                    break;

                case "saleCount":
                    Collections.sort(c.getProducts(), new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(c.getProducts(), new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(c.getProducts(), new ProductAllComparator());
                    break;
            }
        }
        model.addAttribute("c", c);
        return "fore/category";
    }

    @RequestMapping("foresearch")
    public String search(String keyword, Model model) {

        PageHelper.offsetPage(0, 20);
        List<Product> ps = productService.search(keyword);
        productService.setSaleAndReviewNumber(ps);
        model.addAttribute("ps", ps);
        return "fore/searchResult";
    }

    @RequestMapping("forebuyone")
    public String buyone(int pid, int num, HttpSession session) {
        Product p = productService.get(pid);
        int oiid = 0;

        User user = (User) session.getAttribute("user");
        boolean found = false;
        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        for (OrderItem oi : ois) {
            if (oi.getProduct().getId().intValue() == p.getId().intValue()) {
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                found = true;
                oiid = oi.getId();
                break;
            }
        }
        if (!found) {
            OrderItem oi = new OrderItem();
            oi.setUid(user.getId());
            oi.setNumber(num);
            oi.setPid(pid);
            orderItemService.add(oi);
            oiid = oi.getId();
        }
        return "redirect:forebuy?oiid=" + oiid;
    }

    @RequestMapping("forebuy")
    public String buy(Model model, String[] oiid, HttpSession session) {
        List<OrderItem> ois = new ArrayList<>();
        float total = 0;

        for (String strid : oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi = orderItemService.get(id);
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
            ois.add(oi);
        }

        session.setAttribute("ois", ois);
        model.addAttribute("total", total);
        return "fore/buy";
    }

    @RequestMapping("foreaddCart")
    @ResponseBody
    public String addCart(int pid, int num, Model model, HttpSession session) {
        Product p = productService.get(pid);
        User user = (User) session.getAttribute("user");
        boolean found = false;

        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        for (OrderItem oi : ois) {
            if (oi.getProduct().getId().intValue() == p.getId().intValue()) {
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                found = true;
                break;
            }
        }
        if (!found) {
            OrderItem oi = new OrderItem();
            oi.setUid(user.getId());
            oi.setNumber(num);
            oi.setPid(pid);
            orderItemService.add(oi);
        }
        return "success";
    }

    @RequestMapping("forecart")
    public String cart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        model.addAttribute("ois", ois);
        return "fore/cart";
    }

    /**
     * var page = "forechangeOrderItem";
     * 点击增加或者减少按钮后，根据 cartPage.jsp 中的js代码，会通过Ajax访问/forechangeOrderItem路径，
     * 导致ForeController.changeOrderItem()方法被调用
     * 1. 判断用户是否登录
     * 2. 获取pid和number
     * 3. 遍历出用户当前所有的未生成订单的OrderItem
     * 4. 根据pid找到匹配的OrderItem，并修改数量后更新到数据库
     * 5. 返回字符串"success"
     */

    @RequestMapping("forechangeOrderItem")
    @ResponseBody
    public String changeOrderItem(Model model, HttpSession session, int pid, int number) {
        User user = (User) session.getAttribute("user");
        if (null == user)
            return "fail";

        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        for (OrderItem oi : ois) {
            if (oi.getProduct().getId().intValue() == pid) {
                oi.setNumber(number);
                orderItemService.update(oi);
                break;
            }
        }
        return "success";
    }

    /**
     * var page = "foredeleteOrderItem";
     * 点击删除按钮后，根据 cartPage.jsp 中的js代码，会通过Ajax访问/foredeleteOrderItem路径，\
     * 导致ForeController.deleteOrderItem方法被调用
     * 1. 判断用户是否登录
     * 2. 获取oiid
     * 3. 删除oiid对应的OrderItem数据
     * 4. 返回字符串"success"
     */

    @RequestMapping("foredeleteOrderItem")
    @ResponseBody
    public String deleteOrderItem(Model model, HttpSession session, int oiid) {
        User user = (User) session.getAttribute("user");
        if (null == user)
            return "fail";

        orderItemService.delete(oiid);
        return "success";

    }

    /**
     * 提交订单访问路径 /forecreateOrder, 导致ForeController.createOrder 方法被调用
     * 1. 从session中获取user对象
     * 2. 通过参数Order接受地址，邮编，收货人，用户留言等信息
     * 3. 根据当前时间加上一个4位随机数生成订单号
     * 4. 根据上述参数，创建订单对象
     * 5. 把订单状态设置为等待支付
     * 6. 从session中获取订单项集合 ( 在结算功能的ForeController.buy() session.setAttribute("ois", ois);，订单项集合被放到了session中 )
     * 7. 把订单加入到数据库，并且遍历订单项集合，设置每个订单项的order，更新到数据库
     * 8. 统计本次订单的总金额
     * 9. 客户端跳转到确认支付页forealipay，并带上订单id和总金额
     */

    @RequestMapping("forecreateOrder")
    public String createOrder(Model model, Order order, HttpSession session) {
        User user = (User) session.getAttribute("user");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);

        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUid(user.getId());
        order.setStatus(OrderService.waitPay);
        List<OrderItem> ois = (List<OrderItem>) session.getAttribute("ois");

        float total = orderService.add(order, ois);
        return "redirect:forealipay?oid=" + order.getId() + "&total=" + total;

    }

    @RequestMapping("forepayed")
    public String payed(int oid, float total, Model model) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        model.addAttribute("o", order);
        return "fore/payed";

    }

    @RequestMapping("forebought")
    public String bought(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Order> os = orderService.list(user.getId(), OrderService.delete);

        orderItemService.fill(os);

        model.addAttribute("os", os);

        return "fore/bought";
    }

    @RequestMapping("foreconfirmPay")
    public String confirmPay(Model model, int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        model.addAttribute("o", o);

        return "fore/confirmPay";

    }

    @RequestMapping("foreorderConfirmed")
    public String orderConfirmed(Model model, int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.waitReview);
        o.setConfirmDate(new Date());
        orderService.update(o);
        return "fore/orderConfirmed";
    }

    @RequestMapping("foredeleteOrder")
    @ResponseBody
    public String deleteOrder(Model model, int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return "success";

    }

    /**
     * 通过点击评价按钮，来到路径/forereview，导致ForeController.review()方法被调用
     * <a href="forereview?oid=${o.id}">
     * 1.1 获取参数oid
     * 1.2 根据oid获取订单对象o
     * 1.3 为订单对象填充订单项
     * 1.4 获取第一个订单项对应的产品,因为在评价页面需要显示一个产品图片，那么就使用这第一个产品的图片了
     * 1.5 获取这个产品的评价集合
     * 1.6 为产品设置评价数量和销量
     * 1.7 把产品，订单和评价集合放在request上
     * 1.8 服务端跳转到 review.jsp
     */

    @RequestMapping("forereview")
    public String review(Model model, int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        Product p = o.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(p.getId());
        productService.setSaleAndReviewNumber(p);
        model.addAttribute("p", p);
        model.addAttribute("o", o);
        model.addAttribute("reviews", reviews);
        return "fore/review";
    }

    /**
     * 在评价产品页面点击提交评价，就把数据提交到了/foredoreview路径，导致ForeController.doreview方法被调用
     *  <form method="post" action="foredoreview">
     * 1. ForeController.doreview()
     * 1.1 获取参数oid
     * 1.2 根据oid获取订单对象o
     * 1.3 修改订单对象状态    o.setStatus(OrderService.finish);
     * 1.4 更新订单对象到数据库
     * 1.5 获取参数pid
     * 1.6 根据pid获取产品对象
     * 1.7 获取参数content (评价信息)
     * 1.8 对评价信息进行转义，道理同注册ForeController.register()
     * 1.9 从session中获取当前用户
     * 1.10 创建评价对象review
     * 1.11 为评价对象review设置 评价信息，产品，时间，用户
     * 1.12 增加到数据库
     * 1.13.客户端跳转到/forereview： 评价产品页面，并带上参数showonly=true
     * 2. reviewPage.jsp
     * 在reviewPage.jsp中，当参数showonly==true，那么就显示当前产品的所有评价信息
     * <c:if test="${param.showonly==true}">
     */

    /**
     * 加与不加@RequestParam标签的区别:
     * 1. 加了@RequestParam标签，那么前端在请求这个url的时候就必须有这个参数，否则后端映射失败。而@RequestParam("name")中的"name"参数，则定义了前端请求时的参数名。
     * 2. 不加@RequestParam标签，那么前端请求这个url时，参数可带可不带，后端都能映射成功。所以后端就要处理url没有带参数的情况。
     */

    @RequestMapping("foredoreview")
    public String doreview(Model model, HttpSession session, @RequestParam("oid") int oid, @RequestParam("pid") int pid, String content) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.finish);
        orderService.update(o);

        Product p = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        User user = (User) session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setPid(pid);
        review.setCreateDate(new Date());
        review.setUid(user.getId());
        reviewService.add(review);

        return "redirect:forereview?oid=" + oid + "&showonly=true";
    }

}
