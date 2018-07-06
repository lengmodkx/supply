package com.art1001.supply.mapper.task;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * taskmapper接口
 */
@Mapper
public interface TaskMapper {

	/**
	 * 根据任务id数组查找多个任务
	 * @param taskId 任务id数组
	 * @return
	 */
	List<Task> findManyTask(String[] taskId);

    /**
	 * 查询分页task数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Task> findTaskPagerList(Pager pager);

	/**
	 * 通过taskId获取单条task数据
	 * 
	 * @param taskId
	 * @return
	 */
	Task findTaskByTaskId(String taskId);

	/**
	 * 删除任务
	 * 通过taskId删除task数据
	 * @param taskId 任务id
	 */
	int deleteTaskByTaskId(String taskId);

	/**
	 * 修改task数据
	 * 
	 * @param task
	 */
	int updateTask(Task task);

	/**
	 * 保存task数据
	 * 
	 * @param task
	 */
	int saveTask(Task task);

	/**
	 * 获取所有task数据
	 * 
	 * @return
	 */
	List<Task> findTaskAllList();

	/**
	 * 移入回收站/恢复任务
	 * @param taskId 任务id
	 * @param updateTime 更新时间
	 * @return
	 */
    int moveToRecycleBin(@Param("taskId") String taskId, @Param("updateTime") long updateTime);

	/**
	 * 移入回收站/恢复任务(适用于和分组一起移入回收站的任务)
	 * @param taskId 任务id
	 * @param taskDel 任务是否在回收站
	 * @param updateTime 更新时间
	 * @return
	 */
	int groupTaskmoveToRecycleBin(@Param("taskId") String taskId, @Param("taskDel") String taskDel, @Param("updateTime") long updateTime);

	/**
	 * 修改当前任务状态
	 * @param taskId 当前任务id
	 * @param updateTime 当前时间毫秒数
     * @return
	 */
	int changeTaskStatus(@Param("taskId") String taskId, @Param("taskStatus") String taskStatus, @Param("updateTime") long updateTime);

	/**
	 * 判断当前菜单有没有任务
	 * @param taskMenuId 菜单id
	 * @return
	 */
    int findTaskByMenuId(String taskMenuId);

	/**
	 * 根据任务id查找出该任务的所有标签
	 * @param taskId 任务id
	 * @return
	 */
	String findTaskTagByTaskId(String taskId);

	/**
	 * 清空任务开始时间
	 * @param task
	 * @return
	 */
	@Update("update prm_task set start_time = null,update_time = #{updateTime} where task_id = #{taskId}")
    int removeTaskStartTime(Task task);

	/**
	 * 清空任务结束时间
	 * @param task
	 * @return
	 */
	@Update("update prm_task set end_time = null,update_time = #{updateTime} where task_id = #{taskId}")
	int removeTaskEndTime(Task task);

	/**
	 * 查找某个任务下的子任务
	 * @param taskId 父任务id
	 * @return
	 */
	List<Task> findSubLevelTask(String taskId);

	/**
	 * 更改当前任务的隐私模式
	 * @param task
	 * @return
	 */
    int SettingUpPrivacyPatterns(Task task);

	/**
	 * 根据任务的状态查询多条任务信息
	 * @param status
	 * @return
	 */
	List<Task> findTaskByStatus(@Param("status") String status,@Param("projectId")String projectId);

	/**
	 * 查询今天的任务信息
	 * @return
	 */
	List<Task> findTaskByToday(@Param("projectId")String projectId);

	/**
	 * 根据任务菜单查询任务信息
	 * @param menuId
	 * @return
	 */
    List<Task> taskMenu(@Param("menuId") String menuId);

	/**
	 * 查询某个人执行的所有任务
	 * @param uId 执行者的id
	 * @param projectId 项目的id
	 * @return
	 */
	List<Task> findTaskByExecutor(@Param("uId") String uId,@Param("projectId") String projectId);

	/**
	 * 查询该项目下所有未被认领的任务
	 * @return
	 */
    List<Task> waitClaimTask(@Param("projectId") String projectId);

	/**
	 * 将该任务的执行者清除掉
	 * @param taskId 任务id
	 * @return
	 */
	int removeExecutor(@Param("taskId") String taskId);

	/**
	 * 清空该任务下的所有成员信息
	 * @param taskId 任务的id
	 */
	void clearTaskMember(@Param("taskId") String taskId);

	/**
	 * 查询当前子任务的父级任务的项目id
	 * @param taskId 父任务id
	 * @return
	 */
	Task findFatherLevelProjectId(String taskId);

	/**
	 * 查询项目下所有任务信息
	 * @param projectId 项目id
	 * @return
	 */
	List<Task> findTaskByProject(@Param("projectId") String projectId);

	/**
	 * 查询项目下的指定的优先级的任务
	 * @param projectId 项目id
	 * @param priority 优先级别
	 * @return
	 */
	List<Task> findTaskByPriority(@Param("projectId") String projectId,@Param("priority") String priority);

	/**
	 * 查询任务下的所有子任务
	 * @param taskId 任务id
	 * @return
	 */
	List<Task> findTaskByFatherTask(String taskId);

	/**
	 * 恢复任务的功能
	 * @param taskId 任务的id
	 * @param menuId 恢复后放到哪个菜单
	 * @param updateTime 更新时间
	 * @param projectId 项目id
	 */
	void recoverTask(@Param("taskId") String taskId, @Param("menuId") String menuId,@Param("updateTime") Long updateTime,@Param("projectId") String projectId);

	/**
	 * 查询某个菜单下的所有任务的信息 不包括执行者信息
	 * @param menuId
	 * @return
	 */
    List<Task> simpleTaskMenu(String menuId);

	/**
	 * 根据任务的id 查询出任务的创建者id
	 * @param taskId 任务的id
	 * @return
	 */
	@Select("select member_id from prm_task where task_id = #{taskId}")
	String findTaskMemberIdByTaskId(String taskId);

	/**
	 * 清空任务的执行者
	 * @param taskId 任务id
	 */
	@Update("update prm_task set executor = '' where task_id = #{taskId}")
    void clearExecutor(String taskId);

	/**
	 * 排序一组任务菜单的任务序号
	 * @param taskId 任务id数组
	 * @param order 任务的序号
	 */
	@Update("update prm_task set `order` = #{order} where task_id = #{taskId} ")
    void orderOneTaskMenu(@Param("taskId") String taskId, @Param("order") int order);

	/**
	 * 根据子任务id 查询出父任务信息
	 * @param taskId 子任务id
	 * @return
	 */
	Task findTaskBySubTaskId(String taskId);
}