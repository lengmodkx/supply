package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.organization.Organization;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.Stringer;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
    private TaskService taskService;

    @Resource
    private FileService fileService;

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private ShareService shareService;


    @Resource
    private OrganizationService organizationService;

    /**
     * 任务,日程，文件，分享与任务,日程，文件，分享的绑定关系
     * @param publicId 任务,日程，文件，分享的id
     * @param bindId 被绑定的任务,日程，文件，分享的id
     * @param publicType 绑定类型 任务,日程，文件，分享 枚举类型
     * @return
     */
    @Push(value = PushType.A28,type = 3)
    @PostMapping
    public JSONObject saveBinding(@RequestParam String publicId,
                                  @RequestParam String bindId,
                                  @RequestParam String fromType,
                                  @RequestParam String publicType){
        JSONObject jsonObject = new JSONObject();
        try {
            if(Stringer.isNullOrEmpty(bindId)){
                jsonObject.put("result",0);
                jsonObject.put("msg", "绑定信息不能为空");
            }else{
                bindingService.saveBindBatch(publicId, bindId, publicType);
                jsonObject.put("data", new JSONObject().fluentPut("fromType", fromType).fluentPut("publicId",publicId).fluentPut("publicType", publicType));
                jsonObject.put("result",1);
                jsonObject.put("msgId", bindingService.getProjectId(publicId));
                jsonObject.put("id", publicId);
                jsonObject.put("publicType", publicType);
            }
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
    @Push(value = PushType.A29,type = 3)
    @DeleteMapping
    public JSONObject deleteBinding(@RequestParam(value = "bindId") String bindId,@RequestParam String projectId,@RequestParam String fromType,@RequestParam String publicId){
        JSONObject jsonObject = new JSONObject();
        try {
            QueryWrapper<Binding> eq = new QueryWrapper<Binding>().eq("public_id", publicId).eq("bind_id", bindId);
            Binding byId = bindingService.getOne(eq);
            bindingService.remove(eq);
            jsonObject.put("data", new JSONObject().fluentPut("fromType", fromType).fluentPut("publicType", byId.getPublicType()).fluentPut("publicId", publicId));
            jsonObject.put("msgId", projectId);
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
            List<Project> projectList = projectService.findProjectByUserId(userId);

            List<Organization> projectOrg = organizationService.findJoinOrgProject(userId);

            List<Relation> relationList = relationService.list(new QueryWrapper<Relation>().eq("parent_id","0").eq("projectId",projectId));

            jsonObject.put("projectList",projectList);
            jsonObject.put("projectOrg",projectOrg);
            jsonObject.put("relationList",relationList);
            jsonObject.put("projectId",projectId);
        } catch (Exception e){
            log.error("系统异常:",e);
            throw new SystemException(e);
        }
        return jsonObject;
    }
}
