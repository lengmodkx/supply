package com.art1001.supply.mapper.task;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.TaskMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * taskMembermapper接口
 */
@Mapper
public interface TaskMemberMapper {

	/**
	 * 查询分页taskMember数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<TaskMember> findTaskMemberPagerList(Pager pager);

	/**
	 * 通过id获取单条taskMember数据
	 * 
	 * @param id
	 * @return
	 */
	TaskMember findTaskMemberById(String id);

	/**
	 * 通过id删除taskMember数据
	 * 
	 * @param id
	 */
	void deleteTaskMemberById(String id);

	/**
	 * 修改taskMember数据
	 * 
	 * @param taskMember
	 */
	void updateTaskMember(TaskMember taskMember);

	/**
	 * 保存taskMember数据
	 * 
	 * @param taskMember
	 */
	void saveTaskMember(TaskMember taskMember);

	/**
	 * 获取所有taskMember数据
	 * 
	 * @return
	 */
	List<TaskMember> findTaskMemberAllList();

	/**
	 * 保存多条任务和参与者关系的数据
	 * @param taskMembers
	 * @return
	 */
    int saveManyTaskMember(List<TaskMember> taskMembers);
}