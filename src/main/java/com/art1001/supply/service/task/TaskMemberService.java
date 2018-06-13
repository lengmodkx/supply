package com.art1001.supply.service.task;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskLogVO;
import com.art1001.supply.entity.task.TaskMember;


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


	public void saveManyTaskeMmber(String[] memberId,Task task);
	
}