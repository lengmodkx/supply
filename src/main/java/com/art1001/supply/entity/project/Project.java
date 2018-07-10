package com.art1001.supply.entity.project;

import com.art1001.supply.entity.base.BaseEntity;
import com.art1001.supply.entity.task.Task;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * projectEntity
 */
@Data
@ToString
public class Project extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * project_id
     */
    private String projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 用户id，项目创建人
     */
    private String memberId;

    /**
     * 项目开始时间
     */
    private Long startTime;

    /**
     * 项目结束时间 0：没有 1：
     */
    private Long endTime;

    /**
     * 项目封面
     */
    private String projectCover;

    /**
     * 描述
     */
    private String projectDes;

    /**
     * 是否删除  0：未删除  1：已删除
     */
    private Integer projectDel;

    /**
     * 状态
     */
    private Integer projectStatus;
    /**
     * 项目是否公开，0私有，1公开
     */
    private Integer isPublic;

    /**
     * 项目是否开启推送提醒
     */
    private Integer projectRemind;

    private List<ProjectFunc> funcList;

    /**
     * 项目下的所有任务
     */
    private List<Task> taskList;
    /**
     * 是否收藏，0不是，1是
     */
    private Integer isCollect;
}