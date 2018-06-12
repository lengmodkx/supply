package com.art1001.supply.entity.task;

import lombok.Data;

/**
 * @author heshaohua
 * @Title: TaskLogVO
 * @Description: 日志实体类 + 更新操作返回的影响行数 (结果)
 * @date 2018/6/12 10:32
 **/
@Data
public class TaskLogVO {

    /**
     * id
     */
    private String id;

    /**
     * 用户名
     */
    private String memberName;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 返回执行更新操作的结果
     */
    private int result;
}
