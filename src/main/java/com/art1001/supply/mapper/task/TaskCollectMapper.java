package com.art1001.supply.mapper.task;

import com.art1001.supply.entity.task.TaskCollect;

import java.util.List;

/**
 * @author heshaohua
 * 任务收藏的mapper
 */
public interface TaskCollectMapper {
//    int deleteByPrimaryKey(String id);
//
//    int insert(TaskCollect record);
//
//    int insertSelective(TaskCollect record);
//
//    TaskCollect selectByPrimaryKey(String id);
//
//    int updateByPrimaryKeySelective(TaskCollect record);
//
//    int updateByPrimaryKey(TaskCollect record);

    /**
     * 任务收藏mapper层
     * 数据: 查询出某个用户收藏的所用任务
     * 功能: 返回数据给逻辑层进行处理
     * @param memberId 用户id
     * @return 返回收藏集合
     */
    List<TaskCollect> findMyCollectTask(String memberId);

}