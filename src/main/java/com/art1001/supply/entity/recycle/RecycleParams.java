package com.art1001.supply.entity.recycle;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Description 恢复回收站中的信息---参数封装类
 * @Date:2019/6/4 17:42
 * @Author heshaohua
 **/
@Data
public class RecycleParams {

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

}
