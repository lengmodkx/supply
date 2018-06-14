package com.art1001.supply.entity.task;

import lombok.Data;

/**
 * @author heshaohua
 * @Title: Fabulous
 * @Description: 任务得赞信息
 * @date 2018/6/14 11:09
 **/
@Data
public class Fabulous {

    /**
     * 赞的id
     */
    private Long fabulousId;

    /**
     * 成员id
     */
    private String memberId;

    /**
     * 任务的id
     */
    private String taskId;
}
