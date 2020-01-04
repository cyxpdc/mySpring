package com.pdc.spring.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pdc.spring.util.CastUtil;
import com.pdc.spring.util.CollectionUtil;
import com.pdc.spring.util.StringUtil;

/**
 * 请求参数对象
 * 一个表单的参数可分为：表单参数或文件参数
 * 可通过一个form表单来上传文件
 * @author pdc
 */
public class Param {

    //private Map<String,Object> paramMap;

    private List<FormParam> formParamList;//表单参数，若非文件，可以替代之前的paramMap

    private List<FileParam> fileParamList;//文件参数

    public Param(List<FormParam> formParamList) {
        this.formParamList = formParamList;
    }

    public Param(List<FormParam> formParamList, List<FileParam> fileParamList) {
        this.formParamList = formParamList;
        this.fileParamList = fileParamList;
    }

    /**
     * 获取请求参数映射
     * 如表单字段的键值对映射，或其他地方传来的参数
     * 返回值为Map<String, Object>，这样就可以减少以前代码的改动
     * 优化：不用重复创建HashMap
     */

    private Map<String, Object> fieldMap = new HashMap<>();

    public Map<String, Object> getFieldMap() {
        //Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.clear();
        if (CollectionUtil.isNotEmpty(formParamList)) {
            for (FormParam formParam : formParamList) {
                String fieldName = formParam.getFieldName();
                Object fieldValue = formParam.getFieldValue();
                if (fieldMap.containsKey(fieldName)) {
                    fieldValue = fieldMap.get(fieldName) + StringUtil.SEPARATOR + fieldValue;//分隔
                }
                fieldMap.put(fieldName, fieldValue);
            }
        }
        return fieldMap;
    }

    /**
     * 获取上传文件映射
     * 一个字段名可对应多个文件，实现多文件上传的需求
     * 优化：不用重复创建HashMap
     */

    private Map<String, List<FileParam>> fileMap = new HashMap<>();

    public Map<String, List<FileParam>> getFileMap() {
        //Map<String, List<FileParam>> fileMap = new HashMap<>();
        fileMap.clear();
        if (CollectionUtil.isNotEmpty(fileParamList)) {
            for (FileParam fileParam : fileParamList) {
                String fieldName = fileParam.getFieldName();
                List<FileParam> fileParamList;
                if (fileMap.containsKey(fieldName)) {
                    fileParamList = fileMap.get(fieldName);
                } else {
                    fileParamList = new ArrayList<>();
                }
                fileParamList.add(fileParam);
                fileMap.put(fieldName, fileParamList);
            }
        }
        return fileMap;
    }

    /**
     * 获取所有上传文件
     */
    public List<FileParam> getFileList(String fieldName) {
        return getFileMap().get(fieldName);
    }

    /**
     * 获取唯一上传文件
     */
    public FileParam getFile(String fieldName) {
        List<FileParam> fileParamList = getFileList(fieldName);
        if (CollectionUtil.isNotEmpty(fileParamList) && fileParamList.size() == 1) {
            return fileParamList.get(0);
        }
        return null;
    }

    /**
     * 验证参数是否为空
     */
    public boolean isEmpty() {
        return CollectionUtil.isEmpty(formParamList) && CollectionUtil.isEmpty(fileParamList);
    }

    /**
     * 根据参数名获取 String 型参数值
     */
    public String getString(String name) {
        return CastUtil.castString(getFieldMap().get(name));
    }

    /**
     * 根据参数名获取 long 型参数值
     */
    public long getLong(String name) {
        return CastUtil.castLong(getFieldMap().get(name));
    }

    /**
     * 根据参数名获取 double 型参数值
     */
    /*public double getDouble(String name) {
        return CastUtil.castDouble(getFieldMap().get(name));
    }*/

    /**
     * 根据参数名获取 int 型参数值
     */
    /*public int getInt(String name) {
        return CastUtil.castInt(getFieldMap().get(name));
    }*/

    /**
     * 根据参数名获取 boolean 型参数值
     */
    /*public boolean getBoolean(String name) {
        return CastUtil.castBoolean(getFieldMap().get(name));
    }*/

    /*public long getLong(String name){
        return CastUtil.castLong(paramMap.get(name));
    }*/

    /*public Map<String,Object> getMap(){
        return paramMap;
    }*/
}
