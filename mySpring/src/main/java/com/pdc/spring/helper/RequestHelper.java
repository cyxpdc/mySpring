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
 * @author pdc
 */
public final class RequestHelper {

    /**
     * 创建请求对象
     * 可以不需要创建两个parse方法内部的List<FormParam>：改造方法，将一个formParamList传进去即可
     */
    public static Param createParam(HttpServletRequest request) throws IOException {
        List<FormParam> formParamList = new ArrayList<>();
        //application/x- www-form-urlencoded是Post请求默认的请求体内容类型，也是form表单默认的类型。
        // Servlet API规范中对该类型的请求内容提供了request.getParameter()方法来获取请求参数值。
        // 但当请求内容不是该类型时，需要调用request.getInputStream()或request.getReader()方法来获取请求内容值。
        formParamList.addAll(parseParameterNames(request));//解析参数
        formParamList.addAll(parseInputStream(request));//解析输入流
        return new Param(formParamList);
    }

    /**
     * 解析请求体中参数
     * @param request
     * @return
     */
    private static List<FormParam> parseParameterNames(HttpServletRequest request) {
        List<FormParam> formParamList = new ArrayList<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String fieldName = paramNames.nextElement();
            String[] fieldValues = request.getParameterValues(fieldName);
            if (ArrayUtil.isNotEmpty(fieldValues)) {
                String fieldValue;
                if (fieldValues.length == 1) {//小优化，可以减少StringBuilder对象的创建
                    fieldValue = fieldValues[0];
                } else {
                    StringBuilder sb = new StringBuilder();
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

    /**
     * 解析输入流(请求体)，先通过&拆为kv对，再用=拆开kv对
     * @param request
     * @return
     * @throws IOException
     */
    private static List<FormParam> parseInputStream(HttpServletRequest request) throws IOException {
        List<FormParam> formParamList = new ArrayList<>();
        //String形式的请求体
        String body = CodecUtil.decodeUrl(StreamUtil.getStringInfo(request.getInputStream()));
        if (StringUtil.isNotEmpty(body)) {
            String[] kvs = StringUtil.splitString(body, "&");//分隔所有键值对
            if (ArrayUtil.isNotEmpty(kvs)) {
                for (String kv : kvs) {
                    String[] array = StringUtil.splitString(kv, "=");
                    if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                        //分隔键值，array[0]为fieldName，array[1]为fieldValue
                        formParamList.add(new FormParam(array[0], array[1]));
                    }
                }
            }
        }
        return formParamList;
    }
}
