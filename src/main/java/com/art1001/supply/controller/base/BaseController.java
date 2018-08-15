package com.art1001.supply.controller.base;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * controller基类,目前功能比较简单
 * @author wangyafeng
 * 2016年7月12日 下午3:02:14
 *
 */
@Slf4j
public abstract class BaseController {
    @Resource
    private ProjectMemberService projectMemberService;

    @Resource
    private ProjectService projectService;

    @Resource
    private UserService userService;

    @Resource
    private LogService logService;

    @Resource
    private TagService tagService;

    //查询全部项目成员
    @PostMapping("/findAllProjectMember")
    @ResponseBody
    public JSONObject findAllProjectMember(@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("data",projectMemberService.findByProjectId(projectId));
            jsonObject.put("result",1);
            jsonObject.put("msg","获取成功");
        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    @GetMapping("/removePeople.html")
    public String removePeople(@RequestParam String nodeName,Model model){
        try {
            model.addAttribute("nodeName",nodeName);
        }catch (Exception e){
            throw new SystemException(e);
        }

        return "tk-group-remove";
    }

    /**
     * 查找用户
     * @param keyword
     * @return
     */
    @PostMapping("/searchMember")
    @ResponseBody
    public JSONObject searchMember(@RequestParam String keyword,@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try{
            List<UserEntity> userEntity = userService.findByKey(keyword);
            if(userEntity.size()==0){
                jsonObject.put("msg","无数据");
                jsonObject.put("result",0);
            }else{
                jsonObject.put("data",userEntity);
                jsonObject.put("msg","获取成功");
                jsonObject.put("result",1);
            }

        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 给项目添加成员
     * @param projectId
     * @param memberId
     * @return
     */
    @PostMapping("/addProjectMember")
    @ResponseBody
    public JSONObject addProjectMember(@RequestParam String projectId,@RequestParam String memberId){
        JSONObject jsonObject = new JSONObject();
        try{

            if(StringUtils.isEmpty(memberId)){
                jsonObject.put("result",0);
                jsonObject.put("msg","请选择成员");
            }else{

                UserEntity userEntity = userService.findById(memberId);
                ProjectMember projectMember = new ProjectMember();
                projectMember.setProjectId(projectId);
                projectMember.setMemberId(memberId);
                projectMember.setMemberName(userEntity.getUserName());
                projectMember.setMemberPhone(userEntity.getUserInfo().getTelephone());
                projectMember.setMemberEmail(userEntity.getUserInfo().getEmail());
                projectMember.setMemberImg(userEntity.getUserInfo().getImage());
                projectMember.setCreateTime(System.currentTimeMillis());
                projectMember.setUpdateTime(System.currentTimeMillis());
                projectMember.setMemberLabel(0);

                int isExist = projectMemberService.findMemberIsExist(projectId, memberId);
                if(isExist==0){
                    projectMemberService.saveProjectMember(projectMember);
                    jsonObject.put("result",1);
                    jsonObject.put("msg","添加成功");
                    jsonObject.put("data",projectMemberService.findByProjectId(projectId));
                }else {
                    jsonObject.put("result",0);
                    jsonObject.put("msg","成员已经存在");
                }
            }
        }catch (Exception e){
            throw new AjaxException(e);
        }

        return jsonObject;
    }

    //项目设置
    @GetMapping("/projectSetting")
    public String projectSetting(@RequestParam String projectId, Model model){
        String userId = ShiroAuthenticationManager.getUserId();
        Project project = projectService.findProjectByProjectId(projectId);
        //获取项目拥有着信息
        UserEntity userEntity = userService.findById(project.getMemberId());

        model.addAttribute("project",project);
        model.addAttribute("user",userEntity);
        if(userId.equals(project.getMemberId())){
            model.addAttribute("hasPermission",1);
        }else{
            model.addAttribute("hasPermission",0);
        }
        return "objsetting";
    }


    /**
     * 移除项目成员 支持单独删除和批量删除
     */
    @PostMapping("/delProjectMember")
    @ResponseBody
    public JSONObject delProjectMember(@RequestParam String id){
        JSONObject jsonObject = new JSONObject();
        try {
            if(StringUtils.isEmpty(id)){
                jsonObject.put("result",0);
                jsonObject.put("msg","请选择组员");
            }else{
                String[] ids = id.split(",");
                for (int i=0;i<ids.length;i++){
                    projectMemberService.deleteProjectMemberById(ids[i]);
                }
                jsonObject.put("result",1);
                jsonObject.put("msg","删除成功");
            }
        }catch (Exception e){
            throw  new AjaxException(e);
        }
        return jsonObject;
    }

    @GetMapping("/projectMenu.html")
    public String projectMenu(@RequestParam(required = false) String projectId, Model model){
        try {
            Log log = new Log();
            log.setLogType(0);
            List<Log> logList = logService.findLogAllList(log);
            model.addAttribute("projectId",projectId);
            model.addAttribute("logList",logList);
        }catch (Exception e){
            throw new SystemException(e);
        }

        return "tk-project-menu";
    }

    @GetMapping("/projectTag.html")
    public String  projectTag(String projectId,Model model){
        List<Tag> tagList = tagService.findByProjectId(projectId);
        model.addAttribute("tagList",tagList);
        return "tk-look-tag";
    }

}