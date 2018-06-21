package com.art1001.supply.service.task.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.collect.TaskCollect;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.*;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserInfoEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.collect.TaskCollectMapper;
import com.art1001.supply.mapper.task.*;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.collect.TaskCollectService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.task.TaskLogService;
import com.art1001.supply.service.task.TaskMemberService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

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
    private TaskCollectService taskCollectService;

    /** 用户逻辑层接口 */
    @Resource
    private UserService userService;
	
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
	    return taskMapper.deleteTaskByTaskId(taskId);
    }

	/**
	 * 重写方法
	 * 修改task数据
	 * @param task 任务信息
	 */
	@Override
	public TaskLogVO updateTask(Task task){
	    String content = "";
	    TaskLogVO taskLogVO = new TaskLogVO();
	    //任务更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //更新任务内容
        if(task.getTaskName() != null && task.getTaskName() != ""){
            taskLogVO = saveTaskLog(task,TaskLogFunction.T.getName());
        }
        //更新任务优先级
        if(task.getPriority() != null && task.getPriority() != ""){
            taskLogVO = saveTaskLog(task,TaskLogFunction.F.getName() + " " + task.getPriority());
        }
        //更新任务备注
        if(task.getRemarks() != null && task.getRemarks() != null){
            taskLogVO = saveTaskLog(task,TaskLogFunction.E.getName());
        }
        //更新任务执行者
        if(task.getExecutor() != null && task.getExecutor() != ""){
            taskLogVO = saveTaskLog(task,TaskLogFunction.U.getName());
        }
        //更新任务其他
        if(task.getOther() != null && task.getOther() != ""){
            taskLogVO = saveTaskLog(task,TaskLogFunction.G.getName());
        }
        int result = taskMapper.updateTask(task);
        taskLogVO.setResult(result);
        return taskLogVO;
	}

	/**
	 * 重写方法
	 * 将添加的任务信息保存至数据库
     * @param memberId 该任务的参与者
     * @param project 当前项目的实体信息
     * @param task task信息
     */
	@Override
	public TaskLogVO saveTask(UserEntity[] memberId, Project project, Task task) {
        //获取当前登录用户的id
        //String id = ShiroAuthenticationManager.getUserEntity().getId();
        task.setMemberId("4");
        //设置该任务的id
        task.setTaskId(IdGen.uuid());
        //初始创建任务设置为父任务
        task.setParentId("0");
        //设置所在项目
        task.setProjectId(project.getProjectId());
        //设置该任务的初始状态
        task.setTaskStatus("未完成");
        //设置该任务是否删除 0 未删除 1 已删除
        task.setTaskDel(0);
        //设置任务的隐私面模式
        task.setPrivacyPattern(0);
        //如果没有设置执行者 则 为空字符串
        if(StringUtils.isEmpty(task.getExecutor())){
            System.out.println("走");
            task.setExecutor(new String(""));
        }
        //设置该任务的创建时间
        task.setCreateTime(System.currentTimeMillis());
        //设置该任务的最后更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //保存任务信息
        taskMapper.saveTask(task);
        //将任务的参与者信息保存至 (任务-参与者 [task_member] ) 关系表中
        taskMemberService.saveManyTaskeMmber(memberId,task);
        //拿到TaskLog对象并且保存
        return saveTaskLog(task, TaskLogFunction.R.getName() + task.getTaskName());
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
     * 移入回收站/恢复任务
     * @param taskId 当前任务id
     * @param taskDel 当前任务是否已经在回收站
     * @return
     */
    @Override
    public TaskLogVO moveToRecycleBin(String taskId, String taskDel) {
        //把该任务放到回收站
        int result = taskMapper.moveToRecycleBin(taskId,taskDel,System.currentTimeMillis());
        Task task = new Task();
        task.setTaskId(taskId);
        TaskLogVO taskLogVO = new TaskLogVO();
        //如果任务状态为0 日志打印内容为 xxx把任务移入了回收站 否则   xxx恢复了任务
        if(taskDel == "0"){
            taskLogVO = saveTaskLog(task,TaskLogFunction.P.getName());
        } else{
            taskLogVO = saveTaskLog(task,TaskLogFunction.O.getName());
        }
        taskLogVO.setResult(result);
        return taskLogVO;
    }

	/**
	 * 重写方法
	 * 修改当前任务状态
	 * @param task 任务信息
	 * @return
	 */
	@Override
	public TaskLogVO resetAndCompleteTask(Task task) {
	    StringBuilder content = new StringBuilder("");
        //如果当前状态为未完成  则 日志记录为 完成任务 否则为 重做任务
        if(task.getTaskStatus().equals("完成")){
            content.append(TaskLogFunction.Q.getName()).append(" ").append("\"").append(task.getTaskName()).append("\"");
        }
        //查询出该父级任务下的所有子级任务
        List<Task> subLevelList = taskMapper.findSubLevelTask(task.getTaskId());
        if(subLevelList != null){
            //如果所有的子级任务里有未完成的则抛出异常
            for (Task t : subLevelList) {
                if(t.getTaskStatus().equals("未完成") || t.getTaskStatus().equals("重新开始")){
                    throw new ServiceException("必须完成子级任务,才能完成父级任务!");
                }
            }
        }
	    //修改任务状态
	    int result = taskMapper.changeTaskStatus(task.getTaskId(),task.getTaskStatus(),System.currentTimeMillis());
        int a = 1/0;
        if(task.getTaskStatus().equals("未完成")){
            content.append(TaskLogFunction.S.getName()).append(" ").append("\"").append(task.getTaskName()).append("\"");
        }
        if(task.getTaskStatus().equals("重新开始")){
            content.append(TaskLogFunction.S.getName()).append(" ").append("\"").append(task.getTaskName()).append("\"");
        }
        TaskLogVO taskLogVO = saveTaskLog(task, content.toString());
        taskLogVO.setResult(result);
        return taskLogVO;
    }

    /**
     * 重写方法
     * 设定任务的时间(开始 / 结束)
     * @param task 任务时间的信息
     * @return
     */
    @Override
    public TaskLogVO updateTaskStartAndEndTime(Task task) {
        String content = "";
        if(task.getStartTime() != null){
            content = TaskLogFunction.M.getName();
        }
        if(task.getEndTime() != null){
            content = TaskLogFunction.L.getName();
        }
        int result =  taskMapper.updateTask(task);
        TaskLogVO taskLogVO = saveTaskLog(task, content);
        taskLogVO.setResult(result);
        return taskLogVO;
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
    public TaskLogVO mobileTask(Task task, TaskMenuVO oldTaskMenuVO,TaskMenuVO newTaskMenuVO) {
        task.setExecutor("");
        taskMapper.clearTaskMember(task.getTaskId());
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
            TaskLogVO taskLogVO = saveTaskLog(task, content);
            taskLogVO.setResult(result);
            return taskLogVO;
        }
        //如果任务的菜单信息不为空 说明该任务要移至其他的任务菜单
        if(newTaskMenuVO.getTaskMenuId() != null && newTaskMenuVO.getTaskMenuId() != ""){
            //拼接任务操作日志内容的字符串
            content = TaskLogFunction.X.getName() + " " + newTaskMenuVO.getTaskMenuName();
            //保存日志信息
            TaskLogVO taskLogVO = saveTaskLog(task, content);
            taskLogVO.setResult(result);
            return taskLogVO;
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
    public TaskLogVO turnToFatherLevel(Task task) {
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
        TaskLogVO taskLogVO = saveTaskLog(task, content.toString());
        taskLogVO.setResult(result);
        return taskLogVO;
    }

    /**
     * 绑定标签到当前任务
     * @param tag 标签的实体信息
     * @param taskId 当前任务的id
     * @param countByTagName 判断要绑定到任务上的标签是不是已经存在
     * @return
     */
    @Override
    public TaskLogVO addTaskTags(Tag tag,String taskId,int countByTagName) {
        //先查询出当前任务原有的标签id信息
        String taskTag = taskMapper.findTaskTagByTaskId(taskId);
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
        TaskLogVO taskLogVO = new TaskLogVO();
        //判断 如果是向数据库新插入了标签 则保存日志 否则不保存
        if(countByTagName == 0){
            //拼接任务操作日志内容
            StringBuilder content = new StringBuilder("");
            content.append(TaskLogFunction.A10.getName()).append(" ").append(tag.getTagName());
            taskLogVO = saveTaskLog(task, content.toString());
            taskLogVO.setResult(result);
        }
        return taskLogVO;
    }

    /**
     * 移除该任务上的标签
     * @param tags 当前任务上绑定的所有标签对象数组
     * @param tag 当前要被的标签对象
     * @param taskId 当前任务uid
     * @return
     */
    @Override
    public int removeTaskTag(Tag[] tags, Tag tag, String taskId) {
        StringBuilder taskTagsId = new StringBuilder();
        //循环标签数组
        for (int i = 0; i < tags.length ; i++) {
            //如果循环到的标签和要被删除的标签的信息一致时 清空该对象
            if(tags[i].getTagId().equals(tag.getTagId())){
                tags[i] = null;
                continue;
            }
            //累加标签的id
            taskTagsId.append(tags[i].getTagId()).append(",");
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
    public TaskLogVO updateTaskRepeat(Task task, Object object) {
        //判断是不是自定义重复
        if(!task.getRepeat().equals("自定义重复")){
            //如果不是自定义重复删除该任务的自定义重复时间
        }
        task.setUpdateTime(System.currentTimeMillis());
        int result = taskMapper.updateTask(task);
        String content = TaskLogFunction.D.getName();
        TaskLogVO taskLogVO = saveTaskLog(task, content);
        taskLogVO.setResult(result);
        return taskLogVO;
    }

    /**
     * 更新任务的提醒时间
     * @param task 任务实体信息
     * @param userEntity 用户实体信息
     * @return
     */
    @Override
    public TaskLogVO updateTaskRemindTime(Task task, UserEntity userEntity) {
        //判断是开始时提醒还是结束时提醒
        if(task.getRemind().equals("任务截止时提醒")){
           task.setRepetitionTime(task.getEndTime());
        }
        if(task.getRemind().equals("任务开始时提醒")){
            task.setRepetitionTime(task.getStartTime());
        }
        task.setUpdateTime(System.currentTimeMillis());
        String content = TaskLogFunction.M.getName();
        int result = taskMapper.updateTask(task);
        TaskLogVO taskLogVO = saveTaskLog(task, content);
        taskLogVO.setResult(result);
        return taskLogVO;
        //UserEntity是要被提醒的成员信息(暂时先不用)
    }

    /**
     * 清除任务的开始时间和结束时间
     * @param task 任务的实体信息
     * @return
     */
    @Override
    public TaskLogVO removeTaskStartAndEndTime(Task task) {
        StringBuilder content = new StringBuilder("");
        int result = 0;
        //设置最后更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //如果开始时间不为空 则清空开始时间
        if(task.getStartTime() != null){
            result = taskMapper.removeTaskStartTime(task);
            content.append(TaskLogFunction.J.getName());
        }
        //如果截止时间不为空 则清空截止时间
        if(task.getEndTime() != null){
            result = taskMapper.removeTaskEndTime(task);
            content.append(TaskLogFunction.K.getName());
        }
        //保存操作日志
        TaskLogVO taskLogVO = saveTaskLog(task, content.toString());
        taskLogVO.setResult(result);
        return taskLogVO;
    }

    /**
     * 添加项目成员
     * @param task 任务实体信息
     * @param addUserEntity 要添加的参与者的信息
     * @param removeUserEntity 要移除的参与者的信息
     * @return
     */
    @Override
    public TaskLogVO addAndRemoveTaskMember(Task task, UserEntity[] addUserEntity, UserEntity[] removeUserEntity) {
        StringBuilder content = new StringBuilder("");
        //向任务成员表中添加数据
        if(addUserEntity != null){
            taskMemberService.addManyMemberInfo(addUserEntity,task);
            //循环用来拼接log日志字符串
            content.append(TaskLogFunction.C.getName()).append(" ");
            for (int i = 0; i < addUserEntity.length; i++) {
                if(i == addUserEntity.length - 1){
                    content.append(addUserEntity[i].getUserName());
                } else{
                    content.append(addUserEntity[i].getUserName()).append(",");
                }
            }
        }
        if(removeUserEntity != null){
            taskMemberService.delTaskMemberByTaskIdAndMemberId(task, removeUserEntity);
            if(!StringUtils.isEmpty(content.toString())){
                content.append(",");
            }
            content.append(TaskLogFunction.B.getName()).append(" ");
            for (int i = 0; i < removeUserEntity.length; i++) {
                if(i == removeUserEntity.length - 1){
                    content.append(removeUserEntity[i].getUserName());
                } else{
                    content.append(removeUserEntity[i].getUserName()).append(",");
                }
            }
        }
        TaskLogVO taskLogVO = saveTaskLog(task, content.toString());
        return taskLogVO;
    }

    /**
     * 移除任务-成员关系
     * @param task 当前项目实体信息
     * @param userEntity 被移除的用户的信息
     * @return
     */
    @Override
    public TaskLogVO removeTaskMember(Task task, UserEntity userEntity) {
        taskMemberService.removeTaskMember(task,userEntity);
        StringBuilder builder = new StringBuilder("");
        builder.append(TaskLogFunction.B.getName()).append(" ").append(userEntity.getUserName());
        return saveTaskLog(task,builder.toString());
    }

    /**
     * 给当前任务点赞
     * @param task 任务的实体信息
     * @return
     */
    @Override
    public int clickFabulous(Task task) {
        //暂时不用
        //String memberId = ShiroAuthenticationManager.getUserEntity().getId();
        Fabulous fabulous = new Fabulous();
        fabulous.setTaskId(task.getTaskId());
        fabulous.setMemberId("11111");
        fabulous.setFabulousId(System.currentTimeMillis());
        //添加任务和赞的关系数据
        int result = fabulousMapper.addFabulous(fabulous);
        //更新任务得赞数量
        task.setFabulousCount(task.getFabulousCount() + 1);
        System.out.println(task.getFabulousCount());
        return taskMapper.updateTask(task);
    }

    /**
     * 判断当前用户有没有给该任务点赞
     * @param task 任务信息
     * @return
     */
    @Override
    public boolean judgeFabulous(Task task) {
        //获取当前用户登录的id (暂时不用)
        //String memberId = ShiroAuthenticationManager.getUserEntity().getId();
        String member = "11111";
        int result = fabulousMapper.judgeFabulous(task.getTaskId(),member);
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
        //获取当前用户登录的id (暂时不用)
        //String memberId = ShiroAuthenticationManager.getUserEntity().getId();
        String memberId = "11111";
        //这里要把当前任务的赞 - 1
        task.setFabulousCount(task.getFabulousCount()-1);
        taskMapper.updateTask(task);
        return fabulousMapper.cancelFabulous(task.getTaskId(),memberId);
    }

    /**
     * 给当前任务添加子任务
     * @param currentTask 当前任务 信息
     * @param subLevel 子级任务信息
     * @return
     */
    @Override
    public TaskLogVO addSubLevelTasks(Task currentTask, Task subLevel) {
        //获取当前登录用户的id
        //String id = ShiroAuthenticationManager.getUserEntity().getId();
        subLevel.setMemberId("11111");
        //设置任务的层级
        subLevel.setLevel(2);
        //设置父任务id
        subLevel.setParentId(currentTask.getTaskId());
        //设置该任务的id
        subLevel.setTaskId(IdGen.uuid());
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
        TaskLogVO taskLogVO = saveTaskLog(currentTask, content.toString());
        taskLogVO.setResult(result);
        return taskLogVO;
    }

    /**
     * 完成和重做子任务
     * @param task 当前任务信息
     * @return
     */
    @Override
    public TaskLogVO resetAndCompleteSubLevelTask(Task task) {
        StringBuilder content = new StringBuilder("");
        //如果子任务为完成则设置成未完成 如果子任务为未完成则设置为完成
        if(task.getTaskStatus().equals("完成")){
            content.append(TaskLogFunction.I.getName()).append(" ").append("\"").append(task.getTaskName()).append("\"");
        }
        if(task.getTaskStatus().equals("未完成")){
            content.append(TaskLogFunction.A12.getName()).append(" ").append("\"").append(task.getTaskName()).append("\"");
        }
        if(task.getTaskStatus().equals("重新开始")){
            content.append(TaskLogFunction.A12.getName()).append(" ").append("\"").append(task.getTaskName()).append("\"");
        }
        //更新任务信息
        int result = taskMapper.changeTaskStatus(task.getTaskId(),task.getTaskStatus(),System.currentTimeMillis());
        TaskLogVO taskLogVO = saveTaskLog(task, content.toString());
        taskLogVO.setResult(result);
        return taskLogVO;
    }

    /**
     * 复制任务
     * @param task 当前任务信息
     * @return
     */
    @Override
    public TaskLogVO copyTask(Task task) {
        //根据被复制任务的id 取出该项目所有的子任务
        List<Task> subLevelTaskList = taskMapper.findSubLevelTask(task.getTaskId());
        //把被复制的任务的id更改成新生成的任务的id
        task.setTaskId(IdGen.uuid());
        //更新新任务的创建时间
        task.setCreateTime(System.currentTimeMillis());
        //设置新任务的更新时间
        task.setUpdateTime(System.currentTimeMillis());
        taskMapper.saveTask(task);
        int result = 0;
        StringBuilder content = new StringBuilder("");
        //把所有的子任务信息设置好后插入数据库
        for (Task subLevelTask : subLevelTaskList) {
            //设置新的子任务id
            subLevelTask.setTaskId(IdGen.uuid());
            //设置新的子任务id为新子任务的id
            subLevelTask.setParentId(task.getTaskId());
            //设置子任务的项目id为新子任务的项目id
            subLevelTask.setProjectId(task.getProjectId());
            //设置新子任务的更新时间
            subLevelTask.setUpdateTime(System.currentTimeMillis());
            //设置新子任务的创建时间
            subLevelTask.setCreateTime(System.currentTimeMillis());
            result += taskMapper.saveTask(subLevelTask);
        }
        //追加日志字符串
        content.append(TaskLogFunction.R.getName()).append(" ").append(task.getTaskName());
        TaskLogVO taskLogVO = saveTaskLog(task, content.toString());
        taskLogVO.setResult(result);
        return taskLogVO;
    }

    /**
     * 收藏任务
     * @param task 任务实体信息
     * @return
     */
    @Override
    public int collectTask(Task task) {
        TaskCollect taskCollect = new TaskCollect();
        //设置收藏的id
        taskCollect.setId(IdGen.uuid());
        //暂时不用
        //taskCollect.setMemberId(ShiroAuthenticationManager.getUserEntity().getId());
        //taskCollect.setMemberName(ShiroAuthenticationManager.getUserEntity().getUserName());
        //taskCollect.setMemberImg(ShiroAuthenticationManager.getUserEntity().getUserInfo().getImage());
        //设置收藏的项目的id
        taskCollect.setProjectId(task.getProjectId());
        //设置收藏的任务id
        taskCollect.setTaskId(task.getTaskId());
        //设置这条收藏的创建时间
        taskCollect.setCreateTime(System.currentTimeMillis());
        //设置这条收藏的更新时间
        taskCollect.setUpdateTime(System.currentTimeMillis());
        //保存至数据库
        return taskCollectService.saveTaskCollect(taskCollect);
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
        int result = taskCollectService.judgeCollectTask(memberId,task.getTaskId());
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
        //暂时不用
        //String memberId = ShiroAuthenticationManager.getUserEntity().getId();
        int result = taskCollectService.deleteTaskCollectById("12",task.getTaskId());
        return 0;
    }

    /**
     * 更改当前任务的隐私模式
     * @param task 任务的实体信息
     * @return
     */
    @Override
    public int SettingUpPrivacyPatterns(Task task) {
        return taskMapper.SettingUpPrivacyPatterns(task);
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
        for (int i = 0; i < projectAllMember.size(); i++) {
            if(projectAllMember.get(i).getId().equals(executor)){
                projectAllMember.remove(projectAllMember.get(i));
            }
        }
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
        if(StringUtils.isEmpty(status)){
            taskList = taskMapper.findTaskByToday(projectId);
        } else{
            taskList = taskMapper.findTaskByStatus(status,projectId);
        }
        return taskList;
    }

    /**
     * 查询某个菜单下的所有任务的信息
     * @param menuId 菜单id
     * @return
     */
    @Override
    public List<Task> taskMenu(String menuId) {
        return taskMapper.taskMenu(menuId);
    }

    /**
     * 查询某个人执行的所有任务
     * @param uId 执行者的id
     * @param projectId 项目id
     * @return
     */
    @Override
    public List<Task> findTaskByExecutor(String uId,String projectId) {
        return taskMapper.findTaskByExecutor(uId,projectId);
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
    public int removeExecutor(String taskId) {
        return taskMapper.removeExecutor(taskId);
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
     * @param executor
     * @return
     */
    @Override
    public int updateTaskExecutor(String taskId, String executor) {
        Task task = new Task();
        task.setTaskId(taskId);
        task.setExecutor(executor);
        return taskMapper.updateTask(task);
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
    }

    /**
     * 返回日志实体对象
     */
    @Override
    public TaskLogVO saveTaskLog(Task task,String content){
        TaskLog taskLog = new TaskLog();
        taskLog.setId(IdGen.uuid());
        taskLog.setMemberName("admin");
        taskLog.setMemberId("4");
        //暂时不用
        //taskLog.setMemberName(ShiroAuthenticationManager.getUserEntity().getUserName());
        //taskLog.setMemberId(ShiroAuthenticationManager.getUserEntity().getId());
        //taskLog.setMemberImg(ShiroAuthenticationManager.getUserEntity().getUserInfo().getImage());
        //头像暂无
        taskLog.setMemberImg("");
        taskLog.setTaskId(task.getTaskId());
        taskLog.setContent("admin " + content);
        taskLog.setCreateTime(System.currentTimeMillis());
        taskLogService.saveTaskLog(taskLog);
        TaskLogVO taskLogVO = taskLogService.findTaskLogContentById(taskLog.getId());
        return taskLogVO;
    }

}