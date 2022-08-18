/**
 * @Title: PropertyValueServiceImpl
 * @Auther: zhang
 * @Version: 1.0
 * @create: 2022/6/10 16:42
 */
package com.how2java.tmall.service.impl;

import com.how2java.tmall.mapper.PropertyValueMapper;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.pojo.PropertyValue;
import com.how2java.tmall.pojo.PropertyValueExample;
import com.how2java.tmall.service.PropertyService;
import com.how2java.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyValueServiceImpl implements PropertyValueService {

    @Autowired
    PropertyValueMapper propertyValueMapper;

    @Autowired
    PropertyService propertyService;

    /**
     * 1  init() 这个方法的作用是初始化PropertyValue
     * 对于PropertyValue的管理，没有增加，只有修改。 需要通过初始化来进行自动地增加，以便于后面的修改。
     * 2 首先根据产品获取分类，然后获取这个分类下的所有属性集合
     * 3 然后用属性和id产品id去查询，看看这个属性和这个产品，是否已经存在属性值了。
     * 4 如果不存在，那么就创建一个属性值，并设置其属性和产品，接着插入到数据库中。
     * */

    @Override
    public void init(Product p) {

        List<Property> pts = propertyService.list(p.getCid());

        for (Property pt : pts) {
            PropertyValue pv = get(pt.getId(), p.getId());
            if (null == pv) {
                pv = new PropertyValue();
                pv.setPid(p.getId());
                pv.setPtid(pt.getId());
                propertyValueMapper.insert(pv);
            }
        }
    }

    @Override
    public void update(PropertyValue pv) {

        propertyValueMapper.updateByPrimaryKeySelective(pv);

    }

    @Override
    public PropertyValue get(int ptid, int pid) {
        PropertyValueExample example = new PropertyValueExample();
        example.createCriteria().andPtidEqualTo(ptid).andPidEqualTo(pid);
        List<PropertyValue> pvs = propertyValueMapper.selectByExample(example);
        if (pvs.isEmpty())
            return null;

        return pvs.get(0);
    }

    @Override
    public List<PropertyValue> list(int pid) {
        PropertyValueExample example = new PropertyValueExample();
        example.createCriteria().andPidEqualTo(pid);
        List<PropertyValue> result = propertyValueMapper.selectByExample(example);
        for (PropertyValue pv : result) {
            Property property = propertyService.get(pv.getPtid());
            pv.setProperty(property);
        }
        return result;
    }
}
