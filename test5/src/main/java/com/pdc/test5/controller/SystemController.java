package com.pdc.test5.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pdc.spring.annotation.Action;
import com.pdc.spring.annotation.Controller;
import com.pdc.spring.bean.Param;
import com.pdc.spring.bean.View;
import com.pdc.plugin.security.SecurityHelper;
import com.pdc.plugin.security.exception.AuthcException;

/**
 * 处理系统请求
 */
@Controller
public class SystemController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemController.class);

    /**
     * 进入首界面
     */
    @Action("get:/")
    public View index() {
        return new View("index.jsp");
    }

    /**
     * 进入登录界面
     */
    @Action("get:/login")
    public View login() {
        return new View("login.jsp");
    }

    /**
     * 提交登录表单
     */
    @Action("post:/login")
    public View loginSubmit(Param param) {
        String username = param.getString("username");
        String password = param.getString("password");
        try {
            SecurityHelper.login(username, password);
        } catch (AuthcException e) {
            LOGGER.error("login failure", e);
            return new View("/login");
        }
        return new View("/customer");
    }

    /**
     * 提交注销请求
     */
    @Action("get:/logout")
    public View logout() {
        SecurityHelper.logout();
        return new View("/");
    }
}
