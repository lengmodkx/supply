package com.art1001.supply.entity.task.vo;

import com.art1001.supply.entity.task.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName TaskDynamicVO
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/4/26 14:18
 * @Discription 最近动态任务显示VO
 */
@Data
public class TaskDynamicVO {

    /**
     * 任务名称
     */
    private List<Task> tasks;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 项目名称
     */
    private String projectName;




}
