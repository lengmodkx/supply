package com.art1001.supply.api.base;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("index")
@RestController
public class IndexApi {
    @RequestMapping("test")
    public JSONObject testIndex(){
        JSONObject object = new JSONObject();
        object.put("result",1);
        object.put("msg","success");
        return object;
    }
}
