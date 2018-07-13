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

}
