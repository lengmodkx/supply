package com.art1001.supply.entity.notice;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

@Data
public class Notice implements Serializable {

    private int type;

    private Object object;
}
