/**
 * @Title: CategoryServiceImpl
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/7 16:27
 */
package com.how2java.tmall.service.impl;

import com.how2java.tmall.mapper.CategoryMapper;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.CategoryExample;
import com.how2java.tmall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 注解@Service声明当前类是一个Service类
 * 通过自动装配@Autowired引入CategoryMapper ，在list方法中调用CategoryMapper 的list方法，
 * 并根据CategoryMapper.xml中的SQL语句查询数据
 */

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;


    @Override
    public List<Category> list() {

        CategoryExample example = new CategoryExample();
        example.setOrderByClause("id desc");
        return categoryMapper.selectByExample(example);
    }

    @Override
    public void add(Category category) {

        categoryMapper.insert(category);
    }

    @Override
    public void delete(int id) {

        categoryMapper.deleteByPrimaryKey(id);

    }

    @Override
    public Category get(int id) {

        return categoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(Category category) {

        categoryMapper.updateByPrimaryKeySelective(category);
    }

}
