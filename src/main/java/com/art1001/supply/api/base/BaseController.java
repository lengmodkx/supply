package com.art1001.supply.api.base;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.ResponseEntity;
import com.art1001.supply.util.Stringer;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * @Author: heshaohua
 * @Description: 基类Controller 封装基本操作
 */
@Controller
public class BaseController {

    @Resource
    protected HttpServletRequest request;

    @Resource
    protected HttpServletResponse response;

    /**
     * @param errorCode    错误代码
     * @param errorMessage 错误信息
     * @return
     * @throws Exception
     * @author by chippy
     * @desc 构造错误的返回信息（带errorCode）.
     */
    protected Object error(Integer errorCode, String errorMessage) {
        return new ResponseEntity(errorCode, errorMessage).toJson();
    }

    /**
     * @param errorMessage 错误信息
     * @return
     * @author by chippy
     * @desc 构造错误的返回信息（不带errorCode）.
     */
    protected JSONObject error(String errorMessage) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result",false);
        jsonObject.put("msg",errorMessage);
        return jsonObject;
    }

    /**
     * @param data - 业务数据json
     * @return 返回值信息
     * @author by chippy
     */
    protected JSONObject success(String msgId, Object data, String id, String name,String projectId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgId",msgId);
        jsonObject.put("data",data);
        jsonObject.put("result",true);
        jsonObject.put("id",id);
        jsonObject.put("name",name);
        jsonObject.put("projectId",projectId);
        return jsonObject;
    }

    /**
     * @return 返回值信息
     * @author by chippy
     */
    protected JSONObject success(String msgId, String id, String name) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgId",msgId);
        jsonObject.put("result",true);
        jsonObject.put("id",id);
        jsonObject.put("name",name);
        return jsonObject;
    }

    /**
     * @param data - 业务数据json
     * @return 返回值信息
     * @author by chippy
     */
    protected JSONObject success(Object data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",data);
        jsonObject.put("result",1);
        return jsonObject;
    }

    /**
     * @param obj
     * @return
     * @author chippy
     * @desc 判断某对象是否为空..
     */
    protected boolean isNullOrEmpty(Object obj) {
        return Stringer.isNullOrEmpty(obj);
    }

    /**
     * @param request
     * @return
     * @author chippy
     * @desc 获取webapp完整URL. e.g http://www.abc.com/app/a/b/c?a=b&c=d...
     */
    protected final String getRequestURL(HttpServletRequest request) {

        if (request == null) {
            return "";
        }

        String url = "";
        url = "http://" + request.getServerName() // 服务器地址
                // + ":"
                // + request.getServerPort() //端口号
                + request.getContextPath() // 项目名称
                + request.getServletPath(); // 请求页面或其他地址
        try {
            // 参数
            Enumeration<?> names = request.getParameterNames();

            int i = 0;
            String queryString = request.getQueryString();
            if (null != queryString && !"".equals(queryString) && (!queryString.equals("null"))) {
                url = url + "?" + request.getQueryString();
                i++;
            }

            if (names != null) {
                while (names.hasMoreElements()) {
                    if (i == 0) {
                        url = url + "?";
                    }

                    String name = (String) names.nextElement();
                    if (url.indexOf(name) < 0) {
                        url = url + "&";

                        String value = request.getParameter(name);
                        if (value == null) {
                            value = "";
                        }
                        url = url + name + "=" + value;
                        i++;
                    }
                    // java.net.URLEncoder.encode(url, "ISO-8859");
                }
            }

            // String enUrl = java.net.URLEncoder.encode(url, "utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return url;
    }

}
