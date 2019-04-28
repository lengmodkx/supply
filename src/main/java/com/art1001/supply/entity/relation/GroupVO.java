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
     * 完成任务数的百分比数值
     */
    private String completePercentage;

    /**
     * 未完成的任务数的百分比数值
     */
    private String noCompletePercentage;

    /**
     * 分组内未完成任务的数量
     */
    private Integer notCompleteCount;

    /**
     * 已经逾期的任务数量
     */
    private Integer beOverdue;

    /**
     * 已经逾期的任务百分比数值
     */
    private String beOverduePercentage;

    /**
     * 分组下的任务列表
     */
    private List<Task> tasks;



}
