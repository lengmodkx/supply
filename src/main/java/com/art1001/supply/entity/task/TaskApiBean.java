package com.art1001.supply.entity.task;

import lombok.Data;

/**
 * @author heshaohua
 * @Title: TaskApiBean
 * @Description: TODO
 * @date 2018/9/25 10:45
 **/
@Data
public class TaskApiBean {

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务所在项目名称
     */
    private String projectName;

    /**
     * 任务所在菜单名称
     */
    private String menuName;

    /**
     * 任务所在分组名称
     */
    private String groupName;

    /**
     * 执行者头像
     */
    private String userImage;

    /**
     * 任务备注
     */
    private String remarks;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;
}
