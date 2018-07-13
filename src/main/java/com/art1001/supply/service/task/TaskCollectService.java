package com.art1001.supply.service.task;
import com.art1001.supply.entity.task.TaskCollect;

import java.util.List;

/**
 * @author heshaohua
 * @Title:
 * @Description: 任务的收藏接口
 * @date 2018/7/13 15:01
 */
public interface TaskCollectService {


    /**
     * 数据: 根据用户id查询该用户收藏的所有任务
     * 功能: 实现查询我的收藏的任务 功能
     * @param memberId 用户id
     * @return 藏实体集合
     */
    List<TaskCollect> findMyCollectTask(String memberId);
}
