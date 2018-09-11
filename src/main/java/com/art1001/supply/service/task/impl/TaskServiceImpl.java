package com.art1001.supply.service.task.impl;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.art1001.supply.base.Base;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.entity.fabulous.Fabulous;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.statistics.StaticticsVO;
import com.art1001.supply.entity.statistics.Statistics;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.tagrelation.TagRelation;
import com.art1001.supply.entity.task.*;
import com.art1001.supply.entity.template.TemplateData;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.fabulous.FabulousMapper;
import com.art1001.supply.mapper.task.*;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.art1001.supply.service.task.TaskLogService;
import com.art1001.supply.service.task.TaskMemberService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserNewsService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import com.google.common.base.Joiner;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * taskServiceImpl
 */
@Service
public class TaskServiceImpl implements TaskService {

    private String isparent = "0";
    /** taskMapper接口*/
	@Resource
	private TaskMapper taskMapper;

	/** userMapper接口 */
	@Resource
    private UserMapper userMapper;

	/** taskMemberService 接口*/
	@Resource
    private TaskMemberService taskMemberService;

	/** TaskLogService接口 */
	@Resource
    private TaskLogService taskLogService;

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

    /** 用于订阅推送消息 */
    @Resource
    private SimpMessagingTemplate messagingTemplate;

    /** 标签标签的逻辑层接口 */
    @Resource
    private TagRelationService tagRelationService;

    /** 公共封装的方法 */
    @Resource
    private Base base;

	/**
	 * 重写方法
	 * 查询分页task数据
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Task> findTaskPagerList(Pager pager){
		return taskMapper.findTaskPagerList(pager);
	}

	/**
	 * 重写方法
	 * 通过taskId获取单条task数据
	 * @param taskId
	 * @return
	 */
	@Override
	public Task findTaskByTaskId(String taskId){
		return taskMapper.findTaskByTaskId(taskId);
	}

	/**
	 * 重写方法
	 * 修改task数据
	 * @param task 任务信息
	 */
	@Override
	public Log updateTask(Task task){
	    String content = "";
	    Log log = new Log();
	    //任务更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //更新任务优先级
        if(StringUtils.isNotEmpty(task.getPriority())){
            log = logService.saveLog(task.getTaskId(),TaskLogFunction.F.getName() + " " + task.getPriority(),1);
        }
        //更新任务备注
        if(task.getRemarks() != null){
            if(!"".equals(task.getRemarks())){
                log = logService.saveLog(task.getTaskId(),TaskLogFunction.E.getName(),1);
            } else{
                log = logService.saveLog(task.getTaskId(),TaskLogFunction.A26.getName(),1);
            }
        }
        //更新任务执行者
        if(StringUtils.isNotEmpty(task.getExecutor())){
            UserEntity user = userMapper.findUserById(task.getExecutor());
            log = logService.saveLog(task.getTaskId(),TaskLogFunction.U.getName() + " " + user.getUserName(),1);
        }
        //更新任务其他
        if(StringUtils.isNotEmpty(task.getOther())){
            log = logService.saveLog(task.getTaskId(),TaskLogFunction.G.getName() + " " + task.getOther(),1);
        }
        //更新任务的名称
        if(StringUtils.isNotEmpty(task.getTaskName())){
            log = logService.saveLog(task.getTaskId(),TaskLogFunction.A18.getName() + " " + task.getTaskName(),1);
        }
        int result = taskMapper.updateTask(task);
        log.setResult(result);
        return log;
	}

	/**
	 * 重写方法
	 * 将添加的任务信息保存至数据库
     * @param task task信息
     */
	@Override
	public void saveTask(Task task) {
        task.setTaskId(IdGen.uuid());
	    //获取当前登录用户的id
        String id = ShiroAuthenticationManager.getUserEntity().getId();
        //设置任务的创建者
        task.setMemberId(id);
        //设置该任务的创建时间
        task.setCreateTime(System.currentTimeMillis());
        //设置该任务的最后更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //根据查询菜单id 查询 菜单id 下的 最大排序号
        Integer maxOrder = relationService.findMenuTaskMaxOrder(task.getTaskMenuId());
        task.setOrder(++maxOrder);
        //保存任务信息
        taskMapper.saveTask(task);
        //保存任务和标签的关联关系
        if(StringUtils.isNotEmpty(task.getTagId())){
            List<String> tagList = Arrays.asList(task.getTagId().split(","));
            tagList.forEach(tagId->{
                TagRelation tagRelation = new TagRelation();
                tagRelation.setTagId(Long.valueOf(tagId));
                tagRelation.setTaskId(task.getTaskId());
                tagRelation.setId(IdGen.uuid());
                tagRelationService.saveTagRelation(tagRelation);
            });
        }
        //拿到TaskLog对象并且保存
        logService.saveLog(task.getTaskId(), TaskLogFunction.R.getName() + task.getTaskName(),1);
    }

    @Override
    public void saveTaskBatch(String projectId, String menuId, List<TemplateData> templateDataList) {
        String id = ShiroAuthenticationManager.getUserEntity().getId();
        taskMapper.saveTaskBatch(projectId,menuId,templateDataList,id);
    }

    /**
	 * 重写方法
	 * 获取所有task数据
	 * @return
	 */
	@Override
	public List<Task> findTaskAllList(){
		return taskMapper.findTaskAllList();
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
        TaskLogVO taskLogVO = new TaskLogVO();
        //任务状态为0 日志打印内容为 xxx把任务移入了回收站
        Log log = logService.saveLog(taskId,TaskLogFunction.P.getName(),1);
        log.setResult(result);
        return log;
    }

	/**
	 * 重写方法
	 * 修改当前任务状态
	 * @param task 任务信息
	 * @return
	 */
	@Override
	public Log resetAndCompleteTask(Task task) {
	    StringBuilder content = new StringBuilder("");
        //如果当前状态为未完成  则 日志记录为 完成任务 否则为 重做任务
        if(task.getTaskStatus().equals("完成")){
            content.append(TaskLogFunction.Q.getName()).append(" ").append("\"").append(task.getTaskName()).append("\"");
        }
        if(task.getTaskStatus().equals("未完成")){
            content.append(TaskLogFunction.S.getName()).append(" ").append("\"").append(task.getTaskName()).append("\"");
        }
        if(task.getTaskStatus().equals("重新开始")){
            content.append(TaskLogFunction.S.getName()).append(" ").append("\"").append(task.getTaskName()).append("\"");
        }
        //查询出该父级任务下的所有子级任务
        List<Task> subLevelList = taskMapper.findSubLevelTask(task.getTaskId());
        if(subLevelList != null && subLevelList.size() > 0){
            //如果所有的子级任务里有未完成的则抛出异常
            for (Task t : subLevelList) {
                if(t.getTaskStatus().equals("未完成") || t.getTaskStatus().equals("重新开始")){
                    throw new ServiceException("必须完成子级任务,才能完成父级任务!");
                }
            }
        }
        if(task.getTaskStatus().equals("完成")){
            task.setTaskStatus("未完成");
        } else if (task.getTaskStatus().equals("未完成")){
            task.setTaskStatus("完成");
        } else if (task.getTaskStatus().equals("重新开始")){
            task.setTaskStatus("完成");
        }
        //修改任务状态
        int result = taskMapper.changeTaskStatus(task.getTaskId(),task.getTaskStatus(),System.currentTimeMillis());
        Log log = logService.saveLog(task.getTaskId(), content.toString(),1);
        log.setResult(result);
        return log;
    }

    /**
     * 重写方法
     * 设定任务的时间(开始 / 结束)
     * @param task 任务时间的信息
     * @return
     */
    @Override
    public Log updateTaskStartAndEndTime(Task task) {
        String content = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if(task.getStartTime() != null){
            Date date = new Date(task.getStartTime());
            content = TaskLogFunction.M.getName()+ " " + format.format(date);
        }
        if(task.getEndTime() != null){
            Date date = new Date(task.getEndTime());
            content = TaskLogFunction.L.getName() + " " + format.format(date);
        }
        int result =  taskMapper.updateTask(task);
        Log log = logService.saveLog(task.getTaskId(), content,1);
        log.setResult(result);
        return log;
    }

    /**
     * 判断当前菜单有没有任务
     * @param taskMenuId 菜单id
     * @return
     */
    @Override
    public int findTaskByMenuId(String taskMenuId) {
        return taskMapper.findTaskByMenuId(taskMenuId);
    }

    /**
     * 移动任务至 ( 项目、分组、菜单 )
     * @param taskId 任务id
     * @param projectId 项目id
     * @param menuId 菜单id
     * @return
     */
    @Override
    public Log mobileTask(String taskId, String projectId, String menuId) {
        Task task = new Task();
        task.setProjectId(projectId);
        task.setTaskMenuId(menuId);
        task.setUpdateTime(System.currentTimeMillis());
        taskMapper.updateTask(task);

        //taskMapper

        String content = "";
        return null;
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
        log.setResult(result);
        return log;
    }

    /**
     * 绑定标签到当前任务
     * @param tag 标签的实体信息
     * @param taskId 当前任务的id
     * @param countByTagName 判断要绑定到任务上的标签是不是已经存在
     * @return
     */
    @Override
    public Log addTaskTags(Tag tag,String taskId,int countByTagName) {
        //先查询出当前任务原有的标签id信息
        String taskTag = taskMapper.findTaskTagByTaskId(taskId);
        if(taskTag == null){
            taskTag = "";
        }
        //将原有标签id和新添加的标签id拼接在一起存入数据库
        StringBuilder newTaskTag = new StringBuilder();
        newTaskTag.append(taskTag).append(tag.getTagId()).append(",");
        Task task = new Task();
        task.setTaskId(taskId);
        //设置最后更新时间
        task.setUpdateTime(System.currentTimeMillis());
        task.setTagId(newTaskTag.toString());
        //更新到数据库
        int result = taskMapper.updateTask(task);
        Log log = new Log();
        //判断 如果是向数据库新插入了标签 则保存日志 否则不保存
        if(countByTagName == 0){
            //拼接任务操作日志内容
            StringBuilder content = new StringBuilder("");
            content.append(TaskLogFunction.A10.getName()).append(" ").append(tag.getTagName());
            log = logService.saveLog(task.getTaskId(), content.toString(),1);
            log.setResult(result);
            return log;
        }
        return log;
    }

    /**
     * 移除该任务上的标签
     * @param taskId 当前任务uid
     * @return
     */
    @Override
    public void removeTaskTag(String tagId, String taskId) {
        Task task = taskMapper.findTaskByTaskId(taskId);
        String tagIds = task.getTagId();
        List<String> tagIdTemp = Arrays.stream(tagIds.split(",")).filter(tagId1->!tagId1.equals(tagId)).collect(Collectors.toList());
        if(tagIdTemp.size()==0){
            taskMapper.clearTaskTag(taskId);
        }else{
            task.setTagId(StringUtils.join(tagIdTemp,",")+",");
            task.setUpdateTime(System.currentTimeMillis());
            taskMapper.updateTask(task);
        }
    }

    /**
     * 更新任务的重复规则
     * @param task 任务的实体信息
     * @param object 时间重复周期的具体信息 (未设定)
     * @return
     */
    @Override
    public Log updateTaskRepeat(Task task, Object object) {
        //判断是不是自定义重复
        if(!task.getRepeat().equals("自定义重复")){
            //如果不是自定义重复删除该任务的自定义重复时间
        }
        task.setUpdateTime(System.currentTimeMillis());
        int result = taskMapper.updateTask(task);
        String content = TaskLogFunction.D.getName();
        Log log = logService.saveLog(task.getTaskId(), content,1);
        log.setResult(result);
        return log;
    }

    /**
     * 更新任务的提醒时间
     * @param task 任务实体信息
     * @param userEntity 用户实体信息
     * @return
     */
    @Override
    public Log updateTaskRemindTime(Task task, UserEntity userEntity) {
        StringBuilder content = new StringBuilder("");
        //判断是开始时提醒还是结束时提醒
        if(task.getRemind().equals("任务截止时提醒")){
           task.setRepetitionTime(task.getEndTime());
        }
        if(task.getRemind().equals("任务开始时提醒")){
            task.setRepetitionTime(task.getStartTime());
        }
        if(task.getRemind().equals("不提醒")){
            task.setRepetitionTime(0L);
        }
        task.setUpdateTime(System.currentTimeMillis());
        content.append(TaskLogFunction.A13.getName()).append(" ").append(task.getRemind());
        int result = taskMapper.updateTask(task);
        Log log = logService.saveLog(task.getTaskId(), content.toString(),1);
        log.setResult(result);
        return log;
        //UserEntity是要被提醒的成员信息(暂时先不用)
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
        log.setResult(result);
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
        log.setResult(result);
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
        int result = fabulousMapper.addFabulous(fabulous);
        //更新任务得赞数量
        task.setFabulousCount(taskFabulous + 1);
        return taskMapper.updateTask(task);
    }

    /**
     * 判断当前用户有没有给该任务点赞
     * @param taskId 任务id信息
     * @return
     */
    @Override
    public boolean judgeFabulous(String taskId) {
        String memberId = ShiroAuthenticationManager.getUserEntity().getId();
        int result = fabulousMapper.judgeFabulous(taskId,memberId);
        //如果已经给该任务点赞 返回 false 否则返回true
        if(result > 0){
            return false;
        } else{
            return true;
        }
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
        String memberId = ShiroAuthenticationManager.getUserEntity().getId();
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
        String id = ShiroAuthenticationManager.getUserEntity().getId();
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
        log.setResult(result);
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
        log.setResult(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","子任务完成/重做");
        jsonObject.put("taskId",task.getTaskId());
        jsonObject.put("result",task.getTaskStatus());
        messagingTemplate.convertAndSend("/topic/"+parentTask.getTaskId(),new ServerMessage(JSON.toJSONString(jsonObject)));
        return log;
    }

    /**
     * 复制任务
     * @param taskId 当前任务信息
     * @param projectId 当前任务所在的项目id
     * @return
     */
    @Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
    @Override
    public String copyTask(String taskId, String projectId, String menuId) {
        Log log = new Log();
        List<Task> tasks = new ArrayList<Task>();
        Task oldTask = taskMapper.findTaskByTaskId(taskId);
        try {
            //设置新的id
            oldTask.setTaskId(IdGen.uuid());
            //更新新任务的创建时间
            oldTask.setCreateTime(System.currentTimeMillis());
            //设置新任务的更新时间
            oldTask.setUpdateTime(System.currentTimeMillis());
            oldTask.setProjectId(projectId);
            oldTask.setTaskMenuId(menuId);
            tasks.add(oldTask);
            //根据被复制任务的id 取出该任务所有的子任务
            List<Task> subLevelTaskList = taskMapper.findSubLevelTask(taskId);
            if(subLevelTaskList != null && subLevelTaskList.size() > 0) {
                for (Task task : subLevelTaskList) {
                    //设置新的子任务id
                    task.setTaskId(IdGen.uuid());
                    task.setProjectId(projectId);
                    //设置新的子任务的父任务id
                    task.setParentId(oldTask.getTaskId());
                    //设置新子任务的更新时间
                    task.setUpdateTime(System.currentTimeMillis());
                    //设置新子任务的创建时间
                    task.setCreateTime(System.currentTimeMillis());
                    tasks.add(task);
                }
            }

            //插入数据
            for (Task task : tasks) {
                taskMapper.saveTask(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type","创建了任务");
        jsonObject.put("task",taskMapper.findTaskByTaskId(oldTask.getTaskId()));
        messagingTemplate.convertAndSend("/topic/"+ projectId, new ServerMessage(JSON.toJSONString(jsonObject, SerializerFeature.DisableCircularReferenceDetect)));
        return oldTask.getTaskId();
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
        publicCollect.setId(IdGen.uuid());
        publicCollect.setMemberId(ShiroAuthenticationManager.getUserEntity().getId());
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
     * 判断当前用户有没有收藏当前任务
     * @param task 当前用户的信息
     * @return
     */
    @Override
    public boolean judgeCollectTask(Task task) {
        //如果收藏了任务返回false 否则返回true
        int result = publicCollectService.judgeCollectPublic(ShiroAuthenticationManager.getUserId(),task.getTaskId(),BindingConstants.BINDING_TASK_NAME);
        if(result > 0){
            return false;
        } else{
            return true;
        }
    }

    /**
     * 取消收藏的任务
     * @param task 任务的信息
     * @return
     */
    @Override
    public int cancelCollectTask(Task task) {
        String memberId = ShiroAuthenticationManager.getUserEntity().getId();
        int result = publicCollectService.deletePublicCollectById(memberId,task.getTaskId());
        return result;
    }

    /**
     * 更改当前任务的隐私模式
     * @param task 任务的实体信息
     * @return
     */
    @Override
    public int settingUpPrivacyPatterns(Task task) {
        return taskMapper.settingUpPrivacyPatterns(task);
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
    public List<Task> taskMenu(String menuId) {
        return taskMapper.taskMenu(menuId);
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
        userNewsService.saveUserNews(task.getTaskUIds().split(","),taskId,BindingConstants.BINDING_TASK_NAME,TaskLogFunction.A.getName() + " " + task.getExecutorInfo().getUserName(),0);
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
        List<Task> taskRelationTask = taskMemberService.findTaskRelationTask(taskId);
        List<File> taskRelationFile = taskMemberService.taskRelationFile(taskId);
        map.put("relationTask",taskRelationTask);
        map.put("relationFile",taskRelationFile);
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
     * 根据任务的菜单信息 查询出任务分组信息
     * @param menuParent 根据任务的菜单信息 查询出任务分组信息
     * @return 任务的实体
     */
    @Override
    public Relation findTaskGroupInfoByTaskMenuId(String menuParent) {
        return taskMapper.findTaskGroupInfoByTaskMenuId(menuParent);
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
                taskCount = taskMapper.currDayTaskCount(projectId,System.currentTimeMillis());
            }

            //查询出已逾期的任务
            if(StaticticsVO.BEOVERDUE.equals(names)){
                taskCount = taskMapper.findBeoberdueTaskCount(projectId,System.currentTimeMillis());
            }

            //查询出待认领的任务
            if(StaticticsVO.TOBECLAIMED.equals(names)){
                taskCount = taskMapper.findTobeclaimedTaskCount(projectId);
            }

            //查询出按时完成的任务
            if(StaticticsVO.FINISHONTIME.equals(names)){
                taskCount = taskMapper.findFinishontTimeTaskCount(projectId,System.currentTimeMillis());
            }

            //查询出逾期完成任务
            if(StaticticsVO.OVERDUECOMPLETION.equals(names)){
                taskCount = taskMapper.findOverdueCompletion(projectId,System.currentTimeMillis());
            }

            //设置该组的达标数量
            statictics.setCount(taskCount);
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            //设置百分比
            statictics.setPercentage(Double.valueOf(numberFormat.format((float)taskCount / (float)total * 100)));
            list.add(statictics);
        }
        return list;
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
     * 永久删除任务
     * 步骤1 : 删除此任务的所有关联信息
     * 步骤2 : 删除此任务的所有日志记录
     * 步骤3 : 删除此任务的所有评论记录
     * 步骤4 : 删除此任务的所有得赞记录
     * 步骤5 : 删除此任务的所有收藏记录
     * 步骤6 : 删除此任务的所有标签记录
     * 步骤7 : 删除和此任务相关的用户消息信息
     * 步骤8 : 删除此任务上传的文件信息
     * 步骤9 : 删除此任务的子任务
     * @param taskId 任务id
     */
    @Override
    public void deleteTask(String taskId) {
        base.deleteItemOther(taskId,BindingConstants.BINDING_TASK_NAME);
        taskMapper.deleteSubTaskByParentId(taskId);
        taskMapper.deleteTask(taskId);
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
     * 永久删除多个任务
     * @param taskIds 任务id 的集合
     */
    @Override
    public void deleteManyTask(List<String> taskIds) {
        taskMapper.deleteManyTask(taskIds);
    }
}
