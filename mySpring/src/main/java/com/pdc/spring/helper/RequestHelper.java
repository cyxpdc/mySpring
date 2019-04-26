package com.pdc.spring.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.pdc.spring.bean.FormParam;
import com.pdc.spring.bean.Param;
import com.pdc.spring.util.ArrayUtil;
import com.pdc.spring.util.CodecUtil;
import com.pdc.spring.util.StreamUtil;
import com.pdc.spring.util.StringUtil;

/**
 * 请求助手类,解析请求
 * 只需要添加formParamList
 *
 * @author pdc
 */
public final class RequestHelper {

    /**
     * 创建请求对象
     */
    public static Param createParam(HttpServletRequest request) throws IOException {
        List<FormParam> formParamList = new ArrayList<FormParam>();
        formParamList.addAll(parseParameterNames(request));//解析参数
        formParamList.addAll(parseInputStream(request));//解析输入流
        return new Param(formParamList);
    }

    private static List<FormParam> parseParameterNames(HttpServletRequest request) {
        List<FormParam> formParamList = new ArrayList<>();
        Enumeration<String> paramNames = request.getParameterNames();//getParameterNames
        while (paramNames.hasMoreElements()) {
            //hasMoreElements、nextElement、getParameterValues
            String fieldName = paramNames.nextElement();
            String[] fieldValues = request.getParameterValues(fieldName);
            if (ArrayUtil.isNotEmpty(fieldValues)) {
                Object fieldValue;
                if (fieldValues.length == 1) {
                    fieldValue = fieldValues[0];
                } else {
                    StringBuilder sb = new StringBuilder("");
                    for (int i = 0; i < fieldValues.length; i++) {
                        sb.append(fieldValues[i]);
                        if (i != fieldValues.length - 1) {
                            sb.append(StringUtil.SEPARATOR);
                        }
                    }
                    fieldValue = sb.toString();
                }
                formParamList.add(new FormParam(fieldName, fieldValue));
            }
        }
        return formParamList;
    }

    private static List<FormParam> parseInputStream(HttpServletRequest request) throws IOException {
        List<FormParam> formParamList = new ArrayList<FormParam>();
        //String形式的请求体
        String body = CodecUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        if (StringUtil.isNotEmpty(body)) {
            String[] kvs = StringUtil.splitString(body, "&");//分隔所有键值对
            if (ArrayUtil.isNotEmpty(kvs)) {
                for (String kv : kvs) {
                    String[] array = StringUtil.splitString(kv, "=");
                    if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                        //分隔键值
                        String fieldName = array[0];
                        String fieldValue = array[1];
                        formParamList.add(new FormParam(fieldName, fieldValue));
                    }
                }
            }
        }
        return formParamList;
    }
}
