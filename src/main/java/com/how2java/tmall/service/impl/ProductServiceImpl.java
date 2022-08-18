/**
 * @Title: ProductServiceImpl
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/10 15:10
 */
package com.how2java.tmall.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.how2java.tmall.mapper.ProductMapper;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.ProductExample;
import com.how2java.tmall.pojo.ProductImage;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.OrderItemService;
import com.how2java.tmall.service.ProductImageService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.service.ReviewService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductImageService productImageService;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    ReviewService reviewService;

    @Override
    public void add(Product p) {
        productMapper.insert(p);
    }

    @Override
    public void delete(int id) {
        productMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Product p) {

        productMapper.updateByPrimaryKeySelective(p);

    }

    @Override
    public Product get(int id) {
        /*在get方法中调用setFirstProductImage(Product p) 为单个产品设置图片*/
        Product p = productMapper.selectByPrimaryKey(id);
        setFirstProductImage(p);
        setCategory(p);
        return p;
    }

    public void setCategory(List<Product> ps) {

        for (Product p : ps)
            setCategory(p);
    }

    public void setCategory(Product p) {

        int cid = p.getCid();
        Category c = categoryService.get(cid);
        p.setCategory(c);
    }

    @Override
    public List list(int cid) {

        /*在list方法中调用setFirstProductImage(List<Product> ps) 为多个产品设置图片*/
        ProductExample example = new ProductExample();
        example.createCriteria().andCidEqualTo(cid);
        example.setOrderByClause("id desc");
        List result = productMapper.selectByExample(example);
        setCategory(result);
        setFirstProductImage(result);
        return result;
    }

    @Override
    public void setFirstProductImage(Product p) {

        List<ProductImage> pis = productImageService.list(p.getId(), ProductImageService.type_single);
        if (!pis.isEmpty()) {
            ProductImage pi = pis.get(0);
            p.setFirstProductImage(pi);
        }
    }

    /*为多个分类填充产品集合（遍历每一个分类）将每一个分类进行fill(Category c)操作，目的是添加产品集合进去*/
    @Override
    public void fill(List<Category> cs) {
        for (Category c : cs) {
            fill(c);
        }
    }

    /*为每一个分类填充该分类下的所有产品集合（根据产品表的cid字段进行查询），再将产品集合注入分类集合里*/
    @Override
    public void fill(Category c) {
        List<Product> ps = list(c.getId());
        c.setProducts(ps);
    }

    /**
     * 为所有分类填充产品集合后，即到此所有分类下就有所属的产品集合，按照8个为一行，拆成多行，以利于后续页面上进行显示
     * 每行显示的产品个数   int productNumberEachRow = 8;
     * 对每一个分类进行遍历
     * 取出当前分类下的所有产品，单独存入products集合中   List<Product> products =  c.getProducts();
     * 新建一个productsByRow集合，第一次为null
     * 每次循环都是8个 8个地装入，第一次size=0+8，第二次size=8+8以此类推
     * 判断size大小，若size大于该分类集合下所有产品总数量，则size=product.size()即产品数，
     * 否则size=i+8(0+8第一次循环,第二次循环8+8以此类推)   size = size > products.size() ? products.size() : size;
     * sublist代表把prodcts里的第i个和第 size-1 个之间产品装入productsOfEachRow集合中。（第一次0至7，第二次8至15依此类推）
     * 把productsOfEachRow封装好的产品集合 ，添加进先前定义好的 productsByRow集合中
     * （此时productsByRow是为每8个为一个小产品集合的大集合），这样productsByRow里面就是按行隔离了
     * 把按行隔离的 productsByRow集合 传回 该 Category 对象中 ---> c.setProductsByRow(productsByRow);
     */
    @Override
    public void fillByRow(List<Category> cs) {
        int productNumberEachRow = 8;
        for (Category c : cs) {
            List<Product> products = c.getProducts();
            List<List<Product>> productsByRow = new ArrayList<>();
            for (int i = 0; i < products.size(); i += productNumberEachRow) {
                int size = i + productNumberEachRow;
                size = size > products.size() ? products.size() : size;
                List<Product> productsOfEachRow = products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            c.setProductsByRow(productsByRow);
        }
    }

    @Override
    public void setSaleAndReviewNumber(Product p) {
        int saleCount = orderItemService.getSaleCount(p.getId());
        p.setSaleCount(saleCount);

        int reviewCount = reviewService.getCount(p.getId());
        p.setReviewCount(reviewCount);
    }

    @Override
    public void setSaleAndReviewNumber(List<Product> ps) {
        for (Product p : ps) {
            setSaleAndReviewNumber(p);
        }
    }

    @Override
    public List<Product> search(String keyword) {
        ProductExample example = new ProductExample();
        example.createCriteria().andNameLike("%" + keyword + "%");
        example.setOrderByClause("id desc");
        List result = productMapper.selectByExample(example);
        setFirstProductImage(result);
        setCategory(result);
        return result;
    }

    public void setFirstProductImage(List<Product> ps) {
        for (Product p : ps) {
            setFirstProductImage(p);
        }
    }
}
