package com.art1001.supply.service.publics.impl;

import com.art1001.supply.entity.fabulous.Fabulous;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.service.fabulous.FabulousService;
import com.art1001.supply.service.publics.PublicService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Description
 * @Date:2019/4/3 18:00
 * @Author heshaohua
 **/
@Service
public class PublicServiceImpl implements PublicService {

    @Resource
    private FabulousService fabulousService;

    @Resource
    private TaskService taskService;

    /**
     * 点赞
     * @param publicId 信息id
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int fabulous(String publicId) {
        Fabulous fabulous = new Fabulous();
        fabulous.setMemberId(ShiroAuthenticationManager.getUserId());
        fabulous.setPublicId(publicId);
        return fabulousService.save(fabulous) ? 1:0;
    }
}
