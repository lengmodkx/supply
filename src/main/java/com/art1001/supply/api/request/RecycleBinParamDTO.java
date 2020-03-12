package com.art1001.supply.api.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author heshaohua
 * 移入回收站/恢复 --- 参数封装类
 **/
@Data
public class RecycleBinParamDTO {

    /**
     * 要恢复的信息id
     */
    @NotBlank(message = "publicId不能为空!")
    private String publicId;

    /**
     * 要恢复的信息类型
     */
    @NotBlank(message = "publicType不能为空!")
    private String publicType;

    /**
     * 要回复到的项目id (恢复任务时使用)
     */
    private String projectId;

    /**
     * 要回复到的分组id (恢复任务时使用)
     */
    private String groupId;

    /**
     * 要回复到的列表id (恢复任务时使用)
     */
    private String menuId;

    /**
     * 动作 （移入--move  恢复--recovery）
     */
    private String action;
}
