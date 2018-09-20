package com.art1001.supply.base;

import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.fabulous.FabulousService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserNewsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @Title: Base
 * @Description: TODO
 * @date 2018/8/22 13:53
 **/
@Component
public class Base {

    @Resource
    BindingService bindingService;

    @Resource
    LogService logService;

    @Resource
    TaskService taskService;

    @Resource
    FabulousService fabulousService;

    @Resource
    PublicCollectService publicCollectService;

    @Resource
    TagRelationService tagRelationService;

    @Resource
    UserNewsService userNewsService;

    /**
     * 删除 任务 文件 日程 分享时 可以公用的方法
     * 此方法删除的信息为 当前删除项的  关联信息,日志信息,评论信息,得赞信息,收藏信息,标签信息,用户消息信息
     * @param publicId 项的id
     * @param pubicType 项的类型 分别为 任务,日程,文件,分享
     */
    public void deleteItemOther(String publicId,String pubicType){
        bindingService.deleteByPublicId(publicId);
        logService.deleteByPublicId(publicId);
        fabulousService.deleteFabulousByInfoId(publicId);
        publicCollectService.deleteCollectByItemId(publicId);
        tagRelationService.deleteItemTagRelation(publicId,pubicType);
        userNewsService.deleteNewsByPublicId(publicId);
    }


}
