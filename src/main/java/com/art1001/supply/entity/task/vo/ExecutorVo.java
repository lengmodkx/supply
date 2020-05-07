package com.art1001.supply.entity.task.vo;

import lombok.Data;

/**
 * @ClassName ExecutorVo
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/5/6 10:57
 * @Discription 任务执行者VO
 */
@Data
public class ExecutorVo {
    /**
     * 任务执行者
     */
    private String executor;
    /**
     * 任务执行者名称
     */
    private String executorName;
}
