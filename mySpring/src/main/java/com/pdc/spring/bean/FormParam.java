package com.pdc.spring.bean;

/**
 * 封装表单参数
 * @author pdc
 */
public class FormParam {

    private String fieldName;//属性名
    private Object fieldValue;//属性值

    public FormParam(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
