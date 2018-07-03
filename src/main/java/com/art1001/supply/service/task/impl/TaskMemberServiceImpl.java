package com.art1001.supply.service.task.impl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskLogVO;
import com.art1001.supply.entity.task.TaskMember;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserInfoEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.mapper.task.TaskMemberMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.task.TaskMemberService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import jodd.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
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

	/** fileService接口 */
	@Resource
	private FileService fileService;

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
	 * 清空任务成员关系
	 * @param taskId 任务id
	 */
	@Override
	public void clearTaskMemberByTaskId(String taskId) {
		taskMemberMapper.clearTaskMemberByTaskId(taskId);
	}

	/**
	 * 保存任务和参与者的关系
	 * @param member 参与者们的信息
	 * @param task  (包含任务的创建人 用来确认是否是该任务的创建人) taskId 新创建的任务的id
	 */
	@Override
	public void saveManyTaskeMmber(String[] member,Task task) {
		//保存创建者的信息
		String [] createAndExecutor = {"创建者","执行者"};
		for (String str : createAndExecutor) {
			TaskMember taskMember = new TaskMember();
			taskMember.setId(IdGen.uuid());
			taskMember.setPublicId(task.getTaskId());
			taskMember.setCreateTime(System.currentTimeMillis());
			taskMember.setUpdateTime(System.currentTimeMillis());
			if(str.equals("创建者")){
				taskMember.setType("创建者");
				taskMember.setMemberImg(ShiroAuthenticationManager.getUserEntity().getUserInfo().getImage());
				taskMember.setMemberName(ShiroAuthenticationManager.getUserEntity().getUserName());
				taskMember.setMemberId(ShiroAuthenticationManager.getUserEntity().getId());
				taskMemberMapper.saveTaskMember(taskMember);
				continue;
			}
			if(str.equals("执行者")){
				if(!StringUtils.isEmpty(task.getExecutor())){
					UserEntity userById = userService.findUserInfoById(task.getExecutor());
					taskMember.setType("执行者");
					taskMember.setMemberId(userById.getId());
					taskMember.setMemberName(userById.getUserName());
					taskMember.setMemberImg(userById.getUserInfo().getImage());
					taskMemberMapper.saveTaskMember(taskMember);
					taskMember.setId(IdGen.uuid());
					taskMember.setType("参与者");
					taskMemberMapper.saveTaskMember(taskMember);
				}
			}
		}
		//循环参与者信息把信息放到任务和参与者的实体类对象中
		if(member != null && member.length > 0){
			List<UserEntity> manyUserById = userService.findManyUserById(member);
			if(manyUserById != null && manyUserById.size() > 0) {
				for (UserEntity userEntity : manyUserById) {
					TaskMember taskMember = new TaskMember();
					//设置id
					taskMember.setId(IdGen.uuid());
					//设置参与者id
					taskMember.setMemberId(userEntity.getId());
					//设置参与者姓名
					taskMember.setMemberName(userEntity.getUserName());
					//设置这条关系的创建时间
					taskMember.setCreateTime(System.currentTimeMillis());
					//设置名字
					taskMember.setMemberName(userEntity.getUserName());
					//设置头像
					taskMember.setMemberImg(userEntity.getUserInfo().getImage());
					//设置当前任务id
					taskMember.setPublicId(task.getTaskId());
					//设置更新时间
					taskMember.setUpdateTime(System.currentTimeMillis());
					taskMember.setType("参与者");
					//该条关系的更新时间
					taskMember.setUpdateTime(System.currentTimeMillis());
					//将该关系对象 保存至数据库
					taskMemberMapper.saveTaskMember(taskMember);
				}
			}
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
			//哪个任务的关联关系
			taskMember.setPublicId(task.getTaskId());
			//添加参与者的头像
			taskMember.setMemberImg(userEntity[i].getUserInfo().getImage());
			//设置参与者id
			taskMember.setMemberId(userEntity[i].getId());
			//设置这条关系的创建时间
			taskMember.setCreateTime(System.currentTimeMillis());
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
        //循环删除任务成员关系
		for (int i = 0; i < userEntity.length; i++) {
			taskMemberMapper.delTaskMemberByTaskIdAndMemberId(task,userEntity[i]);
		}
		return null;
	}

	/**
	 * 删除多条关联信息
	 * @param task 任务信息
	 * @param userEntity 被移除的任务参与者信息
	 */
	@Override
	public void removeTaskMember(Task task, UserEntity userEntity) {
		taskMemberMapper.delTaskMemberByTaskIdAndMemberId(task,userEntity);
	}

	/**
	 * 删除一条关联信息 (必须是执行者)
	 * @param taskId 任务id
	 */
	@Override
	public void removeExecutor(String taskId) {
		taskMemberMapper.removeExecutor(taskId);
	}

	/**
	 * 查询当前成员在该任务下存不存在该任务参与者的记录
	 * @param memberId 成员nid
	 * @param taskId 任务id
	 * @return
	 */
	@Override
	public int findTaskMemberExecutorIsMember(String memberId, String taskId) {
		return taskMemberMapper.findTaskMemberExecutorIsMember(memberId,taskId);
	}

	/**
	 * 保存 任务参与者的关联信息
	 * @param taskMember
	 */
	@Override
	public void saveTaskMember(TaskMember taskMember) {
		taskMemberMapper.saveTaskMember(taskMember);
	}

	/**
	 * 查询一个任务下的所有参与者信息
	 * @param taskId 任务id
	 */
	@Override
	public List<TaskMember> findTaskMemberByTaskId(String taskId) {
		return taskMemberMapper.findTaskMemberByTaskId(taskId);
	}

	/**
	 * 查询此任务的关联任务
	 * @param taskId 此任务的id
	 * @return
	 */
	@Override
	public List<Task> findTaskRelationTask(String taskId) {
		List<TaskMember> list = taskMemberMapper.findTaskRelationTask(taskId);
		if(list != null && list.size() > 0){
			List<Task> taskList = new ArrayList<Task>();
			for (TaskMember taskMember : list) {
				taskList.add(taskService.findTaskByTaskId(taskMember.getPublicId()));
			}
			return taskList;
		}
		return null;
	}

	/**
	 * 查询关联的文件
	 * @param taskId 任务id
	 * @return
	 */
	@Override
	public List<File> taskRelationFile(String taskId) {
		List<TaskMember> list = taskMemberMapper.findTaskRelationTask(taskId);
		if(list != null && list.size() > 0){
			List<File> fileList = new ArrayList<File>();
			for (TaskMember taskMember : list) {
				fileList.add(fileService.findFileById(taskMember.getPublicId()));
			}
			return fileList;
		}
		return null;
	}

	/**
	 * 查询到该任务的所有参与者的信息(不保括执行者)
	 * @param taskId 任务id
	 * @param status 要查询的成员身份属于什么
	 * @param executorId 执行者的id
	 * @return
	 */
	@Override
	public List<UserEntity> findTaskMemberInfo(String taskId, String status,String executorId) {
        List<UserEntity> taskMemberInfo = userService.findTaskMemberInfo(taskId, status);
        if(taskMemberInfo != null && taskMemberInfo.size() > 0){
            for (UserEntity infoEntity : taskMemberInfo) {
                //如果任务的参与者集合中 存在任务执行者身份的参与者的话 则移除掉
                if(infoEntity.getId().equals(executorId)){
                    taskMemberInfo.remove(infoEntity);
                    break;
                }
            }
            if(taskMemberInfo.size() == 0){
                return null;
            }
        }
        return taskMemberInfo;
    }

	@Override
	public void delTaskMemberExecutor(String taskId) {
		taskMemberMapper.delTaskMemberExecutor(taskId);
	}
}