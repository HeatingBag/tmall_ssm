/**
 * @Title: PropertyServiceImpl
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/10 10:23
 */
package com.how2java.tmall.service.impl;

import com.how2java.tmall.mapper.PropertyMapper;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.pojo.PropertyExample;
import com.how2java.tmall.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    @Autowired
    PropertyMapper propertyMapper;

    @Override
    public void add(Property p) {

        propertyMapper.insert(p);
    }

    @Override
    public void delete(int id) {

        propertyMapper.deleteByPrimaryKey(id);

    }

    @Override
    public void update(Property p) {

        propertyMapper.updateByPrimaryKeySelective(p);

    }

    @Override
    public Property get(int id) {

        return propertyMapper.selectByPrimaryKey(id);
    }

    @Override
    public List list(int cid) {

        PropertyExample example = new PropertyExample();
        example.createCriteria().andCidEqualTo(cid);
        example.setOrderByClause("id desc");
        return propertyMapper.selectByExample(example);
    }
}
