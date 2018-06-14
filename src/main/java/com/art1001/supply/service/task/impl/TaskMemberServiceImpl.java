package com.art1001.supply.service.task.impl;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;
import javax.swing.plaf.synth.SynthOptionPaneUI;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskLog;
import com.art1001.supply.entity.task.TaskLogVO;
import com.art1001.supply.entity.task.TaskMember;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.mapper.task.TaskMemberMapper;
import com.art1001.supply.mapper.user.UserMapper;
import com.art1001.supply.service.task.TaskMemberService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * taskMemberServiceImpl
 */
@Service
public class TaskMemberServiceImpl implements TaskMemberService {

	/** taskMemberMapper接口*/
	@Resource
	private TaskMemberMapper taskMemberMapper;

	/** userService 接口*/
	@Resource
	private UserService userService;

	/** taskService 接口*/
	@Resource
	private TaskService taskService;

	/**
	 * 查询分页taskMember数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<TaskMember> findTaskMemberPagerList(Pager pager){
		return taskMemberMapper.findTaskMemberPagerList(pager);
	}

	/**
	 * 通过id获取单条taskMember数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public TaskMember findTaskMemberById(String id){
		return taskMemberMapper.findTaskMemberById(id);
	}

	/**
	 * 通过id删除taskMember数据
	 * 
	 * @param taskMemberId 关联id
	 * @param task 任务实体信息
	 * @param file 文件实体信息
	 * @param share 分享实体信息
	 * @param schedule 日程实体信息
	 * @param taskId 当前被操作的任务的id
	 */
	@Override
	public TaskLogVO deleteTaskMemberById(Task task,File file,Share share,Schedule schedule,String taskId,String taskMemberId){
		TaskLogVO taskLogVO = new TaskLogVO();
		int result = taskMemberMapper.deleteTaskMemberById(taskMemberId);
		Task currentTask = new Task();
		currentTask.setTaskId(taskId);
		StringBuilder content = new StringBuilder("");
		//判断关联的是不是任务
		if(task != null ){
			content.append(TaskLogFunction.A7.getName()).append(" ");
			taskLogVO.setTask(task);
		}
		//判断关联的是不是文件
		if(file != null ){
			content.append(TaskLogFunction.A6.getName()).append(" ");
			taskLogVO.setFile(file);
		}
		//判断关联的是不是日程
		if(schedule != null ){
			content.append(TaskLogFunction.A5.getName()).append(" ");
			taskLogVO.setSchedule(schedule);
		}
		//判断关联的是不是分享
		if(share != null ){
			content.append(TaskLogFunction.A1.getName()).append(" ");
			taskLogVO.setShare(share);
		}
		taskService.saveTaskLog(currentTask,content.toString());
		taskLogVO.setResult(result);
		return taskLogVO;
	}

	/**
	 * 修改taskMember数据
	 * 
	 * @param taskMember
	 */
	@Override
	public void updateTaskMember(TaskMember taskMember){
		taskMemberMapper.updateTaskMember(taskMember);
	}

	/**
	 * 保存taskMember数据
	 * 
	 * @param
	 */
//	@Override
	public TaskLogVO saveTaskMember(Task task, File file, Share share, Schedule schedule,TaskMember taskMember,String taskId) {
		StringBuilder content = new StringBuilder("");
		//判断关联的是不是任务
		if(task != null ){
			content.append(TaskLogFunction.N.getName()).append(" ").append(task.getTaskName());
			taskMember.setPublicId(task.getTaskId());
			taskMember.setPublicType("1");
		}
		//判断关联的是不是文件
		if(file != null ){
		    taskMember.setPublicId(file.getFileId());
            content.append(TaskLogFunction.A3.getName()).append(" ").append(file.getFileName());
			taskMember.setPublicType("4");
		}
		//判断关联的是不是日程
		if(schedule != null ){
            taskMember.setPublicId(schedule.getScheduleId());
            content.append(TaskLogFunction.A2.getName()).append(" ").append(schedule.getScheduleName());
			taskMember.setPublicType("3");
		}
		//判断关联的是不是分享
		if(share != null ){
            taskMember.setPublicId(share.getId());
            content.append(TaskLogFunction.A1.getName()).append(" ").append(share.getTitle());
			taskMember.setPublicType("2");
		}
		//设置id
        taskMember.setId(IdGen.uuid());
		//设置这条关联信息的创建时间
		taskMember.setCreateTime(System.currentTimeMillis());
		//设置这条关联信息的更新时间
		taskMember.setUpdateTime(System.currentTimeMillis());
		//将关联信息保存至数据库
		int result = taskMemberMapper.saveTaskMember(taskMember);
		//给这条关联关系设置被操作的任务的id
		taskMember.setCurrentTaskId(taskId);
		//创建一个任务对象 用来封装当前被操作任务的信息
		Task currentTask = new Task();
		currentTask.setTaskId(taskId);
		//返回日志和结果
        return taskService.saveTaskLog(currentTask, content.toString());
	}
	/**
	 * 获取所有taskMember数据
	 * 
	 * @return
	 */
	@Override
	public List<TaskMember> findTaskMemberAllList(){
		return taskMemberMapper.findTaskMemberAllList();
	}

	/**
	 * 保存任务和参与者的关系
	 * @param member 参与者们的信息
	 * @param task  (包含任务的创建人 用来确认是否是该任务的创建人) taskId 新创建的任务的id
	 */
	@Override
	public void saveManyTaskeMmber(UserEntity[] member,Task task) {
		//循环参与者信息把信息放到任务和参与者的实体类对象中
		for (UserEntity userEntity : member){
			TaskMember taskMember = new TaskMember();
			//设置id
			taskMember.setId(IdGen.uuid());
			//设置参与者姓名
			taskMember.setMemberName(userEntity.getUserName());
			//设置参与者id
			taskMember.setMemberId(userEntity.getId());
			//设置这条关系的创建时间
			taskMember.setCreateTime(System.currentTimeMillis());
			//设置当前任务id
			taskMember.setCurrentTaskId(task.getTaskId());
			//设置更新时间
			taskMember.setUpdateTime(System.currentTimeMillis());
			//如果当前用户的id是该任务的创建者 则把该条关系的任务角色设置为创建者
			if(userEntity.getId().equals(task.getMemberId())){
				taskMember.setType("3");
			} else{
				//否则设置为参与者
				taskMember.setType("1");
			}
			//该条关系的更新时间
			taskMember.setUpdateTime(System.currentTimeMillis());
			//将该关系对象 保存至数据库
			taskMemberMapper.saveTaskMember(taskMember);
		}
	}

    /**
     * 循环向(任务-成员) 关系表中添加多条数据
     * @param userEntity
     * @param task
     * @return
     */
	public int addManyMemberInfo(UserEntity[] userEntity,Task task){
	    int result = 0;
		for (int i = 0; i < userEntity.length ; i++) {
			TaskMember taskMember = new TaskMember();
			//设置id
			taskMember.setId(IdGen.uuid());
			//设置参与者姓名
			taskMember.setMemberName(userEntity[i].getUserName());
			//设置参与者id
			taskMember.setMemberId(userEntity[i].getId());
			//设置这条关系的创建时间
			taskMember.setCreateTime(System.currentTimeMillis());
			//设置当前任务id
			taskMember.setCurrentTaskId(task.getTaskId());
			//设置任务成员的角色
            taskMember.setType("参与者");
            //设置更新时间
            taskMember.setUpdateTime(System.currentTimeMillis());
            result += taskMemberMapper.saveTaskMember(taskMember);
        }
		return result;
	}

    /**
     * 通过任务id 成员id 删除 任务成员关系表的数据
     * @param task 任务实体信息
     * @param userEntity 多个成员信息
     * @return
     */
	@Override
	public TaskLogVO delTaskMemberByTaskIdAndMemberId(Task task, UserEntity[] userEntity) {
        int result =0;
        StringBuilder content = new StringBuilder(TaskLogFunction.A.getName()).append(" ");
        //循环删除任务成员关系并且拼接日志字符串
		for (int i = 0; i < userEntity.length; i++) {
			result = taskMemberMapper.delTaskMemberByTaskIdAndMemberId(task,userEntity[i]);
			if(i == userEntity.length - 1){
				content.append(userEntity[i].getUserName());
			} else{
				content.append(userEntity[i].getUserName()).append(",");
			}
		}
        TaskLogVO taskLogVO = taskService.saveTaskLog(task, content.toString());
		taskLogVO.setResult(result);
        return taskLogVO;
	}

}