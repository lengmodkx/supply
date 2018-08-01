package com.art1001.supply.entity.task;

import lombok.Data;

import java.util.Map;

/**
 * @author heshaohua
 * @Title: TaskPushType
 * @Description: TODO
 * @date 2018/7/8 15:09
 **/
@Data
public class TaskPushType {

    private String type;
    private Map<String,Object> object;

    public TaskPushType(String type){
        this.type = type;
    }

    public TaskPushType(){

    }
}

