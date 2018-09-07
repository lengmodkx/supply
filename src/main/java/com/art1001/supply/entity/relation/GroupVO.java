package com.art1001.supply.entity.relation;

import lombok.Data;

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
     * 分组内任务总数
     */
    private int taskTotal;

    /**
     * 分组内 已完成任务的数量
     */
    private int complete;

    /**
     * 分组内未完成任务的数量
     */
    private int hangInTheAir;

    /**
     * 分组内近期的任务数量
     */
    private int recent;

    /**
     * 分组内已逾期的任务数量
     */
    private int overdue;

    /**
     * 分组的创建者
     */
    private String creator;

    /**
     * 分组状态
     */
    private int isDel;
}
