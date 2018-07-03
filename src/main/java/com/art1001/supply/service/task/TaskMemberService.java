package com.art1001.supply.service.task;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskLogVO;
import com.art1001.supply.entity.task.TaskMember;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserInfoEntity;
import org.apache.ibatis.annotations.Delete;


/**
 * taskMemberService接口
 */
public interface TaskMemberService {

	/**
	 * 查询分页taskMember数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<TaskMember> findTaskMemberPagerList(Pager pager);

	/**
	 * 通过id获取单条taskMember数据
	 * 
	 * @param id
	 * @return
	 */
	public TaskMember findTaskMemberById(String id);

	/**
	 * 通过id删除taskMember数据
	 * 
	 * @param id
	 */
	public TaskLogVO deleteTaskMemberById(Task task,File file,Share share,Schedule schedule,String taskId,String id);

	/**
	 * 修改taskMember数据
	 * 
	 * @param taskMember
	 */
	public void updateTaskMember(TaskMember taskMember);

	/**
	 * 保存taskMember数据
	 * 
	 * @param taskMember
	 */
	public TaskLogVO saveTaskMember(Task task, File file, Share share,Schedule schedule,TaskMember taskMember,String taskId);

	/**
	 * 获取所有taskMember数据
	 * 
	 * @return
	 */
	public List<TaskMember> findTaskMemberAllList();


	/**
	 * 在任务关系表里添加多条成员信息
	 * @param member 多个成员信息
	 * @param task 任务实体信息
	 */
	public void saveManyTaskeMmber(String[] member, Task task);

	/**
	 * 添加任务成员关系
	 * @param taskMember
	 */
	public void saveTaskMember(TaskMember taskMember);

	/**
	 * 通过任务id 成员id 删除 任务成员关系表的数据
	 * @param task 任务实体信息
	 * @param userEntity 多个成员信息
	 * @return
	 */
	TaskLogVO delTaskMemberByTaskIdAndMemberId(Task task, UserEntity[] userEntity);

	/**
	 * 移除单个任务参与者
	 * @param task 任务信息
	 * @param userEntity 被移除的任务参与者信息
	 * @return
	 */
	void removeTaskMember(Task task, UserEntity userEntity);

	/**
	 * 删除一条关联信息
	 * @param taskId 任务id
	 */
    void removeExecutor(String taskId);

	/**
	 * 添加多个参与者信息
	 * @param addUserEntity 添加的用户数组信息
	 * @param task 任务信息
	 */
	int addManyMemberInfo(UserEntity[] addUserEntity, Task task);

	/**
	 * 查询 指定任务下的指定成员有没有以任务参与者的方式出现
	 * @param memberId 成员nid
	 * @param taskId 任务id
	 */
	int findTaskMemberExecutorIsMember(String memberId, String taskId);

	/**
	 * 查询一个任务下所有的参与者的关系信息
	 * @param taskId 任务id信息
	 *
	 */
	List<TaskMember> findTaskMemberByTaskId(String taskId);



	/**
	 * 查询此任务的关联id
	 * @param taskId 此任务的id
	 * @return
	 */
	List<Task> findTaskRelationTask(String taskId);

	/**
	 * 查询此任务关联的文件
	 * @param taskId 任务id
	 * @return 任务id
	 */
	List<File> taskRelationFile(String taskId);

	/**
	 * 查询到该任务的所有参与者的"基本信息" (不保括执行者)
	 *
	 * @param taskId 任务id
	 * @param status 要查询的成员身份属于什么
	 * @param executorId 任务执行者的id
	 * @return
	 */
	List<UserEntity> findTaskMemberInfo(String taskId, String status,String executorId);

	/**
	 * 清空该人的成员关联信息
	 * @param taskId
	 */
	void clearTaskMemberByTaskId(String taskId);

	/**
	 * 删除一个任务下的执行者信息
	 * @param taskId 任务id
	 */
	void delTaskMemberExecutor(String taskId);
}