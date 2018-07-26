package com.art1001.supply.entity.file;

import lombok.Data;

import java.util.Map;

/**
 * @author heshaohua
 * @Title: TaskPushType
 * @Description: TODO
 * @date 2018/7/8 15:09
 **/
@Data
public class FilePushType {

    private String type;
    private Map<String,Object> object;
    public FilePushType(String type){
        this.type = type;
    }
}

