package com.art1001.supply.entity.task.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName TaskDynamicVO
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/4/26 14:18
 * @Discription 最近动态任务显示VO
 */
@Data
public class TaskDynamicVO {
    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 项目名称
     */
    private String projectName;

}
