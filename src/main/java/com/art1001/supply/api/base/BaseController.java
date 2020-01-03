package com.art1001.supply.api.base;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Author: heshaohua
 * @Description: 基类Controller 封装基本操作
 */
@Slf4j
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
        log.error(errorMessage);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result",0);
        jsonObject.put("msg",errorMessage);
        return jsonObject;
    }

    /**
     * 参数为空处理
     * @param paramsName 参数名称
     * @return 结果
     */
    protected JSONObject paramsIsNullHandle(String... paramsName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result",0);
        jsonObject.put("msg",Arrays.toString(paramsName) + "为空");
        String logMsg = "参数 " + Arrays.toString(paramsName) + " 为空!";
        log.error(logMsg);
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
        jsonObject.put("result",1);
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
        jsonObject.put("result",1);
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
     * @return 返回值信息
     * @author by chippy
     */
    protected JSONObject success() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result",1);
        return jsonObject;
    }
}
