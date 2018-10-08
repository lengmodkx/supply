package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 任务，文件，日程，分享收藏
 * @author 汪亚锋
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */
@Slf4j
@RestController
    @RequestMapping("collections")
public class CollectionApi {

    @Resource
    private PublicCollectService collectService;

    @Resource
    private TaskService taskService;

    @Resource
    private FileService fileService;

    @Resource
    ScheduleService scheduleService;

    @Resource
    ShareService shareService;

    /**
     * 收藏
     * @param projectId 项目id
     * @param publicId  任务/文件/日程/分享的id
     * @param collectType 收藏类型
     * @return
     */
    @PostMapping
    public JSONObject addCollection(@RequestParam String projectId,@RequestParam String publicId,@RequestParam String collectType){
        JSONObject object = new JSONObject();
        try{
            PublicCollect collect = new PublicCollect();
            collect.setProjectId(projectId);
            collect.setPublicId(publicId);
            collect.setCollectType(collectType);
            collect.setCreateTime(System.currentTimeMillis());
            collect.setUpdateTime(System.currentTimeMillis());
            collect.setMemberId(ShiroAuthenticationManager.getUserId());
            if(Constants.TASK.equals(collectType)){
                collect.setCollectContent(JSON.toJSONString(taskService.findTaskApiBean(publicId)));
            }
            if(Constants.SCHEDULE.equals(collectType)){
                collect.setCollectContent(JSON.toJSONString(scheduleService.findScheduleApiBean(publicId)));
            }
            if(Constants.SHARE.equals(collectType)){
                collect.setCollectContent(JSON.toJSONString(shareService.findShareApiBean(publicId)));
            }
            if(Constants.FILE.equals(collectType)){
                collect.setCollectContent(JSON.toJSONString(fileService.findFileApiBean(publicId)));
            }
            collectService.save(collect);
            object.put("result",1);
            object.put("msg","收藏成功");
        }catch(Exception e){
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 取消收藏
     * @param publicId 任务/文件/日程/分享的id
     * @return
     */
    @DeleteMapping("/{publicId}")
    public JSONObject deleteCollection(@PathVariable String publicId){
        JSONObject object = new JSONObject();
        try{
            String userId = ShiroAuthenticationManager.getUserId();
            collectService.deletePublicCollectById(userId,publicId);
            object.put("result",1);
            object.put("msg","取消收藏成功");
        }catch(Exception e){
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 全部收藏
     * @param collectType 收藏类型
     * @param size 每页数量
     * @param currPage 当前页
     * @return
     */
    @GetMapping
    public JSONObject collections(@RequestParam("currPage") long currPage, @RequestParam("size") long size, @RequestParam(required = false) String collectType){
        JSONObject object = new JSONObject();
        try{
            IPage<PublicCollect> page = collectService.page(new Page<PublicCollect>().setSize(size).setCurrent(currPage), new QueryWrapper<PublicCollect>().eq("member_id", ShiroAuthenticationManager.getUserId()).eq("collect_type", collectType));
            object.put("data",page);
            object.put("result",1);
        }catch(Exception e){
            log.error("系统异常,收藏数据获取失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

}
