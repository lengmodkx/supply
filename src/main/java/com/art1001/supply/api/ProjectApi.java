package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Log;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.api.base.BaseController;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.TimeMap;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectFunc;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.project.ProjectSimpleInfo;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.MemberViewResult;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.vo.MenuVo;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.organization.OrganizationService;
import com.art1001.supply.service.partment.PartmentService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.project.ProjectSimpleInfoService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.resource.ProResourcesService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 项目
 *
 * @author 汪亚锋
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 */
@Slf4j
@Validated
@RestController
@RequestMapping("projects")
public class ProjectApi extends BaseController {

    @Resource
    private ProjectService projectService;

    @Resource
    private RelationService relationService;

    @Resource
    private ProjectMemberService projectMemberService;

    @Resource
    private FileService fileService;

    @Resource
    private TaskService taskService;

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ProResourcesService proResourcesService;

    @Resource
    private OrganizationService organizationService;

    @Resource
    private PartmentService partmentService;

    @Resource
    private ProjectSimpleInfoService projectSimpleInfoService;


    /**
     * 创建项目
     *
     * @param projectName 项目名称
     * @param projectDes  项目描述
     * @param orgId       企业id
     * @return 是否成功
     */
    @PostMapping
    public JSONObject createProject(@RequestParam(value = "orgId") String orgId,
                                    @RequestParam(value = "projectName") String projectName,
                                    @RequestParam(value = "projectDes") String projectDes,
                                    @RequestParam(value = "startTime") Long startTime,
                                    @RequestParam(required = false) @Length(max = 32, message = "parentId参数不正确!") String parentId,
                                    @RequestParam(value = "endTime") Long endTime,
                                    @RequestParam(value = "templateId", required = false) String templateId) {
        JSONObject object = new JSONObject();
        if (endTime < startTime) {
            object.put("result", 1);
            object.put("msg", "项目开始时间不能大于结束时间。");
            return object;
        }
        Project project = new Project();
        project.setOrganizationId(orgId);
        project.setProjectName(projectName);
        project.setProjectDel(0);
        project.setProjectDes(projectDes);
        project.setStartTime(startTime);
        if (StringUtils.isNotEmpty(parentId)) {
            project.setParentId(parentId);
        }
        project.setCreateTime(System.currentTimeMillis());
        project.setUpdateTime(System.currentTimeMillis());
        project.setEndTime(endTime);
        project.setMemberId(ShiroAuthenticationManager.getUserId());
        projectService.saveProject(project);

        //写资源表
        object.put("result", 1);
        object.put("data", project.getProjectId());
        object.put("msg", "创建成功");
        return object;
    }


    /**
     * 局部更新项目
     *
     * @param projectId     项目id
     * @param projectName   项目名称
     * @param projectDes    项目简介
     * @param isPublic      是否公开
     * @param projectCover  项目封面
     * @param projectDel    是否移入回收站
     * @param projectStatus 是否归档
     * @param newProjectId 修改的项目id
     * @param projectSchedule 项目进度 默认0
     * @param updateSchedule 自动更新项目进度 0不自动更新 1自动更新 默认0
     * @return json
     */
    @PutMapping("/{projectId}")
    public JSONObject projectUpdate(@PathVariable(value = "projectId") String projectId,
                                    @RequestParam(value = "projectName", required = false) String projectName,
                                    @RequestParam(value = "projectDes", required = false) String projectDes,
                                    @RequestParam(value = "isPublic", required = false) Integer isPublic,
                                    @RequestParam(value = "projectCover", required = false) String projectCover,
                                    @RequestParam(value = "projectDel", required = false) Integer projectDel,
                                    @RequestParam(value = "projectStatus", required = false) Integer projectStatus,
                                    @RequestParam(value = "startTime", required = false) Long startTime,
                                    @RequestParam(value = "endTime", required = false) Long endTime,
                                    @RequestParam(value = "newProjectId", required = false) String newProjectId,
                                    @RequestParam(value = "projectSchedule",defaultValue = "0",required = false) Integer projectSchedule,
                                    @RequestParam(value = "updateSchedule",defaultValue = "0",required = false)Integer updateSchedule
    ) {
        JSONObject object = new JSONObject();
        try {
            Project project = new Project();
            project.setProjectId(projectId);
            project.setProjectName(projectName);
            project.setProjectDes(projectDes);
            project.setIsPublic(isPublic);
            project.setStartTime(startTime);
            project.setEndTime(endTime);
            if (StringUtils.isNotEmpty(projectCover) && !"upload/project/bj.png".equals(projectCover)) {
                //将新的图片路径写入项目
                project.setProjectCover(projectCover);
            }
            project.setProjectDel(projectDel);
            project.setProjectStatus(projectStatus);
            project.setUpdateTime(System.currentTimeMillis());
            if (StringUtils.isNotEmpty(newProjectId)) {
                project.setNewProjectId(newProjectId);
            }
            if (Constants.ONE.equals(updateSchedule)) {
                project.setProjectSchedule(organizationService.automaticUpdateProjectSchedule(project));
            }else {
                project.setProjectSchedule(projectSchedule);
            }
            projectService.updateProject(project);
            project.setProjectId(newProjectId);
            object.put("result", 1);
            object.put("msg", "更新成功");
            object.put("data", project);
        } catch (Exception e) {
            log.error("保存失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 获取 我创建的项目，我参与的项目，我收藏的项目，项目回收站
     *
     * @return
     */
    @Log
    // @Push(value = PushType.I2,type = 2)
    @GetMapping
    public JSONObject projects() {
        JSONObject object = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            List<Project> projectList = projectService.findProjectByUserId(userId);
            object.put("result", 1);
            object.put("data", projectList);
            object.put("msg", "获取成功");
        } catch (Exception e) {
            log.error("系统异常,信息获取失败:", e);
            throw new SystemException(e);
        }
        return object;
    }

    /**
     * 获取项目详情
     *
     * @param projectId 项目id
     * @return
     */
    @Log
    @GetMapping("/{projectId}")
    public JSONObject projectDetail(@PathVariable String projectId) {
        JSONObject object = new JSONObject();
        try {
            Project project = projectService.findProjectByProjectId(projectId);
            object.put("result", 1);
            object.put("data", project);
            return object;
        } catch (Exception e) {
            log.error("系统异常,信息获取失败:", e);
            throw new SystemException(e);
        }
    }

    /**
     * 删除项目
     *
     * @param projectId 项目id
     * @return
     */
//    @Push(value = PushType.D12,type = 1)
    @DeleteMapping("/{projectId}")
    public JSONObject deleteProject(@PathVariable String projectId) {
        JSONObject object = new JSONObject();
        try {
            projectService.removeById(projectId);
            object.put("result", 1);
            object.put("msg", "删除成功");
        } catch (Exception e) {
            log.error("系统异常,项目删除失败:", e);
            throw new AjaxException(e);
        }
        return object;
    }

    @GetMapping("/{projectId}/menu")
    public JSONObject initMenu(@PathVariable String projectId) {
        JSONObject object = new JSONObject();
        try {
            Project project = projectService.getById(projectId);
            List<ProjectFunc> funcList = JSON.parseArray(project.getFunc(), ProjectFunc.class);
            funcList.forEach(item -> {
                switch (item.getFuncName()) {
                    case "文件":
                        item.setSuffix(fileService.findParentId(projectId));
                        break;
                    case "任务":
                        item.setSuffix(projectMemberService.findDefaultGroup(projectId, ShiroAuthenticationManager.getUserId()));
                        break;
                    default:

                }
            });
            object.put("result", 1);
            object.put("data", funcList);
            object.put("project", project);
        } catch (NullPointerException e) {
            object.put("result", 0);
            object.put("msg", "项目不存在");
        } catch (Exception e) {
            throw new AjaxException("系统异常,信息获取失败", e);
        }
        return object;
    }

    /**
     * 任务界面初始化
     *
     * @param projectId 项目id
     * @return
     */
    @GetMapping("/{projectId}/tasks")
    public JSONObject taskIndex(@PathVariable String projectId) {
        JSONObject object = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            List<String> keyList = proResourcesService.getMemberResourceKey(projectId, userId);
            redisUtil.remove("perms:" + userId);
            redisUtil.lset("perms:" + userId, keyList);
            redisUtil.set("userId:" + userId, projectId);
            String groupId = projectMemberService.findDefaultGroup(projectId, userId);
            //查询项目默认分组
            Relation relation = new Relation();
            relation.setParentId(groupId);
            relation.setLable(1);
            List<Relation> taskMenu = relationService.findRelationAllList(relation);
            ProjectMember projectMember = projectMemberService.getOne(new QueryWrapper<ProjectMember>()
                    .eq("project_Id", projectId).eq("member_id", userId));
            object.put("roleKey", projectMember.getRoleKey());
            object.put("userId", userId);
            object.put("result", 1);
            object.put("menus", taskMenu);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 用于自动化流程显示列表
     * @param projectId
     * @return
     */
    @GetMapping("/{projectId}/taskIndex2")
    public JSONObject taskIndex2(@PathVariable("projectId") String projectId){
               JSONObject object = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            List<MenuVo>menuVos=projectService.taskIndex(projectId,userId);

            ProjectMember projectMember = projectMemberService.getOne(new QueryWrapper<ProjectMember>()
                    .eq("project_Id", projectId).eq("member_id", userId));
            object.put("roleKey", projectMember.getRoleKey());
            object.put("userId", userId);
            object.put("result", 1);
            object.put("menus", menuVos);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException(e);
        }
        return object;
    }


    /**
     * 获取成员任务视图
     *
     * @param projectId 项目id
     * @return 数据
     */
    @RequestMapping("view_member")
    public Result memberView(@NotNull(message = "项目id不能为空!") String projectId) {
        log.info("Get member view data. [{}]", projectId);
        List<MemberViewResult> memberViewResults = taskService.memberView(projectId);
        return Result.success(memberViewResults);

    }

    /**
     * 项目收藏/取消收藏
     *
     * @param projectId 项目id
     */
    @Log
    @PutMapping("/{projectId}/collect")
    public JSONObject collectProject(@PathVariable(value = "projectId") String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            ProjectMember projectMember = projectMemberService.getOne(new QueryWrapper<ProjectMember>().eq("project_id", projectId).eq("member_id", userId));
            //如果等于0，说明收藏表不存在项目的收藏，此时插入
            if (projectMember.getCollect() == 0) {
                projectMember.setCollect(1);
                projectMemberService.updateById(projectMember);
                jsonObject.put("result", 1);
                jsonObject.put("msg", "收藏成功");
            } else {
                //收藏表存在该项目，则取消收藏，删除收藏表的项目
                projectMember.setCollect(0);
                projectMemberService.updateById(projectMember);
                jsonObject.put("result", 1);
                jsonObject.put("msg", "取消收藏成功");
            }
            jsonObject.put("data", projectService.findProjectByUserId(userId));
        } catch (Exception e) {
            throw new AjaxException(e);
        }

        return jsonObject;
    }

    /**
     * 项目归档/取消归档
     *
     * @param projectId 项目id
     * @param status    要操作的 标识
     */
    @Log
    @PutMapping("/{projectId}/status")
    public JSONObject updateStatus(@PathVariable String projectId, @RequestParam(value = "status") Integer status) {
        JSONObject object = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            Project project = new Project();
            project.setProjectId(projectId);
            if (status == 1) {
                project.setProjectStatus(0);
                object.put("msg", "取消项目归档成功!");
            } else {
                project.setProjectStatus(1);
                object.put("msg", "项目已归档");
            }
            projectService.updateById(project);
            object.put("result", 1);
            object.put("data", projectService.findProjectByUserId(userId));
        } catch (Exception e) {
            throw new AjaxException("项目归档操作异常!", e);
        }
        return object;
    }

    /**
     * 模糊搜索项目
     *
     * @param projectName 项目名称
     * @param condition   搜索条件(created,join,star)
     * @return
     */
    @GetMapping("/seach")
    public JSONObject seachByName(@RequestParam(required = false) String projectName,
                                  @RequestParam(required = false) String condition,
                                  @RequestParam String orgId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", projectService.seachByName(projectName, condition, orgId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,数据获取失败!", e);
        }
    }

    /**
     * 移入项目至回收站
     *
     * @param projectId 项目id
     * @return
     */
    @Push(value = PushType.I1, type = 3)
    @PutMapping("/{projectId}/recycle_bin")
    public JSONObject projectMoveRecycleBin(@PathVariable(value = "projectId") String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            Project project = new Project();
            project.setProjectDel(1);
            project.setProjectId(projectId);
            projectService.updateById(project);
            jsonObject.put("msgId", projectId);
            jsonObject.put("publicType", "project");
            jsonObject.put("id", projectId);
            jsonObject.put("data", projectId);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "项目移入回收站成功!");

        } catch (Exception e) {
            throw new AjaxException("项目移入回收站失败!", e);
        }
        return jsonObject;
    }


    /**
     * 甘特图移入项目至回收站
     * @param projectId 项目id
     * @return
     *//*
    @Push(value = PushType.I1,type = 3)
    //@PutMapping("")
   /* public JSONObject MoveRecycleBin(@PathVariable(value = "projectId") String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            Project project = new Project();
            project.setProjectDel(1);
            project.setProjectId(projectId);
            projectService.updateById(project);
            jsonObject.put("msgId", projectId);
            jsonObject.put("publicType", "project");
            jsonObject.put("id", projectId);
            jsonObject.put("data", projectId);
            jsonObject.put("result",1);
            jsonObject.put("msg","项目移入回收站成功!");
        } catch (Exception e){
            throw new AjaxException("项目移入回收站失败!",e);
        }
        return jsonObject;
    }*/


    /**
     * 将向项目移出回收站
     *
     * @param projectId 项目id
     * @return
     */
    @PutMapping("/{projectId}/out_recycle_bin")
    public JSONObject projectOutRecycleBin(@PathVariable(value = "projectId") String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            Project project = new Project();
            project.setProjectDel(0);
            projectService.update(project, new QueryWrapper<Project>().eq("project_id", projectId));
            jsonObject.put("result", 1);
            jsonObject.put("msg", "项目移出回收站成功!");
        } catch (Exception e) {
            throw new AjaxException("项目移出回收站失败!", e);
        }
        return jsonObject;
    }

    /**
     * 根据项目id 获取成员信息
     *
     * @param projectId 项目id
     * @return 成员信息
     */
    @GetMapping("/{projectId}/members")
    public JSONObject getMembersByProject(@PathVariable String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", projectMemberService.findByProjectId(projectId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,获取成员信息失败!", e);
        }
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param:
     * @return:
     * @Description: 根据成员名称或成员电话模糊查询项目成员
     * @create: 16:12 2020/5/15
     */
    @GetMapping("/searchMemberByName/{projectId}")
    public JSONObject searchMemberByName(@RequestParam(required = false) String condition, @PathVariable String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", projectMemberService.searchMemberByName(condition, projectId));
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，请稍后再试");
        }
        return jsonObject;
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param: projectId 项目id
     * @return:
     * @Description: 获取项目成员详细信息
     * @create: 11:33 2020/4/22
     */
    @GetMapping("/{orgId}/membersInfo/{memberId}")
    public JSONObject getMembersInfoByProject(@PathVariable String orgId, @PathVariable String memberId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", projectMemberService.findMemberByProjectIdAndMemberId(orgId, memberId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,获取成员信息失败!", e);
        }
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param: 企业详细信息DTO
     * @return:
     * @Description: 修改企业成员详细信息
     * @create: 11:32 2020/4/22
     */
    @PostMapping("/updateMembersInfo")
    public JSONObject updateMembersInfo(
            @RequestParam(value = "memberId") String memberId,
            @RequestParam(value = "orgId") String orgId,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "entryTime", required = false) String entryTime,
            @RequestParam(value = "job", required = false) String job,
            @RequestParam(value = "memberLabel", required = false) String memberLabel,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "memberEmail", required = false) String memberEmail,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "birthday", required = false) String birthday,
            @RequestParam(value = "deptId", required = false) String deptId

    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            projectService.updateMembersInfo(memberId, orgId, userName, entryTime, job, memberLabel, address, memberEmail, phone, birthday, deptId);
            jsonObject.put("data", projectMemberService.findMemberByProjectIdAndMemberId(orgId, memberId));
            jsonObject.put("result", 1);
            jsonObject.put("message", "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常,修改成员信息失败!");
        }


        return jsonObject;
    }


    /**
     * 获取项目甘特图的数据
     *
     * @param projectId 项目id
     * @return
     */
    @GetMapping("/gantt_chart/{projectId}")
    public JSONObject ganttChart(@PathVariable String projectId, @RequestParam(required = false) String groupId) {
        JSONObject jsonObject = new JSONObject();
        boolean projectNotExist = !projectService.checkIsExist(projectId);
        if (projectNotExist) {
            return error("项目不存在!");
        }

        jsonObject.put("data", projectService.getGanttChart(projectId, groupId));
        jsonObject.put("result", 1);
        return jsonObject;
    }

    /**
     * 更新项目的开始/结束时间
     *
     * @param projectId 项目id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 结果
     */
    @PutMapping("{projectId}/start_end_time")
    public JSONObject updateStartEndTime(@PathVariable String projectId, @RequestParam(required = false) Long startTime, @RequestParam(required = false) Long endTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (startTime == null && endTime == null) {
                jsonObject.put("msg", "开始和结束时间必须给定一个!");
                jsonObject.put("result", 0);
                return jsonObject;
            }
            Project project = new Project();
            project.setProjectId(projectId);
            project.setStartTime(startTime);
            project.setEndTime(endTime);
            if (projectService.updateById(project)) {
                jsonObject.put("result", 1);
            }
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,更新失败!", e);
        }
    }

    /**
     * 获取该日历上的任务信息
     *
     * @param projectId 项目id
     * @return 日历任务信息
     */
    @GetMapping("/{projectId}/calendar")
    public JSONObject getCalendarTaskInfo(@PathVariable String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", new JSONObject().fluentPut("tasks", taskService.getCalendarTask(projectId)).fluentPut("schedules", scheduleService.getCalendarSchedule(projectId)));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,更新失败!", e);
        }
    }


    /**
     * 甘特图修改
     *
     * @param projectId 项目id
     * @return 成功信息
     */
    @PostMapping("/{projectId}/updateProInfo")
    public JSONObject updateProjectInfo(@PathVariable String projectId, String projectName, Long startTime, Long endTime) {
        JSONObject jsonObject = new JSONObject();
        try {
            Project project = projectService.findProjectByProjectId(projectId);
            if (project != null) {
                project.setProjectName(projectName);
                project.setStartTime(startTime);
                project.setEndTime(endTime);
                boolean project_id = projectService.update(project, new QueryWrapper<Project>().eq("project_id", projectId));
                if (project_id) {
                    jsonObject.put("msg", "修改项目信息成功");
                    jsonObject.put("result", 1);
                } else {
                    jsonObject.put("msg", "修改项目信息失败");
                    jsonObject.put("result", 0);
                }
            } else {
                Task task = taskService.findTaskByTaskId(projectId);
                task.setTaskName(projectName);
                task.setStartTime(startTime);
                task.setEndTime(endTime);
                boolean task_id = taskService.update(task, new QueryWrapper<Task>().eq("task_id", projectId));
                if (task_id) {
                    jsonObject.put("msg", "修改任务信息成功");
                    jsonObject.put("result", 1);
                } else {
                    jsonObject.put("msg", "修改任务信息失败");
                    jsonObject.put("result", 0);
                }
            }
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,更新失败!", e);
        }
    }


    /**
     * 更新项目封面
     *
     * @param projectId 项目id
     * @return obj
     */
    @PostMapping("{projectId}/picture")
    public JSONObject updateProjectPic(@PathVariable String projectId, String fileName) {
        JSONObject jsonObject = new JSONObject();
        try {
            //根据项目id获取需要更改的项目信息
            Project project = projectService.findProjectByProjectId(projectId);
            //删除云端图片
            //AliyunOss.deleteFile(project.getProjectCover());
            //修改数据库
            Integer integer = projectService.updatePictureById(projectId, fileName);

            if (integer == 1) {
                jsonObject.put("result", 1);
                jsonObject.put("msg", "图片更改成功!");
            } else {
                jsonObject.put("result", 0);
                jsonObject.put("msg", "图片更改失败!");
            }
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException("系统异常,更新失败!", e);
        }
    }

    /**
     * 获取到项目的树形图数据
     *
     * @return
     */
    @GetMapping("/tree")
    public JSONObject getProjectTreeData(@RequestParam(required = false) @Length(max = 32) String projectId) {
        return success(projectService.getTreeData(projectId));
    }

    @GetMapping("/re")
    public Object re() {
        LambdaQueryWrapper<Project> eq = new QueryWrapper<Project>().lambda().select(Project::getProjectId).eq(Project::getMemberId, "74bf02c0299d4661b684c046fe2ad8f2");
        List<Project> list = projectService.list(eq);
        list.forEach(p -> {
            ProjectMember projectMember = new ProjectMember();
            projectMember.setMemberId("74bf02c0299d4661b684c046fe2ad8f2");
            projectMember.setCreateTime(System.currentTimeMillis());
            projectMember.setUpdateTime(System.currentTimeMillis());
            projectMember.setMemberLabel(1);
            projectMember.setProjectId(p.getProjectId());
            projectMemberService.save(projectMember);
        });
        return 1;
    }

    @GetMapping("/re1")
    public Object re1() {
        LambdaQueryWrapper<Relation> eq = new QueryWrapper<Relation>().lambda().eq(Relation::getRelationName, "任务");
        List<Relation> list = relationService.list(eq);
        list.forEach(p -> {
            LambdaQueryWrapper<ProjectMember> eq1 = new QueryWrapper<ProjectMember>().lambda().eq(ProjectMember::getMemberId, "74bf02c0299d4661b684c046fe2ad8f2");
            List<ProjectMember> list1 = projectMemberService.list(eq1);
            list1.forEach(p1 -> {
                if (p1.getProjectId().equals(p.getProjectId())) {
                    ProjectMember projectMember = new ProjectMember();
                    projectMember.setId(p1.getId());
                    projectMember.setDefaultGroup(p.getRelationId());
                    projectMemberService.updateById(projectMember);
                }
            });

        });
        return 1;
    }

    /**
     * 获取企业中的用户项目列表
     *
     * @param orgId 企业id
     * @return 用户项目列表
     */
    @RequestMapping("/org/projects")
    public Result getUserProjectsInCurrOrg(@NotNull(message = "企业id不能为空!") String orgId) {
        log.info("Get user the project list in organization. [{}]", orgId);
        List<Project> projects = projectMemberService.getUserProjectsInOrg(ShiroAuthenticationManager.getUserId(), orgId);
        return Result.success(projects);
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param: orgId 企业id
     * @Param: memberId 用户id
     * @Param: dateSort 查询时间戳
     * @return: dateSort 查询时间  String类型 时间戳
     * @Description: 最近动态API
     * @create: 10:39 2020/4/26
     */
    @GetMapping("/{memberId}/{orgId}/tasks/{time}")
    public JSONObject getTasksByUserIdAndProjectId(@PathVariable String orgId, @PathVariable String memberId, @PathVariable String time) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", projectService.getTaskDynamicVOS(orgId, memberId, time));
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，查询失败");
        }
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param:
     * @return:
     * @Description: 获取当前日期最近一年的年月信息
     * @create: 11:22 2020/4/30
     */
    @GetMapping("/getYearOfAllMonth")
    public JSONObject getYearOfAllMonth() {
        JSONObject jsonObject = new JSONObject();
        try {
            Map<Long, String> times = DateUtils.getYearOfAllMonth();

            List<TimeMap> list = Lists.newArrayList();
            for (Map.Entry<Long, String> entry : times.entrySet()) {
                TimeMap timeMap = new TimeMap();
                timeMap.setTimeStamp(entry.getKey());
                timeMap.setYearOfMonth(entry.getValue());
                list.add(timeMap);
            }
            jsonObject.put("data", list);
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，获取信息失败");
        }

    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param: orgId 企业id
     * @return:
     * @Description: 查询企业下的部门信息
     * @create: 10:45 2020/4/29
     */
    @GetMapping("/{orgId}/getDeptNameByOrgId")
    public JSONObject getDeptNameByOrgId(@PathVariable String orgId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", partmentService.findOrgPartmentInfo(orgId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，请稍后再试");
        }
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param: memberId
     * @return:
     * @Description: 项目经历API 根据成员id获取项目信息
     * @create: 13:51 2020/5/7
     */
    @GetMapping("/getExperience/{organizationId}/{memberId}")
    public JSONObject getExperience(@PathVariable("memberId") String memberId, @PathVariable String organizationId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", projectService.getExperience(memberId, organizationId));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，请稍后再试");
        }
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param: memberId 成员id
     * @return:
     * @Description: 获取项目经历中已添加的项目信息
     * @create: 15:56 2020/5/9
     */
    @GetMapping("/getIsExperience/{organizationId}/{memberId}")
    public JSONObject getIsExperience(@PathVariable String memberId, @PathVariable String organizationId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("data", projectSimpleInfoService.getIsExperience(memberId, organizationId));
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，请稍后再试");
        }
        return jsonObject;
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param: memberId 成员id
     * @Param: projectId 项目id
     * @return:
     * @Description: 添加项目经历
     * @create: 14:40 2020/5/7
     */
    @Push(value = PushType.F2, type = 1)
    @PutMapping("/{memberId}/{projectId}/{organizationId}")
    public JSONObject addExperience(@PathVariable("memberId") String memberId, @PathVariable("projectId") String projectId, @PathVariable("organizationId") String organizationId) {
        JSONObject jsonObject = new JSONObject();
        try {
            ProjectSimpleInfo projectSimpleInfo = new ProjectSimpleInfo();
            projectSimpleInfo.setMemberId(memberId);
            projectSimpleInfo.setProjectId(projectId);
            projectSimpleInfo.setOrganizationId(organizationId);
            projectSimpleInfo.setCreateTime(System.currentTimeMillis());
            projectSimpleInfo.setUpdateTime(System.currentTimeMillis());
            if (projectSimpleInfoService.save(projectSimpleInfo)) {
                jsonObject.put("result", 1);
                jsonObject.put("msg", "成功!");
            } else {
                jsonObject.put("result", 0);
            }
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，请稍后再试");
        }
    }

    /**
     * @Author: 邓凯欣
     * @Email：dengkaixin@art1001.com
     * @Param:
     * @return:
     * @Description: 取消项目经历
     * @create: 14:41 2020/5/7
     */
    @Push(value = PushType.F3, type = 1)
    @DeleteMapping("/deleteExperience/{memberId}/{projectId}")
    public JSONObject deleteExperience(@PathVariable("memberId") String memberId, @PathVariable("projectId") String projectId) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (projectSimpleInfoService.remove(new QueryWrapper<ProjectSimpleInfo>().eq("member_id", memberId).eq("project_id", projectId))) {
                jsonObject.put("result", 1);
                jsonObject.put("msg", "成功");
            } else {
                jsonObject.put("result", 0);
            }
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AjaxException("系统异常，请稍后再试");
        }
    }

    /**
     * 项目角色判断 成员1 拥有者或管理员0
     * @param projectId
     * @return
     */
    @GetMapping("/judgmentRoles/{projectId}")
    public JSONObject judgmentRoles(@PathVariable("projectId") String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result",1);
            jsonObject.put("data",projectService.judgmentRoles(projectId));
            return jsonObject;
        } catch (Exception e) {
            throw new AjaxException(e);
        }
    }


}
