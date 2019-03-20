package com.art1001.supply.common;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @Author: heshaohua
 * @Description: 封装返回信息实体类
 */
@Data
public class ResponseEntity {
    protected boolean success; // 是否成功
    protected String errmsg; // 错误内容
    protected Integer errcode; // 错误代码
    protected Object data; // 数据对象

    /**
     * @param success
     * @param data
     * @param errcode
     * @param errmsg
     * @author by chippy
     * @desc 构造函数.
     */
    public ResponseEntity(boolean success, Object data, Integer errcode, String errmsg) {
        this.success = success;
        this.data = data;
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public ResponseEntity(boolean success){
        this.success = success;
    }

    /**
     * @param errcode
     * @param errmsg
     * @author by chippy
     * @desc 构造函数.
     */
    public ResponseEntity(Integer errcode, String errmsg) {
        this.success = false;
        this.data = null;
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    /**
     * @param data
     * @author by chippy
     * @desc 构造函数.
     */
    public ResponseEntity(Object data) {
        this.success = true;
        this.data = data;
    }

    /**
     * @return
     * @author by chippy
     * @desc 将ResponseEntity转换成json格式对象.
     */
    public Object toJson() {
        return JSONObject.toJSONString(this);
    }

    public boolean isSuccess() {
        return success;
    }


}
