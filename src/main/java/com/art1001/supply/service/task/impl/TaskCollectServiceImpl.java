package com.art1001.supply.service.task.impl;

import com.art1001.supply.entity.task.TaskCollect;
import com.art1001.supply.mapper.task.TaskCollectMapper;
import com.art1001.supply.service.task.TaskCollectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heshaohua
 * @Title: TaskCollectImpl
 * @Description: TODO
 * @date 2018/7/13 15:03
 **/
@Service
public class TaskCollectServiceImpl implements TaskCollectService {

    /**
     * 任务收藏 Mapper层
     */
    @Resource
    private TaskCollectMapper taskCollectMapper;

    /**
     * 重写接口方法
     * 数据: 查询该用户收藏的所有任务
     * 功能: 查看我收藏的任务
     * @param memberId 用户id
     * @return 返回收藏实体类集合信息
     */
    @Override
    public List<TaskCollect> findMyCollectTask(String memberId) {
        return taskCollectMapper.findMyCollectTask(memberId);
    }
}
