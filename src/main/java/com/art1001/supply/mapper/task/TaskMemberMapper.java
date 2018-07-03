package com.art1001.supply.mapper.task;

import java.lang.reflect.Member;
import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskMember;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserInfoEntity;
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
	 * 删除任务成员关系表中的成员信息
	 * @param task 当前任务的实体信息
	 * @param userEntity 多个参与者信息
	 * @return
	 */
	@Delete("delete from prm_task_member where public_id = #{task.taskId} and member_id = #{userEntity.id} and type != '创建者'")
    int delTaskMemberByTaskIdAndMemberId(@Param("task") Task task,@Param("userEntity")UserEntity userEntity);

	/**
	 * 删除任务成员关系表中的成员信息 (删除的是执行者)
	 * @param taskId 任务id
	 */
	@Delete("delete from prm_task_member where current_task_id = #{taskId} and type = '执行者'")
	void removeExecutor(@Param("taskId") String taskId);

	/**
	 * 查询这个执行者 之前是不是 该任务的参与者
	 * @param memberId 参与者的id
	 * @param taskId 任务的id
	 * @return
	 */
    int findTaskMemberExecutorIsMember(@Param("memberId") String memberId, @Param("taskId") String taskId);

	/**
	 * 寻找一个任务下的所有参与者关系信息
	 * @param taskId 寻找一个任务下的所有成员信息
	 */
	List<TaskMember> findTaskMemberByTaskId(String taskId);

	/**
	 * 查询此任务的所有关联任务
	 * @param taskId 任务id
	 * @return
	 */
	List<TaskMember> findTaskRelationTask(String taskId);

	/**
	 * 清空任务的关联
	 * @param taskId 任务id
	 *
	 */
	@Delete("delete from prm_task_member where current_task_id = #{taskId}")
	void clearTaskMemberByTaskId(String taskId);

	/**
	 * 删除一个任务的执行者
	 * @param taskId 任务id
	 */
    void delTaskMemberExecutor(String taskId);

	/**
	 * 查询该任务的执行者信息
	 * @param taskId 任务id
	 */
	void findTaskExecutor(String taskId);
}