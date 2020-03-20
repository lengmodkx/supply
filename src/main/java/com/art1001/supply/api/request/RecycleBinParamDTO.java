package com.art1001.supply.api.request;

import com.art1001.supply.validation.recycle.TaskRecovery;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

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
    @NotNull(message = "项目id不能为空i", groups = TaskRecovery.class)
    private String projectId;

    /**
     * 要回复到的分组id (恢复任务时使用)
     */
    @NotNull(message = "分组id不能为空i", groups = TaskRecovery.class)
    private String groupId;

    /**
     * 要回复到的列表id (恢复任务时使用)
     */
    @NotNull(message = "列表id不能为空i", groups = TaskRecovery.class)
    private String menuId;

    /**
     * 动作 （移入--move  恢复--recovery）
     */
    @NotNull(message = "动作标识不能为空！")
    private String action;

    /**
     * 文件id集合
     */
    private List<String> fileIdList;

    public List<String> getFileIdList() {
        return Arrays.asList(publicId.split(","));
    }
}
