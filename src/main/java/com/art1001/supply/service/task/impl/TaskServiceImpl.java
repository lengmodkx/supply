package com.art1001.supply.service.task.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.entity.fabulous.Fabulous;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.quartz.QuartzInfo;
import com.art1001.supply.entity.statistics.ChartsVO;
import com.art1001.supply.entity.statistics.StaticticsVO;
import com.art1001.supply.entity.statistics.Statistics;
import com.art1001.supply.entity.statistics.StatisticsResultVO;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.tag.TagRelation;
import com.art1001.supply.entity.task.*;
import com.art1001.supply.entity.template.TemplateData;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.fabulous.FabulousMapper;
import com.art1001.supply.mapper.task.TaskMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.quartz.MyJob;
import com.art1001.supply.quartz.QuartzService;
import com.art1001.supply.quartz.job.RemindJob;
import com.art1001.supply.service.apiBean.ApiBeanService;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.quartz.QuartzInfoService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.art1001.supply.service.task.TaskRemindRuleService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.util.IdGen;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * taskServiceImpl
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper,Task> implements TaskService {

    /** taskMapper接口*/
    @Resource
    private TaskMapper taskMapper;

    /** userMapper接口 */
    @Resource
    private UserMapper userMapper;

    /** FablousMapper接口*/
    @Resource
    private FabulousMapper fabulousMapper;

	/** TaskCollectService接口  */
	@Resource
    private PublicCollectService publicCollectService;

    /** 用户逻辑层接口 */
    @Resource
    private UserService userService;

    /** 关系层逻辑接口 */
    @Resource
    private RelationService relationService;

    /** 标签的逻辑层接口 */
    @Resource
    private TagService tagService;

    /** 用户消息的逻辑层接口 */
    @Resource
    private UserNewsService userNewsService;

    /** 日志逻辑层接口 */
    @Resource
    private LogService logService;

    /** 关联信息的逻辑层接口 */
    @Resource
    private BindingService bindingService;

    /** 项目成员逻辑层接口 */
    @Resource
    private ProjectMemberService projectMemberService;

    /** 标签标签的逻辑层接口 */
    @Resource
    private TagRelationService tagRelationService;

    /** 用户更新json信息的接口 */
    @Resource
    private ApiBeanService apiBeanService;

    /** 任务的提醒规则接口 */
    @Resource
    private TaskRemindRuleService taskRemindRuleService;

    /** quartz 信息的接口 */
    @Resource
    private QuartzInfoService quartzInfoService;

    /** quartz操作接口 */
    @Resource
    private QuartzService quartzService;

	/**
	 * 通过taskId获取单条task数据
	 * @param taskId
	 * @return
	 */
	@Override
	public Task findTaskByTaskId(String taskId){
		return taskMapper.findTaskByTaskId(taskId);
	}

	/**
	 * 将添加的任务信息保存至数据库
     * @param task task信息
     * @param taskRemindRules 提醒规则
     */
	@Override
	public void saveTask(Task task, String taskRemindRules) {
        //设置该任务的创建时间
        task.setCreateTime(System.currentTimeMillis());
        //设置该任务的最后更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //根据查询菜单id 查询 菜单id 下的 最大排序号
        Integer maxOrder = relationService.findMenuTaskMaxOrder(task.getTaskMenuId());
        task.setOrder(++maxOrder);
        //保存任务信息
        save(task);

        List<TaskRemindRule> remindList = JSON.parseArray(taskRemindRules, TaskRemindRule.class);
        remindList.forEach(item -> {
            item.setId(IdGen.uuid());
            item.setTaskId(task.getTaskId());
            //存储quartz的job信息和trigger信息
            QuartzInfo quartzInfo = new QuartzInfo();
            quartzInfo.setId(IdGen.uuid());
            quartzInfo.setJobName(IdGen.uuid());
            quartzInfo.setJobGroup("task");
            quartzInfo.setRemindId(item.getId());
            quartzInfo.setTriggerGroup("task");
            quartzInfoService.save(quartzInfo);

            MyJob myJob = new MyJob();
            myJob.setJobGroupName("task");
            myJob.setTriggerGroupName("task");
            myJob.setJobName(quartzInfo.getJobName());

            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("users",item.getUsers());

            myJob.setJobDataMap(jobDataMap);
            myJob.setCronTime(remindCron(item.getTaskId(), item.getRemindType(), item.getNum(), item.getTimeType(), item.getCustomTime()));
            quartzService.addJobByCronTrigger(RemindJob.class,myJob);
        });
        //保存任务的提醒规则
        taskRemindRuleService.saveBatch(remindList);
    }

    /**
     * 将添加的任务信息保存至数据库
     * @param task task信息
     */
    @Override
    public void saveTask(Task task) {
        //设置该任务的创建时间
        task.setCreateTime(System.currentTimeMillis());
        //设置该任务的最后更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //根据查询菜单id 查询 菜单id 下的 最大排序号
        Integer maxOrder = relationService.findMenuTaskMaxOrder(task.getTaskMenuId());
        task.setOrder(++maxOrder);
        //保存任务信息
        save(task);
    }

    /**
     * 批量生成任务
     * @param projectId 项目id
     * @param menuId 列表id
     * @param templateDataList 数据
     */
    @Override
    public void saveTaskBatch(String projectId, String menuId, List<TemplateData> templateDataList) {
        String id = ShiroAuthenticationManager.getUserId();
        taskMapper.saveTaskBatch(projectId,menuId,templateDataList,id);
    }

    @Override
    public List<Task> findTaskAllList() {
        return null;
    }


    /**
	 * 重写方法
     * 移入回收站
     * @param taskId 当前任务id
     * @return
     */
    @Override
    public Log moveToRecycleBin(String taskId) {
        //把该任务放到回收站
        int result = taskMapper.moveToRecycleBin(taskId,System.currentTimeMillis());
        Task task = new Task();
        task.setTaskId(taskId);
        //任务状态为0 日志打印内容为 xxx把任务移入了回收站
        Log log = logService.saveLog(taskId,TaskLogFunction.P.getName(),1);
        return log;
    }


    /**
     * 移动任务至 ( 项目、分组、菜单 )
     * @param taskId 任务id
     * @param projectId 项目id
     * @param groupId 组id
     * @param menuId 菜单id
     * @return
     */
    @Override
    public void mobileTask(String taskId, String projectId, String groupId,String menuId) {
        Task task = getById(taskId);
        task.setTaskId(taskId);
        task.setProjectId(projectId);
        task.setTaskMenuId(menuId);
        task.setTaskGroupId(groupId);
        task.setUpdateTime(System.currentTimeMillis());
        updateById(task);

        //移动子任务
        if(task.getTaskList()!=null&&task.getTaskList().size()>0){
            task.getTaskList().forEach(subTask->{
                subTask.setProjectId(projectId);
                subTask.setUpdateTime(System.currentTimeMillis());
                updateById(subTask);
            });
        }
    }

    /**
     * 根据任务id 数组查询出多条任务信息
     * @param taskId 任务id数组
     * @return
     */
    @Override
    public List<Task> findManyTask(String[] taskId) {
        return taskMapper.findManyTask(taskId);
    }

    /**
     * 转换子任务为顶级任务
     * @param task 包含任务的id,名称
     * @return
     */
    @Override
    public Log turnToFatherLevel(Task task) {
        Task fatherLevelTask = taskMapper.findFatherLevelProjectId(task.getParentId());
        //将任务的父级任务设置为0 (没有父级任务)
        task.setParentId("0");
        //设置项目id
        task.setProjectId(fatherLevelTask.getProjectId());
        //设置更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //更新任务信息
        int result = taskMapper.updateTask(task);
        StringBuilder content = new StringBuilder("");
        //拼接日志内容
        content.append(TaskLogFunction.A8.getName()).append(" ").append(task.getTaskName()).append(" ").append(TaskLogFunction.A9.getName());
        //保存日志
        Log log = logService.saveLog(task.getTaskId(), content.toString(),1);
        return log;
    }

    /**
     * 清除任务的开始时间
     * @param task 任务的实体信息
     * @return
     */
    @Override
    public Log removeTaskStartTime(Task task) {
        StringBuilder content = new StringBuilder("");
        int result = 0;
        //设置最后更新时间
        task.setUpdateTime(System.currentTimeMillis());
        result = taskMapper.removeTaskStartTime(task);
        content.append(TaskLogFunction.J.getName());
        //保存操作日志
        Log log = logService.saveLog(task.getTaskId(), content.toString(),1);

        return log;
    }

    /**
     * 清除任务的结束时间
     * @param task 任务的实体信息
     * @return
     */
    @Override
    public Log removeTaskEndTime(Task task) {
        StringBuilder content = new StringBuilder("");
        int result = 0;
        //设置最后更新时间
        task.setUpdateTime(System.currentTimeMillis());
        result = taskMapper.removeTaskEndTime(task);
        content.append(TaskLogFunction.K.getName());
        //保存操作日志
        Log log = logService.saveLog(task.getTaskId(), content.toString(),1);

        return log;
    }

    /**
     * 添加项目成员
     * @return
     */
    @Override
    public Log addAndRemoveTaskMember(String taskId,String memberIds) {
        Log log = null;
        StringBuilder content = new StringBuilder();
        Task task = taskMapper.findTaskByTaskId(taskId);

        List<String> list1 = Arrays.asList(task.getTaskUIds().split(","));
        List<String> list2 = Arrays.asList(memberIds.split(","));

        List<String> subtract1 = ListUtils.subtract(list1, list2);
        if(subtract1 != null && subtract1.size() > 0){
            content.append(TaskLogFunction.B.getName());
            for (Object aSubtract1 : subtract1) {
                UserEntity user = userMapper.findUserById(aSubtract1.toString());
                content.append(user.getUserName()).append(",");
            }
            //保存被移除的参与者的消息信息
            userNewsService.saveUserNews(subtract1.toArray(new String[0]),taskId,BindingConstants.BINDING_TASK_NAME,TaskLogFunction.B.getName(),0);
        }

        List<String> subtract2 = ListUtils.subtract(list2, list1);
        //保存被添加进来的参与者的消息信息
        userNewsService.saveUserNews(subtract2.toArray(new String[0]),taskId,BindingConstants.BINDING_TASK_NAME,TaskLogFunction.C.getName(),0);
        if(subtract2 != null && subtract2.size() > 0){
            content.append(TaskLogFunction.C.getName());
            for (Object aSubtract2 : subtract2) {
                UserEntity user = userMapper.findUserById(aSubtract2.toString());
                content.append(user.getUserName()).append(",");
            }
        }
        task.setTaskUIds(memberIds);
        if(subtract1.size()>0||subtract2.size()>0){
            taskMapper.updateTask(task);
            log = logService.saveLog(taskId,content.deleteCharAt(content.length()-1).toString(),1);
        }
        
        return log;
    }

    /**
     * 给当前任务点赞
     * @param task 任务的实体信息
     * @return
     */
    @Override
    public int clickFabulous(Task task) {
        Integer taskFabulous = taskMapper.findTaskFabulousCount(task.getTaskId());
        Fabulous fabulous = new Fabulous();
        fabulous.setPublicId(task.getTaskId());
        fabulous.setMemberId(ShiroAuthenticationManager.getUserId());
        fabulous.setFabulousId(System.currentTimeMillis());
        //添加任务和赞的关系数据
        int result = fabulousMapper.insert(fabulous);
        //更新任务得赞数量
        task.setFabulousCount(taskFabulous + 1);
        return taskMapper.updateTask(task);
    }

    /**
     * 用户取消赞
     * @param task 当前任务信息
     * @return
     */
    @Override
    public int cancelFabulous(Task task) {
        Integer taskFabulousCount = taskMapper.findTaskFabulousCount(task.getTaskId());
        //获取当前用户登录的id (暂时不用)
        String memberId = ShiroAuthenticationManager.getUserId();
        //这里要把当前任务的赞 - 1
        task.setFabulousCount(taskFabulousCount - 1);
        taskMapper.updateTask(task);
        return fabulousMapper.cancelFabulous(task.getTaskId(),memberId);
    }

    /**
     * 给当前任务添加子任务
     * @param parentTaskId 父任务的id
     * @param subLevel 子级任务信息
     * @return
     */
    @Override
    public Log addSubLevelTasks(String parentTaskId, Task subLevel) {
        //获取当前登录用户的id
        String id = ShiroAuthenticationManager.getUserId();
        //设置任务的层级
        subLevel.setLevel(2);
        //设置父任务id
        subLevel.setParentId(parentTaskId);
        //设置该任务是否删除 0 未删除 1 已删除
        subLevel.setTaskDel(0);
        //设置该任务的创建时间
        subLevel.setCreateTime(System.currentTimeMillis());
        //设置该任务的最后更新时间
        subLevel.setUpdateTime(System.currentTimeMillis());
        //设置该任务的初始状态
        subLevel.setTaskStatus("未完成");
        subLevel.setTaskUIds(ShiroAuthenticationManager.getUserId());
        //保存任务信息
        int result = taskMapper.saveTask(subLevel);
        //拼接日志字符串
        StringBuilder content = new StringBuilder("");
        content.append(TaskLogFunction.H.getName()).append(" ").append("\"").append(subLevel.getTaskName()).append("\"");
        //保存日志信息至数据库
        Task task = new Task();
        task.setTaskId(parentTaskId);
        Log log = logService.saveLog(task.getTaskId(), content.toString(),1);

        //查询父任务的任务名称
        String parentTask = taskMapper.findTaskNameById(parentTaskId);
        content = new StringBuilder("");
        content.append(TaskLogFunction.A15.getName()).append(" ").append(parentTask).append(TaskLogFunction.A16.getName()).append(" ").append(subLevel.getTaskName());
        logService.saveLog(task.getTaskId(), content.toString(),1);
        return log;
    }

    /**
     * 完成和重做子任务
     * @param task 当前任务信息
     * @return
     */
    @Override
    public Log resetAndCompleteSubLevelTask(Task task) {
        Task parentTask = taskMapper.findTaskBySubTaskId(task.getTaskId());
        if(parentTask.getTaskStatus().equals("完成")){
            throw new ServiceException();
        }
        StringBuilder content = new StringBuilder("");

        //拼接日志
        if(task.getTaskStatus().equals("完成")){
            content.append(TaskLogFunction.A12.getName()).append(" ").append("\"").append(task.getTaskName()).append("\"");
        } else {
            content.append(TaskLogFunction.I.getName()).append(" ").append("\"").append(task.getTaskName()).append("\"");
        }
        //更新任务信息
        int result = taskMapper.changeTaskStatus(task.getTaskId(),task.getTaskStatus(),System.currentTimeMillis());
        logService.saveLog(task.getTaskId(),content.toString(),1);
        Log log = logService.saveLog(parentTask.getTaskId(),content.toString(),1);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","子任务完成/重做");
        jsonObject.put("taskId",task.getTaskId());
        jsonObject.put("result",task.getTaskStatus());
        return log;
    }

    /**
     * 复制任务
     * @param taskId 当前任务信息
     * @param projectId 当前任务所在的项目id
     * @return
     */
    @Override
    public void copyTask(String taskId, String projectId, String groupId,String menuId) {
        Task oldTask = taskMapper.findTaskByTaskId(taskId);
        try {
            //更新新任务的创建时间
            oldTask.setCreateTime(System.currentTimeMillis());
            //设置新任务的更新时间
            oldTask.setUpdateTime(System.currentTimeMillis());
            oldTask.setProjectId(projectId);
            oldTask.setTaskMenuId(menuId);
            oldTask.setTaskGroupId(groupId);
            save(oldTask);
            if(oldTask.getTaskList()!=null&&oldTask.getTaskList().size()>0){
                oldTask.getTaskList().forEach(task->{
                    //设置新的子任务id
                    task.setProjectId(projectId);
                    //设置新的子任务的父任务id
                    task.setParentId(oldTask.getTaskId());
                    //设置新子任务的更新时间
                    task.setUpdateTime(System.currentTimeMillis());
                    //设置新子任务的创建时间
                    task.setCreateTime(System.currentTimeMillis());
                    save(task);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 收藏任务
     * @param task 任务实体信息
     * @return
     */
    @Override
    public int collectTask(Task task) {
        PublicCollect publicCollect = new PublicCollect();
        //设置收藏的id
        publicCollect.setMemberId(ShiroAuthenticationManager.getUserId());
        //设置收藏的项目的id
        publicCollect.setProjectId(task.getProjectId());
        //设置收藏的任务id
        publicCollect.setPublicId(task.getTaskId());
        //设置收藏的类型
        publicCollect.setCollectType(BindingConstants.BINDING_TASK_NAME);
        //设置这条收藏的创建时间
        publicCollect.setCreateTime(System.currentTimeMillis());
        //设置这条收藏的更新时间
        publicCollect.setUpdateTime(System.currentTimeMillis());
        //保存至数据库
        return publicCollectService.savePublicCollect(publicCollect);
    }

    /**
     * 取消收藏的任务
     * @param task 任务的信息
     * @return
     */
    @Override
    public int cancelCollectTask(Task task) {
        String memberId = ShiroAuthenticationManager.getUserId();
        int result = publicCollectService.deletePublicCollectById(memberId,task.getTaskId());
        return result;
    }

    /**
     * 查询该项目下的所有成员信息
     * @param projectId 当前项目的id
     * @param executor 当前任务的执行者信息
     * @return
     */
    @Override
    public List<UserEntity> findProjectAllMember(String projectId,String executor) {
        List<UserEntity> projectAllMember = userService.findProjectAllMember(projectId);
        return projectAllMember;
    }

    /**
     * 智能分组 分别为  查询 今天的任务 , 完成的任务, 未完成的任务
     * @param status 任务状态条件
     * @param projectId 项目id
     * @return
     */
    @Override
    public List<Task> intelligenceGroup(String status,String projectId) {
        List<Task> taskList = new ArrayList<Task>();
        //如果状态为空,就查询今天的任务 否则 按照任务的状态查询任务
        if(TaskStatusConstant.CURRENT_DAY_TASK.equals(status)){
            taskList = taskMapper.findTaskByToday(projectId);
        } else{
            if(TaskStatusConstant.HANG_IN_THE_AIR_TASK.equals(status)){
                status = "未完成";
            } else{
                status = "完成";
            }
            taskList = taskMapper.findTaskByStatus(status,projectId);
        }
        return taskList;
    }

    /**
     * 查询某个菜单下的所有任务的信息 包括执行者信息
     * @param menuId 菜单id
     * @return
     */
    @Override
    public List<Task> findTaskByMenuId(String menuId) {
        return taskMapper.findTaskByMenuId(menuId);
    }

    /**
     * 查询某个菜单下的所有任务的信息 不包括执行者信息
     * @param menuId
     * @return
     */
    @Override
    public List<Task> simpleTaskMenu(String menuId) {
        return taskMapper.simpleTaskMenu(menuId);
    }

    /**
     * 查询某个人执行的所有任务
     * @param uId 执行者的id
     * @return
     */
    @Override
    public List<Task> findTaskByExecutor(String uId,String orderType) {
        return taskMapper.findTaskByExecutor(uId,orderType);
    }

    /**
     * 查询该项目下所有未被认领的任务
     * @param projectId 项目id
     * @return
     */
    @Override
    public List<Task> waitClaimTask(String projectId) {
        return taskMapper.waitClaimTask(projectId);
    }

    /**
     * 移除该任务的执行者 改为待认领状态
     * @param taskId 任务的id
     * @return
     */
    @Override
    public Log removeExecutor(String taskId) {
        //拼接日志
        String content = TaskLogFunction.A.getName();
        //保存被移除的参与者的消息信息
        Task task = taskMapper.findTaskByTaskId(taskId);
        //先将任务成员关系表的执行者清掉
        taskMapper.clearExecutor(taskId);
        return logService.saveLog(taskId,content,1);
    }

    /**
     * 查询项目下的指定的优先级的任务
     * @param projectId 项目id
     * @param priority 优先级别
     * @return
     */
    @Override
    public List<Task> findTaskByPriority(String projectId, String priority) {
        return taskMapper.findTaskByPriority(projectId,priority);
    }

    /**
     * 查询该项目下的所有任务
     * @param projectId 项目id
     * @return
     */
    @Override
    public List<Task> findTaskByProject(String projectId) {
        return taskMapper.findTaskByProject(projectId);
    }

    /**
     * 更新任务的执行者
     * @param taskId 该任务的id
     * @param execcutor 执行者id
     * @param uName 用户名
     * @return
     */
    @Override
    public Log updateTaskExecutor(String taskId, String execcutor,String uName) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setExecutor(execcutor);
        taskMapper.updateTask(task);
        StringBuilder content = new StringBuilder();
        content.append(TaskLogFunction.U.getName()).append(" ").append(uName);
        //保存被移除的参与者的消息信息
        userNewsService.saveUserNews(taskMapper.findTaskByTaskId(taskId).getTaskUIds().split(","),taskId,BindingConstants.BINDING_TASK_NAME,TaskLogFunction.A22.getName() + " " + uName,0);
        return logService.saveLog(task.getTaskId(),content.toString(),1);
    }

    /**
     * 查询某个任务下的所有子任务
     * @param taskId 父级任务id
     * @return
     */
    @Override
    public List<Task> findTaskByFatherTask(String taskId) {
        return taskMapper.findTaskByFatherTask(taskId);
    }

    /**
     * 恢复任务
     * @param taskId 任务的id
     * @param menuId 恢复后放到哪个菜单
     */
    @Override
    public void recoveryTask(String taskId, String menuId) {
        taskMapper.recoverTask(taskId,menuId,System.currentTimeMillis());
        Task task = new Task();
        task.setTaskId(taskId);
        logService.saveLog(task.getTaskId(),TaskLogFunction.O.getName(),1);
    }


//    /**
//     * 返回任务日志实体对象
//     */
//    @Override
//    public TaskLogVO saveTaskLog(Task task,String content){
//        TaskLog taskLog = new TaskLog();
//        taskLog.setId(IdGen.uuid());
//        taskLog.setLogType(0);
//        taskLog.setMemberId(ShiroAuthenticationManager.getUserEntity().getId());
//        taskLog.setTaskId(task.getTaskId());
//        taskLog.setContent(taskLog.getMemberName() + "  " + content);
//        taskLog.setCreateTime(System.currentTimeMillis());
//        taskLog.setLogFlag(1);
//        taskLogService.saveTaskLog(taskLog);
//        TaskLogVO taskLogVO = taskLogService.findTaskLogContentById(taskLog.getId());
//        taskLogVO.setTask(task);
//        return taskLogVO;
//    }

    /**
     * 查询此任务的关联
     * @param taskId 任务id
     */
    @Override
    public Map<String, List> findTaskRelation(String taskId) {
        Map<String, List> map = new HashMap<String,List>();
        return map;
    }

    /**
     * 根据任务的id查询该任务的创建人id
     * @param taskId 任务的id
     * @return
     */
    @Override
    public String findTaskMemberIdByTaskId(String taskId) {
        return taskMapper.findTaskMemberIdByTaskId(taskId);
    }

    /**
     * 重新排序该任务菜单的id
     * @param oldMenuTaskId 旧任务菜单的所有任务id
     * @param newMenuTaskId 新任务菜单的所有任务id
     * @param oldMenuId  旧任务菜单的id
     * @param newMenuId 新任务菜单id
     * @param taskId 任务id
     */
    @Override
    public void orderOneTaskMenu(String[]oldMenuTaskId,String[] newMenuTaskId,String oldMenuId,String newMenuId,String taskId) {
        //重新排序任务的顺序 如果拖动的任务不跨越菜单,则不需要更新任务的菜单id
        for (int i = 0; i < oldMenuTaskId.length; i++) {
            taskMapper.orderOneTaskMenu(oldMenuTaskId[i],oldMenuTaskId.length-i);
        }
        //如果旧菜单id 和新菜单id 不相等,说明这次移动跨越了菜单.1 需要编排新菜单的所有任务id 2.需要更新任务的菜单id
        if(!newMenuId.equals(oldMenuId)){
            for (int i = 0; i < newMenuTaskId.length; i++) {
                taskMapper.orderOneTaskMenu(newMenuTaskId[i],newMenuTaskId.length-i);
            }
            //更新任务的菜单id
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskMenuId(newMenuId);
            task.setUpdateTime(System.currentTimeMillis());
            taskMapper.updateTask(task);
        }
    }

    /**
     * 查询出任务的所有标签
     * 1.先拿出该任务绑定的所有标签id
     * 2.把标签id 转换成数组 再根据数据去查询标签
     * @param taskId 任务id
     * @return
     */
    @Override
    public List<Tag> findTaskTag(String taskId) {
        //根据任务的id查询出该任务所绑定的标签id
        String tagId = taskMapper.findTagIdByTaskId(taskId);
        //把任务的id字符串拆成数组形式
        if(StringUtils.isEmpty(tagId)){
                return null;
        }
        String[] split = tagId.split(",");
        Integer[] tags = new Integer[split.length];
        for (int i = 0; i < split.length; i++) {
            tags[i] = Integer.valueOf(split[i]);
        }
        return tagService.findByIds(tags);
    }

    /**
     * 查询出该用户参与的近三天的任务
     * @param userId 用户id
     * @return
     */
    @Override
    public List<Task> findTaskByUserIdAndByTreeDay(String userId) {
        return taskMapper.findTaskByUserIdAndByTreeDay(userId);
    }

    /**
     * 查询出在回收站中的任务
     * @param projectId 项目id
     * @return 该项目下所有在回收站的任务集合
     */
    @Override
    public List<RecycleBinVO> findRecycleBin(String projectId) {
        return taskMapper.findRecycleBin(projectId);
    }

    /**
     * 查询出我创建的任务id
     * @param memberId 创建者的id
     * @return
     */
    @Override
    public List<Task> findTaskByMemberId(String memberId) {
        return taskMapper.findTaskByMemberId(memberId);
    }

    /**
     * 查询出我参与的任务
     * @param id 当前用户id
     * @param orderType 排序类型
     * @return
     */
    @Override
    public List<Task> findTaskByUserId(String id,String orderType) {
        return taskMapper.findTaskByUserId(id,orderType);
    }

    /**
     * 查询出当前用户执行的所有任务信息 并且按照创建时间或者截止时间排序
     * @param id 用户id
     * @param orderType 按照时间排序的类型  (创建时间,截止时间)
     * @return
     */
    @Override
    public List<Task> findTaskByExecutorIdAndTime(String id, String orderType) {
        return null; //taskMapper.findTaskByExecutorIdAndTime(id,orderType);
    }

    /**
     * 查询出该用户执行的 已经完成的任务
     * @param id 用户id
     * @return
     */
    @Override
    public List<Task> findTaskByExecutorAndStatus(String id) {
        return taskMapper.findTaskByExecutorAndStatus(id);
    }

    /**
     * 查询出该用所参与的所有任务(按照任务的状态)
     * @param id 用户id
     * @param status 任务的状态
     * @return
     */
    @Override
    public List<Task> findTaskByUserIdByStatus(String id, String status) {
        return taskMapper.findTaskByUserIdByStatus(id,status);
    }

    /**
     * 查询出该用户所参与的任务 按照时间排序
     * @param id 用户id
     * @param orderType 比较类型
     * @return
     */
    @Override
    public List<Task> findTaskByUserAndTime(String id, String orderType) {
        return taskMapper.findTaskByUserAndTime(id,orderType);
    }

    /**
     * 查询出该用户创建的任务 (根据任务状态查询)
     * @param id 用户id
     * @param status 任务的状态
     * @return
     */
    @Override
    public List<Task> findTaskByCreateMemberByStatus(String id, String status) {
        return taskMapper.findTaskByCreateMemberByStatus(id,status);
    }

    /**
     *查询出我创建的任务 只要未完成
     * @param id 当前用户id
     * @param orderType 排序类型
     * @return
     */
    @Override
    public List<Task> findTaskByCreateMember(String id,String orderType) {
        return taskMapper.findTaskByCreateMember(id,orderType);
    }

    /**
     * 查询出用户创建的所有任务并且按照时间排序
     * @param id 用户id
     * @param orderType 排序类型
     * @return
     */
    @Override
    public List<Task> findTaskByCreateMemberAndTime(String id, String orderType) {
        return taskMapper.findTaskByCreateMemberAndTime(id,orderType);
    }

    /**
     * 查询该用户在日历上创建的所有任务
     * @param uId 用户id
     * @return
     */
    @Override
    public List<Task> findTaskByCalendar(String uId) {
        return taskMapper.findTaskByCalendar(uId);
    }

    /**
     * 根据任务的id查询任务的名称
     * @param taskId 任务id
     * @return 任务的名称
     */
    @Override
    public String getTaskNameById(String taskId) {
        return taskMapper.getTaskNameById(taskId);
    }

    /**
     * 清空任务的标签
     * @param publicId 任务id
     */
    @Override
    public void clearTaskTag(String publicId) {
        taskMapper.clearTaskTag(publicId);
    }

    /**
     * 根据任务的id 查询出该任务的名称
     * @param taskId 任务id
     * @return
     */
    @Override
    public String findTaskNameById(String taskId) {
        return taskMapper.findTaskNameById(taskId);
    }

    /**
     * 取消收藏任务
     * @param taskId 任务id
     */
    @Override
    public void cancleCollectTask(String taskId) {
        taskMapper.cancleCollectTask(taskId,ShiroAuthenticationManager.getUserId());
    }

    /**
     * 查询出该项目下的所有任务 状态数量概览
     * (多次连库查询比筛选集合效率略高 所以 使用多次按照关键字查库的方式 取出数据)
     * @param projectId 项目id
     * @return
     */
    @Override
    public List<Statistics> findTaskCountOverView(String projectId) {
        int total = taskMapper.findTaskTotalByProjectId(projectId);
        int taskCount = 0;
        String[] overViewName = {"未完成","已完成","任务总量","今日到期","已逾期","待认领","按时完成","逾期完成"};
        List<Statistics> list = new ArrayList<Statistics>();
        List<String> chartsList = new ArrayList<String>();

        for (String names : overViewName) {

            Statistics statictics = new Statistics();
            statictics.setName(names);

            //查询出未完成的任务数量
            if(StaticticsVO.HANGINTHEAIR.equals(names)){
                taskCount = taskMapper.findHangInTheAirTaskCount(projectId);


            }

            //查询出已完成的任务
            if(StaticticsVO.COMPLETED.equals(names)){
                taskCount = taskMapper.findCompletedTaskCount(projectId);

            }

            //查询出今日到期的任务
            if(StaticticsVO.MATURINGTODAY.equals(names)){
                taskCount = taskMapper.currDayTaskCount(projectId,System.currentTimeMillis()/1000);
            }

            //查询出已逾期的任务
            if(StaticticsVO.BEOVERDUE.equals(names)){
                taskCount = taskMapper.findBeoberdueTaskCount(projectId,System.currentTimeMillis()/1000);
            }

            //查询出待认领的任务
            if(StaticticsVO.TOBECLAIMED.equals(names)){
                taskCount = taskMapper.findTobeclaimedTaskCount(projectId);
            }

            //查询出按时完成的任务
            if(StaticticsVO.FINISHONTIME.equals(names)){
                taskCount = taskMapper.findFinishontTimeTaskCount(projectId,System.currentTimeMillis()/1000);
            }

            //查询出逾期完成任务
            if(StaticticsVO.OVERDUECOMPLETION.equals(names)){
                taskCount = taskMapper.findOverdueCompletion(projectId,System.currentTimeMillis()/1000);
            }

            //如果非任务总量,执行以下逻辑
            if (!StaticticsVO.TASKTOTALCOUNT.equals(names)){
                //设置百分比
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                //设置该组的达标数量
                statictics.setCount(taskCount);
                try {
                    statictics.setPercentage(Double.valueOf(numberFormat.format((double) taskCount / (double) total * 100)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }else{
                statictics.setCount(total);
                statictics.setPercentage((double)100);
            }
            list.add(statictics);

        }
        return list;
    }

    /**
     * 查询chart饼图数据
     * @param  projectId 项目id
     * @return  list
     */
    @Override
    public List<StringBuilder> findPieChartOverView(String projectId) {
        List<StringBuilder> result=new ArrayList<>();
        //按执行者分布
        List<StatisticsResultVO> taskByExecutor=this.taskMapper.selectTaskByExecutor(projectId);
        StringBuilder builderByExecutor=new StringBuilder();
        result.add(taskStringJoint(taskByExecutor,builderByExecutor)) ;
        //按优先级分布
        List<StatisticsResultVO> taskByPriority=this.taskMapper.selectTaskByPriority(projectId);
        StringBuilder builderByPriority=new StringBuilder();
        result.add(taskStringJoint(taskByPriority,builderByPriority)) ;
        StringBuilder builderByTime=new StringBuilder();
        // 期间已完成项目任务数
        int finishByTime=this.taskMapper.finishByTime(projectId,System.currentTimeMillis()/1000);
        // 期间未完成项目任务数
        int unfinishByTime=this.taskMapper.unfinishBytime(projectId,System.currentTimeMillis()/1000);
        builderByTime.append("[").append(finishByTime).append(",").append(unfinishByTime).append("]");
        result.add(builderByTime);
        return result;
    }

    /**
     * 查询统计页面柱状图数据
     * @param  projectId 项目id
     * @return  list
     */
    @Override
    public List<StringBuilder> findHistogramOverView(String projectId) {
        List<StringBuilder> result=new ArrayList<>();
        long totalMillisSeconds=System.currentTimeMillis();
        long totalSeconds=totalMillisSeconds/1000;
        //期间完成的任务
        List<StatisticsResultVO> taskByFinishTime=this.taskMapper.selectTaskByFinishTime(projectId,totalSeconds);
        StringBuilder builderByFinishTime=new StringBuilder();
        result.add(taskStringJoint(taskByFinishTime,builderByFinishTime)) ;
        //期间未完成的任务
        List<StatisticsResultVO> taskByUnfinishTime=this.taskMapper.taskByUnfinishTime(projectId,totalSeconds);
        StringBuilder builderByUnfinishTime=new StringBuilder();
        result.add(taskStringJoint(taskByUnfinishTime,builderByUnfinishTime)) ;
        //期间逾期的任务
        List<StatisticsResultVO> taskByOverdue=this.taskMapper.taskByOverdue(projectId,totalSeconds);
        StringBuilder builderByOverdue=new StringBuilder();
        result.add(taskStringJoint(taskByOverdue,builderByOverdue)) ;
        //期间更新截止时间的任务
        List<StatisticsResultVO> taskByEndTime=this.taskMapper.taskByEndTime(projectId,totalSeconds);
        StringBuilder builderByEndTime=new StringBuilder();
        result.add(taskStringJoint(taskByEndTime,builderByEndTime)) ;
        //期间高频参与的任务
        List<StatisticsResultVO> taskByLogCount=this.taskMapper.taskByLogCount(projectId,totalSeconds);
        StringBuilder builderByLogCount=new StringBuilder();
        result.add(taskStringJoint(taskByLogCount,builderByLogCount)) ;
        //不同任务分组已完成数据量
        return result;
    }

    @Override
    public List findDoubleHistogramOverView(String projectId) {
        List result=new ArrayList<>();
        //按任务分组分布查询
        List<StatisticsResultVO> taskByTaskGroup=this.taskMapper.taskByTaskGroup(projectId);
        //期间截止任务分成员完成情况
        List<StatisticsResultVO> taskByMember=this.taskMapper.taskByMember(projectId,System.currentTimeMillis()/1000);
        //期间截止任务按截止时间分布
        List<StatisticsResultVO> taskByEndTaskOfEndTime=this.taskMapper.taskByEndTaskOfEndTime(projectId,System.currentTimeMillis()/1000);
        result.add( doubleHistogramData(taskByTaskGroup));
        result.add( doubleHistogramData(taskByMember));
        result.add( doubleHistogramData(taskByEndTaskOfEndTime));
        return result;
    }


   //组装双容器柱状图需要的数据
    private Object doubleHistogramData(List<StatisticsResultVO> taskType) {
        ChartsVO chartsVO = new ChartsVO();
        List finish = new ArrayList<>();
        List unfinish = new ArrayList<>();
        if (taskType != null && taskType.size() > 0) {
            for (StatisticsResultVO svo : taskType) {
                finish.add(svo.getFinishTaskNum());
                unfinish.add(svo.getUnfinishTaskNum());
            }
            chartsVO.setFinishd(finish);
            chartsVO.setUnfinished(unfinish);
        }
        return JSONObject.toJSON(chartsVO);
    }
    /**
     *数据字符串拼接
     */
    private StringBuilder taskStringJoint(List<StatisticsResultVO> taskName,StringBuilder builder){
        if (taskName!=null && taskName.size()>0){
            builder.append("[");
            for (StatisticsResultVO svo:taskName){
                builder.append(svo.getTaskCountInt()).append(",");
            }
            builder.delete(builder.length()-1,builder.length());
            builder.append("]");
            return builder;
        }else{
            return null;
        }
    }

    /**
     * 设置移动任务的信息
     */
    public void updateMoveTaskInfo(Task task, TaskMenuVO oldTaskMenuVO,TaskMenuVO newTaskMenuVO){
        //设置新的项目id
        task.setProjectId(newTaskMenuVO.getProjectId());
        //设置更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //如果复制到其他项目 移除掉 不在 移动到的新项目中的成员信息
        if(!Objects.equals(oldTaskMenuVO.getProjectId(),newTaskMenuVO.getProjectId())){
            List<ProjectMember> byProjectId = projectMemberService.findByProjectId(oldTaskMenuVO.getProjectId());

            //把每个成员的id 取出来 抽成集合 方便比较
            List<String> members = byProjectId.stream().map(ProjectMember::getMemberId).collect(Collectors.toList());

            //判断此任务的执行者 在不在新项目中
            for (String executorId : members) {
                if(executorId.equals(task.getExecutor())){
                    task.setExecutor(task.getExecutor());
                    break;
                } else{
                    task.setExecutor("");
                }
            }

            //将原来的任务的所有成员id 转为集合 方便比较
            List<String> oldTaskMembers = new ArrayList<String>();
            oldTaskMembers = Arrays.asList(task.getTaskUIds().split(","));

            //取出任务成员 和 新项目成员的  交集
            List<String> intersection = oldTaskMembers.stream().filter(item -> members.contains(item)).collect(Collectors.toList());

            task.setTaskUIds(Joiner.on(",").join(intersection));

            //标签设置
            //被复制的任务的标签信息
            List<Tag> tagList = task.getTagList();
            if(tagList != null && tagList.size() > 0 ){
                //复制到新项目里的所有标签
                List<Tag> byProjectIdTag = tagService.findByProjectId(newTaskMenuVO.getProjectId());

                List<Tag> newTagListAfter = new ArrayList<Tag>();
                List<Long> newTagIds = new ArrayList<Long>();

                if(byProjectIdTag != null && byProjectIdTag.size() > 0){
                    boolean flag = false;
                    for(int i = 0;i < tagList.size() ;i++) {
                        for (Tag t : byProjectIdTag) {
                            if (tagList.get(i).getTagName().equals(t.getTagName())) {
                                newTagIds.add(t.getTagId());
                                flag = true;
                            }
                        }
                        if (!flag) {
                            newTagListAfter.add(tagList.get(i));
                        }
                        flag = false;
                    }
                } else{
                    newTagListAfter = tagList;
                }
                for (Tag t : newTagListAfter) {
                    t.setTagId(null);
                    //t.setTaskId(null);
                    t.setMemberId(ShiroAuthenticationManager.getUserId());
                    t.setProjectId(newTaskMenuVO.getProjectId());
                    t.setCreateTime(System.currentTimeMillis());
                    t.setUpdateTime(System.currentTimeMillis());
                }
                if(!newTagListAfter.isEmpty()){
                    tagService.saveMany(newTagListAfter);
                    for (Tag t : newTagListAfter) {
                        newTagIds.add(t.getTagId());
                    }
                }

                //插入新的任务和标签的关系到库中
                List<TagRelation> tagRelations = new ArrayList<TagRelation>();
                for (Long tagId : newTagIds) {
                    TagRelation tagRelation = new TagRelation();
                    tagRelation.setId(IdGen.uuid());
                    tagRelation.setTaskId(task.getTaskId());
                    tagRelation.setTagId(tagId);
                    tagRelations.add(tagRelation);
                }
                tagRelationService.saveManyTagRelation(tagRelations);
            }
        }
        //更新任务信息
        int result = taskMapper.updateTask(task);
    }

    /**
     * 删除一个任务的所有子任务
     * @param taskId 任务id
     */
    @Override
    public void deleteSubTaskByParentId(String taskId) {
        taskMapper.deleteSubTaskByParentId(taskId);
    }

    /**
     * 根据任务的id 查询出该任务的 所有参与者信息
     * @param taskId 任务id
     * @return
     */
    @Override
    public String findUidsByTaskId(String taskId) {
        return taskMapper.findUidsByTaskId(taskId);
    }

    /**
     * 更新任务的开始时间
     * @param taskId 任务id
     * @param startTime 新的开始时间
     */
    @Override
    public void updateStartTime(String taskId, String startTime) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setStartTime(DateUtils.strToLong(startTime));
        taskMapper.updateById(task);
        List<TaskRemindRule> taskRemindRules = taskRemindRuleService.listRuleAndQuartz(taskId);
        taskRemindRules.forEach(item -> {
            String cron = remindCron(taskId, item.getRemindType(), item.getNum(), item.getTimeType(), item.getCustomTime());
            try {
                quartzService.modifyJobTime(item.getQuartzInfo().getJobName(),"task",cron);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * 永久删除多个任务
     * @param taskIds 任务id 的集合
     */
    @Override
    public void deleteManyTask(List<String> taskIds) {
        taskMapper.deleteManyTask(taskIds);
    }

    /**
     * 查询出需要被关联的任务信息
     * @param id 任务id集合
     */
    @Override
    public TaskApiBean findTaskApiBean(String id) {
       return taskMapper.findTaskApiBean(id);
    }

    /**
     * 添加任务的提醒规则
     * @param taskRemindRule 规则实体
     * @param users 提醒的用户id字符串
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addTaskRemind(TaskRemindRule taskRemindRule, String users) throws ServiceException{
        //生成cron 字符串
        String cronStr = remindCron(taskRemindRule.getTaskId(), taskRemindRule.getRemindType(), taskRemindRule.getNum(), taskRemindRule.getTimeType(), taskRemindRule.getCustomTime());

        //存库
        taskRemindRule.setId(IdGen.uuid());
        taskRemindRule.setCronStr(cronStr);
        taskRemindRuleService.save(taskRemindRule);

        //存储quartz的job信息和trigger信息
        QuartzInfo quartzInfo = new QuartzInfo();
        quartzInfo.setId(IdGen.uuid());
        quartzInfo.setJobName(IdGen.uuid());
        quartzInfo.setJobGroup("task");
        quartzInfo.setRemindId(taskRemindRule.getId());
        quartzInfo.setTriggerGroup("task");
        quartzInfoService.save(quartzInfo);

        //封装Myjob实体
        MyJob myJob = new MyJob();
        myJob.setJobName(quartzInfo.getJobName());
        myJob.setJobGroupName(quartzInfo.getJobGroup());
        myJob.setTriggerGroupName("task");
        try {
            //生成cron表达式
            myJob.setCronTime(cronStr);
        } catch (ServiceException e){
           throw new ServiceException(e);
        }
        //额外信息
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("users",users);
        myJob.setJobDataMap(jobDataMap);

        //把任务的提醒规则加入quartz中
        quartzService.addJobByCronTrigger(RemindJob.class,myJob);
    }

    /**
     * 更新任务的提醒规则
     * @param taskRemindRule 提醒规则实体信息
     */
    @Override
    public void updateTaskRemind(TaskRemindRule taskRemindRule) throws SchedulerException {
        String cronStr = remindCron(taskRemindRule.getTaskId(), taskRemindRule.getRemindType(), taskRemindRule.getNum(), taskRemindRule.getTimeType(), taskRemindRule.getCustomTime());
        taskRemindRule.setCronStr(cronStr);
        taskRemindRuleService.update(taskRemindRule,new QueryWrapper<TaskRemindRule>().eq("id",taskRemindRule.getId()));
        QuartzInfo quartzInfo = quartzInfoService.getOne(new QueryWrapper<QuartzInfo>().eq("remind_id", taskRemindRule.getId()));
        //更新quartz定时任务
        quartzService.modifyJobTime(quartzInfo.getJobName(),quartzInfo.getTriggerGroup(),cronStr);
    }

    /**
     * 移除这条任务的提醒规则 并且从quartz移除
     * @param id 规则的id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeRemind(String id) throws SchedulerException {
        QuartzInfo quartzInfo = quartzInfoService.getOne(new QueryWrapper<QuartzInfo>().eq("remind_id", id));
        //从quartz 中移除该任务
        quartzService.removeJob(quartzService.getScheduler(),new TriggerKey(quartzInfo.getJobName(),"task"), new JobKey(quartzInfo.getJobName(),"task"));
        //移除掉该条规则
        taskRemindRuleService.removeById(id);
        //移除quartz信息
        quartzInfoService.removeById(quartzInfo.getId());
    }

    /**
     * 更新任务要提醒的成员信息
     * @param taskId 任务id
     * @param users 成员id 信息
     */
    @Override
    public void updateRemindUsers(String taskId, String users){
        List<TaskRemindRule> taskRemindRules = taskRemindRuleService.listRuleAndQuartz(taskId);
        taskRemindRules.forEach(item -> {
            QuartzInfo quartzInfo = item.getQuartzInfo();
            MyJob myJob = new MyJob();
            myJob.setJobName(quartzInfo.getJobName());
            myJob.setJobGroupName(quartzInfo.getJobGroup());
            myJob.setCronTime(item.getCronStr());
            myJob.setTriggerGroupName(quartzInfo.getTriggerGroup());
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("users",users);
            myJob.setJobDataMap(jobDataMap);
            quartzService.modifyJobDateMap(RemindJob.class, myJob);
        });
    }

    /**
     * 完成任务
     * @param taskId 任务id
     */
    @Override
    public Task completeTask(String taskId) {
        Task task = taskMapper.selectById(taskId);
        Long startTime = task.getStartTime();
        Long endTime = task.getEndTime();
        Long newStartTime = null;
        Long newEndTime = null;
        if(TaskStatusConstant.DAY_REPEAT.equals(task.getRepeat())){
            //每天重复后的时间
            if(startTime != null){
                newStartTime = afterDaysTime(startTime, 1);
            }
            if(endTime != null){
                newEndTime = afterDaysTime(endTime, 1);
            }
        } else if (TaskStatusConstant.WEEK_REPEAT.equals(task.getRepeat())){
            //每周重复后的时间
            if(startTime != null){
                newStartTime = afterDaysTime(startTime,7);
            }
            if(endTime != null){
                newEndTime = afterDaysTime(endTime,7);
            }
        } else if (TaskStatusConstant.MONTH_REPEAT.equals(task.getRepeat())){
            //每月重复后的时间
            if(startTime != null){
                newStartTime = afterMonthTime(startTime);
            }
            if(endTime != null){
                newEndTime = afterMonthTime(endTime);
            }
        } else if (TaskStatusConstant.YEAR_REPEAT.equals(task.getRepeat())){
            //每年重复后的时间
            if(startTime != null){
                newStartTime = DateUtils.afterYearTime(1,new Date(startTime));
            }
            if(endTime != null){
                newEndTime = DateUtils.afterYearTime(1,new Date(endTime));
            }
        } else if (TaskStatusConstant.WORKING_DAY_REPEAT.equals(task.getRepeat())){
            //工作日重复后的时间
            if(startTime != null){
                newStartTime = DateUtils.afterWorkDay(new Date(startTime));
            }
            if(endTime != null){
                newEndTime = DateUtils.afterWorkDay(new Date(startTime));
            }
        } else {
            return null;
        }
        //更新任务id  以及时间信息
        task.setTaskId(IdGen.uuid());
        task.setStartTime(newStartTime);
        task.setEndTime(newEndTime);
        //存库
        taskMapper.saveTask(task);
        return task;
    }

    /**
     * 生成任务提醒的规则
     * @param taskId 任务id
     * @param remindType 提醒类型
     * @param num 数量
     * @param timeType 时间类型
     * @param customTime 自定义时间的字符串
     * @return cron 表达式
     */
    @Override
    public String remindCron(String taskId,String remindType, Integer num, String timeType, String customTime) throws ServiceException{
        SimpleDateFormat format = new SimpleDateFormat("ss mm HH dd MM ? yyyy");
        //查询出该任务的开始时间
        Task task = taskMapper.selectOne(new QueryWrapper<Task>().eq("task_id", taskId).select("start_time startTime","end_time endTime"));
        //生成自定义时间表达式
        if(!StringUtils.isEmpty(customTime)){
            return DateUtils.cronStr(DateUtils.parse(customTime,"yyyy-MM-dd HH:mm:ss"));
        }
        if(task == null){
            throw new ServiceException("该任务时间不合则规则设定,或者没有时间!");
        }
        //任务开始时
        if(TaskStatusConstant.BEGIN.equals(remindType)){
            if(task.getStartTime() == null){
                throw new ServiceException("该任务没有开始时间!");
            }
            return DateUtils.cronStr(new Date(task.getStartTime()));
        }
        //任务截止时
        if(TaskStatusConstant.END.equals(remindType)){
            if(task.getEndTime() == null){
                throw new ServiceException("该任务没有结束时间!");
            }
            return DateUtils.cronStr(new Date(task.getEndTime()));
        }
        Long day = 86400000L * num;
        Long hour = 3600000L * num;
        Long minute = 60000L * num;
        //算出各单位的毫秒数
        Long times = 0L;
        if(TaskStatusConstant.DAY.equals(timeType)){
            times = day;
        }
        if(TaskStatusConstant.HOUR.equals(timeType)){
            times = hour;
        }
        if(TaskStatusConstant.MINUTE.equals(timeType)){
            times = minute;
        }
        if(task.getStartTime() != null){
            //任务开始前
            if(TaskStatusConstant.BEGIN_BEORE.equals(remindType)){
                task.setStartTime(task.getStartTime() - times);
            }
            //任务开始后
            if(TaskStatusConstant.BEGIN_AFTER.equals(remindType)){
                task.setStartTime(task.getStartTime() + times);
            }
            return DateUtils.cronStr(new Date(task.getStartTime()));
        }
        if(task.getEndTime() != null){
            Long end = 0L;
            //任务截止后
            if(TaskStatusConstant.END_AFTER.equals(remindType)){
                end = task.getEndTime() + times;
            }
            //任务截止前
            if(TaskStatusConstant.END_BEFORE.equals(remindType)){
                end = task.getEndTime() - times;
            }
            return DateUtils.cronStr(new Date(end));
        }
        return null;
    }

    /**
     * 获取给定时间指定天数后的时间戳
     * @param time 时间毫秒数
     * @param days 天数
     */
    public long afterDaysTime(Long time, int days){
        return DateUtils.strToLong(DateUtils.getAfterDay(DateUtils.getDateStr(new Date(time), "yyyy-MM-dd HH:mm:ss"), days, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取给定时间的下个月的时间戳
     * @param time 给定的时间
     * @return
     */
    public long afterMonthTime(Long time){
        return DateUtils.strToLong(DateUtils.getMonth(DateUtils.getDateStr(new Date(time), "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss", 1));
    }
}

