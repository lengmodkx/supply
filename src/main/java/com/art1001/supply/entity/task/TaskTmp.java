package com.art1001.supply.entity.task;

import lombok.Data;

import java.util.List;

@Data
public class TaskTmp {
    //当前用户的默认组
    private String groupId;

    //查询关键字
    private String keyword;

    //执行者
    private String executor;
    //参与者
    private List<String> taskUids;
    //创建人
    private String memberId;
    //开始时间
    private Long startTime;
    //结束时间
    private Long endTime;
    //是否完成
    private Boolean taskStatus;
    //优先级
    private List<String> priority;
}
