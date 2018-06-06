package com.art1001.supply.entity.project;

import com.art1001.supply.entity.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * projectEntity
 */
@Data
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
     * project_cover
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

}