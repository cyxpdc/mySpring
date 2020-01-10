package com.pdc.test3.factoryBean;

import com.pdc.spring.bean.FactoryBean;

/**
 * @author PDC
 */
//FactoryBean接口的实现类
public class FactoryBeanTest implements FactoryBean {

    @Override
    public Object getObject() throws Exception {
        //这个Bean是我们自己new的，这里我们就可以控制Bean的创建过程了
        return new FactoryBeanService();
    }

    @Override
    public Class<?> getObjectType() {
        return FactoryBeanService.class;
    }
}
