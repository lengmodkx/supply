package com.art1001.supply.entity.relation;

import com.art1001.supply.entity.task.Task;
import lombok.Data;

import java.util.List;

/**
 * @author heshaohua
 * @Title: GroupVO
 * @Description: TODO 用于分组下拉菜单的数据显示
 * @date 2018/9/5 11:12
 **/
@Data
public class GroupVO {

    /**
     * 分组id
     */
    private String groupId;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 分组内任务总数
     */
    private int taskTotal;

    /**
     * 分组内 已完成任务的数量
     */
    private int completeCount;

    /**
     * 分组内未完成任务的数量
     */
    private int notCompleteCount;

    /**
     * 普通百分比
     */
    private Long ordinary;

    /**
     * 紧急百分比
     */
    private Long urgent;

    /**
     * 非常紧急百分比
     */
    private Long veryUrgent;

    /**
     * 分组下的任务列表
     */
    private List<Task> tasks;



}
