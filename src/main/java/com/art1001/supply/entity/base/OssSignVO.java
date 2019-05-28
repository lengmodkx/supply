package com.art1001.supply.entity.base;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.xdevapi.JsonArray;
import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Date:2019/5/27 10:46
 * @Author heshaohua
 **/
@Data
public class OssSignVO {
    private String key;
    private String policy;
    private String accessid;
    private Integer successActionStatus;
    private String signature;

    private String policyText;

    public String getPolicyText() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("expiration", "2020-01-01T12:00:00.000Z");
        jsonObject.put("conditions", new JSONArray().fluentAdd(new JSONArray().fluentAdd("content-length-range").fluentAdd(0).fluentAdd(1048576000)));
        return JSONObject.toJSONString(new JSONObject().put("policyText", jsonObject));
    }
}
