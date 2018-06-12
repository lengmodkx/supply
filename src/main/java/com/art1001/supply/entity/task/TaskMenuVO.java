package com.art1001.supply.entity.task;

import lombok.Data;

/**
 * @author heshaohua
 * @Title: TaskMenuVO
 * @Description 传输菜单实体
 * @date 2018/6/12 9:04
 **/
@Data
public class TaskMenuVO {

    /**
     * 当前任务所在项目的项目id
     */
    private String projectId;

    /**
     * 当前任务所在项目的项目名称
     */
    private String projectName;

    /**
     * 任务组id
     */
    private String taskGroupId;

    /**
     * 任务组名称
     */
    private String taskGroupName;

    /**
     * 任务菜单id
     */
    private String taskMenuId;

    /**
     * 任务菜单名称
     */
    private String taskMenuName;

}
