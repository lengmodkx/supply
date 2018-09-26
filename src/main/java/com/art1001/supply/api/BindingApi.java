package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.*;

/**
 * @author heshaohua
 * @Title: BindingApi
 * @Description: TODO 关联api
 * @date 2018/9/13 17:21
 **/
@Slf4j
@RequestMapping("bindings")
@RestController
public class BindingApi {

    @Resource
    private BindingService bindingService;

    @Resource
    private ProjectService projectService;

    @Resource
    private RelationService relationService;

    @Resource
    private OrganizationService organizationService;

    /**
     * 任务,日程，文件，分享与任务,日程，文件，分享的绑定关系
     * @param publicId 任务,日程，文件，分享的id
     * @param bindId 被绑定的任务,日程，文件，分享的id
     * @param publicType 绑定类型 任务,日程，文件，分享 枚举类型
     * @return
     */
    @PostMapping
    public JSONObject saveBinding(@RequestParam String publicId,
                                  @RequestParam String bindId,
                                  @RequestParam String publicType){
        JSONObject jsonObject = new JSONObject();
        try {
            bindingService.saveBindBatch(publicId,bindId,publicType);
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("系统异常,绑定失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * @param bindId 关联关系id
     * @return
     */
    @DeleteMapping
    public JSONObject deleteBinding(@RequestParam(value = "bindId") String bindId){
        JSONObject jsonObject = new JSONObject();
        try {
            bindingService.removeById(bindId);
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("系统异常,关联关系删除失败:",e);
        }
        return jsonObject;
    }

    /**
     * @param taskId 当前哪个任务需要关联的 任务id
     * @param fileId 当前哪个文件需要关联的 文件id
     * @param shareId 当前哪个文件需要关联的 分享id
     * @param projectId 当前项目id
     * @return
     */
    @GetMapping
    public JSONObject bindingInfo(@RequestParam(value = "taskId",required = false) String taskId,
                                  @RequestParam(value = "fileId",required = false) String fileId,
                                  @RequestParam(value = "shareId",required = false) String shareId,
                                  @RequestParam(value = "scheduleId",required = false) String scheduleId,
                                  @RequestParam(value = "orgId",required = false) String orgId,
                                  @RequestParam(value = "projectId") String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            List<Project> projectCollect = projectService.findProjectByUserId(userId,1);

            List<Project> projectJoin = projectService.findProjectByUserId(userId,0);

            List<Organization> projectOrg = organizationService.findJoinOrgProject(userId);

            List<Relation> relationList = relationService.list(new QueryWrapper<Relation>().eq("parent_id","0").eq("projectId",projectId));

            jsonObject.put("projectCollect",projectCollect);
            jsonObject.put("projectOrg",projectOrg);
            jsonObject.put("projectJoin",projectJoin);
            jsonObject.put("relationList",relationList);
            jsonObject.put("projectId",projectId);
        } catch (Exception e){
            log.error("系统异常:",e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

}
