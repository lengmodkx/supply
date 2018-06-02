package com.art1001.supply.controller.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * web错误 全局配置
 */
@Controller
public class AppErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    private ErrorAttributes errorAttributes;

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @Autowired
    public AppErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    /**
     * web 页面错误处理
     */
    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public String errorPageHandler(HttpServletResponse response) {
        int status = response.getStatus();
        switch (status) {
            case 403:
                return "error/403";
            case 404:
                return "error/404";
            case 500:
                return "error/500";
            default:
                return "index";
        }
    }
}
