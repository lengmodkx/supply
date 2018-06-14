package com.art1001.supply.mapper.task;

import java.lang.reflect.Member;
import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskMember;
import com.art1001.supply.entity.user.UserEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
	 * @return
     */
	int deleteTaskMemberById(String id);

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
	int saveTaskMember(TaskMember taskMember);

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

	/**
	 * 删除多条任务成员关系表中的成员信息
	 * @param task 当前任务的实体信息
	 * @param userEntity 多个参与者信息
	 * @return
	 */
	@Delete("delete from prm_task_member where current_task_id = #{task.taskId} and member_id = #{userEntity.id}")
    int delTaskMemberByTaskIdAndMemberId(@Param("task") Task task,@Param("userEntity")UserEntity userEntity);
}