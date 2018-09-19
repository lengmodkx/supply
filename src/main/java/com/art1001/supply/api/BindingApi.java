package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.PushType;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.ui.Model;
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

    /**
     * 任务,日程，文件，分享与任务,日程，文件，分享的绑定关系
     * @param publicId 任务,日程，文件，分享的id
     * @param bindId 被绑定的任务,日程，文件，分享的id
     * @param publicType 绑定类型 任务,日程，文件，分享 枚举类型
     * @return
     */
    @PostMapping
    public JSONObject saveBinding(@RequestParam String publicId, @RequestParam String bindId[], @RequestParam String publicType, String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<String> bindList = new ArrayList<String>(Arrays.asList(bindId));
            bindingService.saveBindings(publicId,bindList,publicType);
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("系统异常,绑定失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * @param publicId 当前绑定信息的id
     * @param bindingId 选择要移除的关联信息的id
     * @return
     */
    @DeleteMapping
    public JSONObject deleteBinding(@RequestParam(value = "publicId") String publicId, @RequestParam(value = "bindingId") String bindingId){
        JSONObject jsonObject = new JSONObject();
        try {
            bindingService.deleteBindingById(publicId,bindingId);
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
                              @RequestParam(value = "projectId") String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            //获取当前用户参与的所有项目
            List<Project> projectList = projectService.listProjectByUid(ShiroAuthenticationManager.getUserId());
            if(!projectList.isEmpty()){
                List<Relation> allGroupInfoByProjectId = relationService.findAllGroupInfoByProjectId(projectList.get(0).getProjectId());
                jsonObject.put("groups",allGroupInfoByProjectId);
            }
            jsonObject.put("projectList",projectList);
            jsonObject.put("id",taskId);
            jsonObject.put("id",scheduleId);
            jsonObject.put("id",shareId);
            jsonObject.put("id",fileId);
            jsonObject.put("projectId",projectId);

        } catch (Exception e){
            log.error("系统异常:",e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

}
