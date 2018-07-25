package com.art1001.supply.service.task.impl;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.Resource;

import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.*;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserInfoEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.task.*;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskLogService;
import com.art1001.supply.service.task.TaskMemberService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * taskServiceImpl
 */
@Service
public class TaskServiceImpl implements TaskService {

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

    /** 日志逻辑层接口 */
    @Resource
    private LogService logService;

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
	 * 删除任务
	 * 通过taskId删除task数据
     * @param taskId 任务id
     */
	@Override
	public int deleteTaskByTaskId(String taskId){
	    //删除任务-成员-文件的关联信息
        taskMemberService.clearTaskMemberByTaskId(taskId);
        //删除任务信息
	    return taskMapper.deleteTaskByTaskId(taskId);
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
        if(task.getPriority() != null && task.getPriority() != ""){
            log = logService.saveLog(task.getTaskId(),TaskLogFunction.F.getName() + " " + task.getPriority(),1);
        }
        //更新任务备注
        if(task.getRemarks() != null && task.getRemarks() != null){
            log = logService.saveLog(task.getTaskId(),TaskLogFunction.E.getName() + " " + task.getPriority(),1);
        }
        //更新任务执行者
        if(task.getExecutor() != null && task.getExecutor() != ""){
            log = logService.saveLog(task.getTaskId(),TaskLogFunction.U.getName() + " " + task.getPriority(),1);
        }
        //更新任务其他
        if(task.getOther() != null && task.getOther() != ""){
            log = logService.saveLog(task.getTaskId(),TaskLogFunction.G.getName() + " " + task.getPriority(),1);
        }
        //更新任务的名称
        if(!StringUtils.isEmpty(task.getTaskName())){
            log = logService.saveLog(task.getTaskId(),TaskLogFunction.A18.getName() + " " + task.getPriority(),1);
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
	public Log saveTask(Task task) {
        task.setTaskId(IdGen.uuid());
	    //获取当前登录用户的id
        String id = ShiroAuthenticationManager.getUserEntity().getId();
        //初始创建任务设置为父任务
        task.setParentId("0");
        //设置任务的创建者
        task.setMemberId(id);

        //设置该任务的初始状态
        task.setTaskStatus("未完成");
        //设置该任务是否删除 0 未删除 1 已删除
        task.setTaskDel(0);
        //设置任务的初始得赞数
        task.setFabulousCount(0);
        //设置该任务的创建时间
        task.setCreateTime(System.currentTimeMillis());
        //设置该任务的最后更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //根据查询菜单id 查询 菜单id 下的 最大排序号
        Integer maxOrder = relationService.findMenuTaskMaxOrder(task.getTaskMenuId());
        task.setOrder(++maxOrder);
        //保存任务信息
        taskMapper.saveTask(task);
        //拿到TaskLog对象并且保存
        return logService.saveLog(task.getTaskId(), TaskLogFunction.R.getName() + task.getTaskName(),1);
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
        Log log = logService.saveLog(task.getTaskId(),TaskLogFunction.P.getName(),1);
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
     * @param task 任务的信息
     * @param oldTaskMenuVO 移动前该任务的位置信息
     * @param newTaskMenuVO 将要移动该任务到的位置信息
     * @return
     */
    @Override
    public Log mobileTask(Task task, TaskMenuVO oldTaskMenuVO,TaskMenuVO newTaskMenuVO) {
        //如果是跨项目移动 则清空任务成员关系 及 执行者
        if(!newTaskMenuVO.getProjectId().equals(oldTaskMenuVO.getProjectId())){
            task.setExecutor("");
            taskMapper.clearTaskMember(task.getTaskId());
        }
        //设置新的项目id
        task.setProjectId(newTaskMenuVO.getProjectId());
        //设置更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //更新任务信息
        int result = taskMapper.updateTask(task);
        String content = "";
        //如果项目id不为空,说明该任务要移至其他项目,所以项目id,分组id,菜单id,肯定都不为空
        //或者如果分组id不为空说明该任务要移至其他分组,所以 分组id,菜单id,肯定不为空
        if((newTaskMenuVO.getProjectId() != null && newTaskMenuVO.getProjectId() != "") || (newTaskMenuVO.getTaskGroupId() != null && newTaskMenuVO.getTaskGroupId() != "")){
            //拼接任务操作日志内容的字符串
            content = TaskLogFunction.V.getName() + " " + oldTaskMenuVO.getTaskGroupName() + "/" + oldTaskMenuVO.getTaskMenuName() +" "+  TaskLogFunction.W.getName() + " " + newTaskMenuVO.getTaskGroupName() + "/" + newTaskMenuVO.getTaskMenuName();
            //保存日志信息
            Log log = logService.saveLog(task.getTaskId(), content,1);
            log.setResult(result);
            return log;
        }
        //如果任务的菜单信息不为空 说明该任务要移至其他的任务菜单
        if(newTaskMenuVO.getTaskMenuId() != null && newTaskMenuVO.getTaskMenuId() != ""){
            //拼接任务操作日志内容的字符串
            content = TaskLogFunction.X.getName() + " " + newTaskMenuVO.getTaskMenuName();
            //保存日志信息
            Log log = logService.saveLog(task.getTaskId(), content,1);
            log.setResult(result);
            return log;
        }
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
     * @param tags 当前任务上绑定的所有标签对象数组
     * @param tag 当前要被的标签对象
     * @param taskId 当前任务uid
     * @return
     */
    @Override
    public int removeTaskTag(String[] tags, Tag tag, String taskId) {
        if(tags.length == 1){
            int result = taskMapper.clearTaskTag(taskId);
            return result;
        }
        StringBuilder taskTagsId = new StringBuilder();
        //循环标签数组
        for (int i = 0; i < tags.length ; i++) {
            //如果循环到的标签和要被删除的标签的信息一致时 清空该对象
            if(tags[i].equals(String.valueOf(tag.getTagId()))){
                tags[i] = null;
                continue;
            }
            //累加标签的id
            taskTagsId.append(tags[i]).append(",");
        }
        Task task = new Task();
        //设置删除后的标签id
        task.setTagId(taskTagsId.toString());
        //设置任务id
        task.setTaskId(taskId);
        //设置更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //保存至数据库
        int result = taskMapper.updateTask(task);
        return result;
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
        StringBuilder content = new StringBuilder();
        Task task = taskMapper.findTaskByTaskId(taskId);

        List<String> list1 = Arrays.asList(task.getTaskUIds().split(","));
        List<String> list2 = Arrays.asList(memberIds.split(","));

        List subtract1 = ListUtils.subtract(list1, list2);
        if(subtract1 != null&& subtract1.size() > 0){
            content.append(TaskLogFunction.B.getName());
            for (Object aSubtract1 : subtract1) {
                UserEntity user = userMapper.findUserById(aSubtract1.toString());
                content.append(user.getUserName()).append(",");
            }
        }


        List subtract2 = ListUtils.subtract(list2, list1);
        if(subtract2 != null && subtract2.size() > 0){
            content.append(TaskLogFunction.C.getName());
            for (Object aSubtract2 : subtract2) {
                UserEntity user = userMapper.findUserById(aSubtract2.toString());
                content.append(user.getUserName()).append(",");
            }
        }
        task.setTaskUIds(memberIds);
        taskMapper.updateTask(task);
        Log log = logService.saveLog(taskId,content.deleteCharAt(content.length()-1).toString(),1);
        return log;
    }


    /**
     * 移除任务-成员关系
     * @param task 当前项目实体信息
     * @param userEntity 被移除的用户的信息
     * @return
     */
    @Override
    public Log removeTaskMember(Task task, UserEntity userEntity) {
        taskMemberService.removeTaskMember(task,userEntity);
        StringBuilder builder = new StringBuilder("");
        builder.append(TaskLogFunction.B.getName()).append(" ").append(userEntity.getUserName());
        return logService.saveLog(task.getTaskId(),builder.toString(),1);
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
        fabulous.setTaskId(task.getTaskId());
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
     * @param task 任务信息
     * @return
     */
    @Override
    public boolean judgeFabulous(Task task) {
        String memberId = ShiroAuthenticationManager.getUserEntity().getId();
        int result = fabulousMapper.judgeFabulous(task.getTaskId(),memberId);
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
        Task taskByTaskId = taskMapper.findTaskBySubTaskId(task.getTaskId());
        if(taskByTaskId.getTaskStatus().equals("完成")){
            throw new ServiceException();
        }
        StringBuilder content = new StringBuilder("");
        //如果子任务为完成则设置成未完成 如果子任务为未完成则设置为完成
        if(task.getTaskStatus().equals("完成")){
            content.append(TaskLogFunction.A12.getName()).append(" ").append("\"").append(task.getTaskName()).append("\"");
        }
        if(task.getTaskStatus().equals("未完成")){
            content.append(TaskLogFunction.I.getName()).append(" ").append("\"").append(task.getTaskName()).append("\"");
        }
        //更新任务信息
        int result = taskMapper.changeTaskStatus(task.getTaskId(),task.getTaskStatus(),System.currentTimeMillis());
        Log log = logService.saveLog(task.getTaskId(),content.toString(),1);
        log.setResult(result);
        return log;
    }

    /**
     * 复制任务
     * @param task 当前任务信息
     * @param projectId 当前任务所在的项目id
     * @param newTaskMenuVO 要复制到的位置信息
     * @return
     */
    @Override
    public Log copyTask(Task task, String projectId, TaskMenuVO newTaskMenuVO) {
        String oldTaskId = task.getTaskId();
        //把被复制的任务的id更改成新生成的任务的id
        task.setTaskId(IdGen.uuid());
        //更新新任务的创建时间
        task.setCreateTime(System.currentTimeMillis());
        //设置新任务的更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //如果复制到其他项目 则任务的 参与者信息、执行者 不会保留
        if(!projectId.equals(newTaskMenuVO.getProjectId())){
            task.setExecutor("");
        } else{
            //如果复制到其他分组或者其他菜单的话  保留任务的执行者以及参与者信息
            List<TaskMember> taskMemberByTaskId = taskMemberService.findTaskMemberByTaskId(oldTaskId);
            //循环向数据库添加 任务成员关系 和 文件,任务,分享,日程 的关联关系
            if(taskMemberByTaskId != null && taskMemberByTaskId.size() > 0){
                for (TaskMember taskMembers : taskMemberByTaskId) {
                    taskMembers.setId(IdGen.uuid());
                    taskMembers.setCreateTime(System.currentTimeMillis());
                    taskMembers.setUpdateTime(System.currentTimeMillis());
                    taskMemberService.saveTaskMember(taskMembers);
                }
            }

            //重新插入关联的文件信息
        }
        //保存到数据库
        taskMapper.saveTask(task);
        int result = 0;
        StringBuilder content = new StringBuilder("");
        //根据被复制任务的id 取出该任务所有的子任务
        List<Task> subLevelTaskList = taskMapper.findSubLevelTask(task.getTaskId());
        if(subLevelTaskList != null && subLevelTaskList.size() > 0){
            //把所有的子任务信息设置好后插入数据库
            for (Task subLevelTask : subLevelTaskList) {
                //设置新的子任务id
                subLevelTask.setTaskId(IdGen.uuid());
                //设置新的子任务的父任务id
                subLevelTask.setParentId(task.getTaskId());
                //设置新子任务的更新时间
                subLevelTask.setUpdateTime(System.currentTimeMillis());
                //设置新子任务的创建时间
                subLevelTask.setCreateTime(System.currentTimeMillis());
                result += taskMapper.saveTask(subLevelTask);
            }
        }
        //追加日志字符串
        content.append(TaskLogFunction.R.getName()).append(" ").append(task.getTaskName());
        Log log = logService.saveLog(task.getTaskId(), content.toString(),1);
        log.setResult(result);
        return log;
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
        String memberId = "admin";
        //暂时不用
        //String memberId = ShiroAuthenticationManager.getUserEntity().getId();
        //如果收藏了任务返回false 负责返回true
        int result = publicCollectService.judgeCollectPublic(memberId,task.getTaskId(),"任务");
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
        //先将任务成员关系表的执行者清掉
        taskMapper.clearExecutor(taskId);
        Task task = new Task();
        task.setTaskId(taskId);
        taskMapper.removeExecutor(taskId);
        //拼接日志
        String content = TaskLogFunction.A.getName();
        return logService.saveLog(task.getTaskId(),content,1);
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
     * @param userInfoEntity 用户信息
     * @param uName 用户名
     * @return
     */
    @Override
    public Log updateTaskExecutor(String taskId, UserInfoEntity userInfoEntity,String uName) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setExecutor(userInfoEntity.getId());
        taskMapper.updateTask(task);
        taskMemberService.delTaskMemberExecutor(taskId);
        //初始化一个任务成员关系实体
        TaskMember taskMember = new TaskMember();
        taskMember.setId(IdGen.uuid());
        taskMember.setMemberId(userInfoEntity.getId());
        taskMember.setPublicId(task.getTaskId());
        taskMember.setMemberName(uName);
        taskMember.setMemberImg(userInfoEntity.getImage());
        //设置任务的关联类型
        taskMember.setPublicType("任务");
        taskMember.setType("执行者");
        taskMember.setCreateTime(System.currentTimeMillis());
        taskMember.setUpdateTime(System.currentTimeMillis());
        taskMemberService.saveTaskMember(taskMember);
        //查询新的任务执行者之前是不是此任务的参与者
        int isTaskMember = taskMemberService.findTaskMemberExecutorIsMember(userInfoEntity.getId(), task.getTaskId());
        //如果新的任务执行者以前已经是该任务的参与者  就不在添加该执行者的参与者信息
        if(isTaskMember == 0){
            taskMember.setId(IdGen.uuid());
            taskMember.setType("参与者");
            taskMemberService.saveTaskMember(taskMember);
        }
        StringBuilder content = new StringBuilder();
        content.append(TaskLogFunction.U.getName()).append(" ").append(uName);
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
     * @param projectId 项目id
     */
    @Override
    public void recoveryTask(String taskId, String menuId,String projectId) {
        taskMapper.recoverTask(taskId,menuId,System.currentTimeMillis(),projectId);
        //任务状态为0 日志打印内容为 xxx把任务移入了回收站
        TaskLogVO taskLogVO = new TaskLogVO();
        Task task = new Task();
        task.setTaskId(taskId);
        logService.saveLog(task.getTaskId(),TaskLogFunction.P.getName(),1);
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
}
