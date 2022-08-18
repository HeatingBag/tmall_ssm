/**
 * @Title: PropertyController
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/10 10:31
 */
package com.how2java.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.PropertyService;
import com.how2java.tmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("")
public class PropertyController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    PropertyService propertyService;

    @RequestMapping("admin_property_add")
    public String add(Model model, Property p) {

        propertyService.add(p);
        return "redirect:admin_property_list?cid=" + p.getCid();
    }

    @RequestMapping("admin_property_delete")
    public String delete(int id) {

        Property p = propertyService.get(id);
        propertyService.delete(id);
        return "redirect:admin_property_list?cid=" + p.getCid();
    }

    /**
     * 1. 在PropertyController的edit方法中，根据id获取Property对象
     * 2. 根据property对象的cid属性获取Category对象，并把其设置在Property对象的category属性上
     * 3. 把Property对象放在request的 "p" 属性中
     * 4. 服务端跳转到admin/editProperty.jsp
     * 5. 在editProperty.jsp中显示属性名称
     * 6. 在editProperty.jsp中隐式提供id和cid( cid 通过 p.category.id 获取)
     * <input type="hidden" name="id" value="${p.id}">
     * <input type="hidden" name="cid" value="${p.category.id}">
     * */

    @RequestMapping("admin_property_edit")
    public String edit(Model model, int id) {

        Property p = propertyService.get(id);
        Category c = categoryService.get(p.getCid());
        p.setCategory(c);
        model.addAttribute("p", p);
        return "admin/editProperty";
    }

    @RequestMapping("admin_property_update")
    public String update(Property p) {

        propertyService.update(p);
        return "redirect:admin_property_list?cid=" + p.getCid();
    }

    /**
     * 1. 获取分类 cid,和分页对象page
     * 2. 通过PageHelper设置分页参数
     * 3. 基于cid，获取当前分类下的属性集合
     * 4. 通过PageInfo获取属性总数
     * 5. 把总数设置给分页page对象
     * 6. 拼接字符串"&cid="+c.getId()，设置给page对象的Param值。 因为属性分页都是基于当前分类下的分页，所以分页的时候需要传递这个cid
     * 7. 把属性集合设置到 request的 "ps" 属性上 <c:forEach items="${ps}" var="p">
     * 8. 把分类对象设置到 request的 "c" 属性上
     * 9. 把分页对象设置到 request的 "page" 对象上
     * 10. 服务端跳转到admin/listProperty.jsp页面
     * */

    @RequestMapping("admin_property_list")
    public String list(int cid, Model model, Page page) {

        Category c = categoryService.get(cid);

        PageHelper.offsetPage(page.getStart(), page.getCount());
        List<Property> ps = propertyService.list(cid);

        int total = (int) new PageInfo<>(ps).getTotal();
        page.setTotal(total);
        page.setParam("&cid=" + c.getId());

        model.addAttribute("ps", ps);
        model.addAttribute("c", c);
        model.addAttribute("page", page);

        return "admin/listProperty";

    }
}
