package com.art1001.supply.service.task;

import java.lang.reflect.Member;
import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskLogVO;
import com.art1001.supply.entity.task.TaskMember;
import com.art1001.supply.entity.user.UserEntity;
import org.apache.catalina.User;


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
	public void saveManyTaskeMmber(UserEntity[] member, Task task);

	/**
	 *
	 * @param member 多个成员信息
	 * @param task 任务实体信息
	 * @return
	 */
	int addManyMemberInfo(UserEntity[] member, Task task);

	/**
	 * 通过任务id 成员id 删除 任务成员关系表的数据
	 * @param task 任务实体信息
	 * @param userEntity 多个成员信息
	 * @return
	 */
	TaskLogVO delTaskMemberByTaskIdAndMemberId(Task task, UserEntity[] userEntity);
}