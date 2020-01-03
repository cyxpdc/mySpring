package com.pdc.spring.bean;

/**
 * 返回数据对象
 * @author pdc
 */
public class Data {

    /**
     * 模型数据
     */
    private Object model;

    public Data(Object model) {
        this.model = model;
    }

    public Object getModel() {
        return model;
    }
}
