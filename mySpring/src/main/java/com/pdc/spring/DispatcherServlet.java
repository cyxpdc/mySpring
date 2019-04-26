package com.pdc.spring;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pdc.spring.bean.Data;
import com.pdc.spring.bean.Handler;
import com.pdc.spring.bean.Param;
import com.pdc.spring.bean.View;
import com.pdc.spring.util.JsonUtil;
import com.pdc.spring.util.ReflectionUtil;
import com.pdc.spring.util.StringUtil;
import com.pdc.spring.helper.BeanHelper;
import com.pdc.spring.helper.ConfigHelper;
import com.pdc.spring.helper.ControllerHelper;
import com.pdc.spring.helper.RequestHelper;
import com.pdc.spring.helper.ServletHelper;
import com.pdc.spring.helper.UploadHelper;

/**
 * 请求转发器
 *
 * @author pdc
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)//接收所有请求
public class DispatcherServlet extends HttpServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        //初始化相关Helper类
        HelperLoader.init();
        //获取ServletContext对象，用于注册Servlet
        ServletContext servletContext = servletConfig.getServletContext();
        //获取ServletContext对象，用于注册Servlet注册Servlet(JSP、静态资源)
        registerServlet(servletContext);
        //初始化文件上传助手
        UploadHelper.init(servletContext);
    }

    private void registerServlet(ServletContext servletContext) {
        //注册处理JSP的servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping("/index.jsp");
        jspServlet.addMapping(ConfigHelper.getAppJspPath() + "*");
        //注册处理静态资源的默认Servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping("/favicon.ico");
        defaultServlet.addMapping(ConfigHelper.getAppAssetPath() + "*");
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletHelper.init(request, response);//初始化ServletHelper
        try {
            //获取请求方法和请求路径
            String requestMethod = request.getMethod().toLowerCase();
            String requestPath = request.getPathInfo();
            //获取Action处理器
            Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
            if (handler != null) {
                //获取Controller类(或代理类)及其Bean实例
                Class<?> controllerClass = handler.getControllerClass();
                Object controllerBean = BeanHelper.getBean(controllerClass);
                //创建请求参数
                //根据请求对象是否为上传文件来创建不同的param对象
                Param param;
                if (UploadHelper.isMultipart(request)) {
                    param = UploadHelper.createParam(request);
                } else {
                    param = RequestHelper.createParam(request);
                }
                //调用真正的Action方法(或代理类),使用Object封装返回结果
                //返回类型为View或Data
                Object result;
                Method actionMethod = handler.getActionMethod();
                if (param.isEmpty()) {//优化1：判断参数是否为空
                    result = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
                } else {
                    result = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
                }
                //返回JSP页面或返回数据对象
                if (result instanceof View) {
                    handleViewResult((View) result, request, response);//跳转到指定JSP页面
                } else if (result instanceof Data) {
                    handleDataResult((Data) result, response);//传Json数据给前端
                }
            }
        } finally {
            ServletHelper.destroy();//销毁ServletHelper
        }
    }

    private void handleViewResult(View view, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String path = view.getPath();
        if (StringUtil.isNotEmpty(path)) {
            if (path.startsWith("/")) {
                response.sendRedirect(request.getContextPath() + path);//重定向
            } else {
                Map<String, Object> model = view.getModel();
                for (Map.Entry<String, Object> entry : model.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }
                //有数据，只能请求转发
                request.getRequestDispatcher(ConfigHelper.getAppJspPath() + path).forward(request, response);
            }
        }
    }

    private void handleDataResult(Data data, HttpServletResponse response) throws IOException {
        Object model = data.getModel();
        if (model != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter writer = response.getWriter();
            String json = JsonUtil.toJson(model);
            writer.write(json);
            writer.flush();
            writer.close();
        }
    }
}
