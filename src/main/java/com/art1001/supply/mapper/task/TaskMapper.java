package com.art1001.supply.mapper.task;

import java.util.LinkedList;
import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskCollect;
import com.art1001.supply.entity.template.TemplateData;
import org.apache.ibatis.annotations.*;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.stereotype.Service;

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

	void saveTaskBatch(@Param("projectId") String projectId, @Param("menuId") String menuId, @Param("templateDataList") List<TemplateData> templateDataList,@Param("memberId")String memberId);
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
    int settingUpPrivacyPatterns(Task task);

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
	 * @param orderType 排序类型
	 * @return
	 */
	List<Task> findTaskByExecutor(@Param("uId") String uId,@Param("orderType") String orderType);

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
	 * 查询项目下所有指定执行者任务信息 (只要未完成)
	 * @param memberId 当前用户id
	 * @param projectId 项目id
	 * @return
	 */
	List<Task> findTaskByProjectAndStatusAndExecutor(@Param("projectId")String projectId,@Param("memberId") String memberId);

	/**
	 * 查询项目下所有指定参与者任务信息 (只要未完成)
	 * @param memberId 成员id
	 * @param projectId 项目id
	 * @return
	 */
	List<Task> findTaskByProjectAndStatusAndUser(@Param("projectId")String projectId,@Param("memberId") String memberId);

	/**
	 * 查询项目下所有指定参与者任务信息 (只要未完成)
	 * @param projectId 项目id
	 * @param memberId 当前用户id
	 * @return
	 */
	List<Task> findTaskByProjectAndStatusAndCreateMember(@Param("projectId")String projectId,@Param("memberId") String memberId);
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

	/**
	 * 根据任务id 查询出任务的标签id
	 * @param taskId 任务id
	 * @return
	 */
	@Select("select tag_id from prm_task where task_id = #{taskId}")
    String findTagIdByTaskId(String taskId);

	/**
	 * 清空该任务的标签
	 * @param taskId 任务的id
	 * @return
	 */
	int clearTaskTag(String taskId);

	/**
	 * 查询该任务的得赞数量
	 * @param taskId 任务id
	 * @return
	 */
	@Select("select ifnull(fabulous_count,0) from prm_task where task_id = #{taskId}")
	Integer findTaskFabulousCount(String taskId);

	/**
	 * 查询出该用户
	 * @param userId 当前用户的id
	 * @return
	 */
	List<Task> findTaskByUserIdAndByTreeDay(String userId);

	/**
	 * 查询出我创建的任务
	 * @param memberId 用户id
	 * @return
	 */
	List<Task> findTaskByMemberId(@Param("memberId") String memberId);

	/**
	 * 查询出我参与的任务
	 * @param id 当前用户id
	 * @param orderType 排序类型
	 * @return
	 */
	List<Task> findTaskByUserId(@Param("id") String id,@Param("orderType")String orderType);

	/**
	 * 查询出该用户执行的 已经完成的任务
	 * @param id 用户id
	 * @return
	 */
	List<Task> findTaskByExecutorAndStatus(String id);

	/**
	 * 查询出该用所参与的所有任务(按照任务的状态)
	 * @param id 用户id
	 * @param status 任务的状态
	 * @return
	 */
	List<Task> findTaskByUserIdByStatus(@Param("uId") String id,@Param("status") String status);

	/**
	 * 查询出任务的
	 * @param id 用户的id
	 * @param orderType 任务的排序类型
	 * @return
	 */
	List<Task> findTaskByUserAndTime(@Param("id") String id,@Param("orderType") String orderType);

	/**
	 * 查询出该用户创建的任务 (根据任务状态查询)
	 * @param id 用户id
	 * @param status 任务的状态
	 * @return
	 */
	List<Task> findTaskByCreateMemberByStatus(@Param("uId") String id,@Param("status") String status);

	/**
	 * 查询出我创建的任务  (只要未完成的)
	 * @param id 用户id
	 * @param orderType 排序类型
	 * @return
	 */
	List<Task> findTaskByCreateMember(@Param("id") String id,@Param("orderType") String orderType);

	/**
	 * 查询出用户创建的所有任务并且按照时间排序
	 * @param id 用户的id
	 * @param orderType 排序类型
	 * @return
	 */
	List<Task> findTaskByCreateMemberAndTime(@Param("uId") String id,@Param("orderType") String orderType);

	/**
	 * 查询任务的名称
	 * @param taskId 任务id
	 * @return 任务名称
	 */
	@Select("select task_name from prm_task where task_id = #{taskId}")
    String findTaskNameById(String taskId);

	/**
	 * 根据任务id 查询出任务的层级
	 * @param taskId 任务id
	 * @return
	 */
	@Select("select level from prm_task where task_id = #{taskId}")
    Integer findTaskLevelById(String taskId);

	/**
	 * 根据用户查询出该用户执行的和创建的附有日历日期的所有任务
	 * @param uId 用户
	 * @return
	 */
    List<Task> findTaskByCalendar(String uId);

	/**
	 * 根据id 查询名称
	 * @param taskId 任务的id
	 * @return 任务名称字段信息
	 */
	@Select("select task_name from prm_task where task_id = #{taskId}")
	String getTaskNameById(String taskId);

	/**
	 * 查询子任务的数量/子任务完成或者未完成的数量
	 * @param taskId
	 * @param taskStatus
	 * @return
	 */
	int findSubTaskCount(@Param("taskId")String taskId,@Param("taskStatus")String taskStatus);

	/**
	 * 取消收藏任务
	 * @param taskId 任务id
	 * @param uId 用户id
	 *
	 */
	@Delete("delete from prm_public_collect where public_id = #{taskId} and member_id = #{uId}")
    void cancleCollectTask(@Param("taskId") String taskId, @Param("uId") String uId);

	/**
	 * 查询出该项目下的任务总数
	 * @param projectId 项目的id
	 * @return 任务数量
	 */
	@Select("select count(0) from prm_task where project_id = #{projectId}")
    int findTaskTotalByProjectId(String projectId);

	/**
	 * 查询某个项目下未完成的任务数量
	 * @param projectId 项目id
	 * @return
	 */
	@Select("select count(0) from prm_task where project_id = #{projectId} and task_status = '未完成'")
	int findHangInTheAirTaskCount(String projectId);

	/**
	 * 查询某个项目下完成的任务数量
	 * @param projectId 项目id
	 * @return
	 */
	@Select("select count(0) from prm_task where project_id = #{projectId} and task_status = '完成'")
	int findCompletedTaskCount(String projectId);

	/**
	 * 查询某个项目下 今日到期的任务
	 * @param projectId 项目id
	 * @param currDate 当前日期 格式为 yyyy-MM-dd
	 * @return
	 */
	@Select("select count(0) from prm_task where project_id = #{projectId} and (SELECT FROM_UNIXTIME(prm_task.end_time/1000, '%Y-%m-%d')) = (SELECT FROM_UNIXTIME(#{currDate}/1000, '%Y-%m-%d'))")
	int currDayTaskCount(@Param("projectId") String projectId, @Param("currDate") Long currDate);

	/**
	 * 查询某个项目下 已逾期的任务
	 * @param projectId 项目id
	 * @param currDate 当前日期 格式为 yyyy-MM-dd
	 * @return
	 */
	@Select("select count(0) from prm_task where project_id = #{projectId} and (SELECT FROM_UNIXTIME(prm_task.end_time/1000, '%Y-%m-%d')) < (SELECT FROM_UNIXTIME(#{currDate}/1000, '%Y-%m-%d')) and task_status = '未完成'")
	int findBeoberdueTaskCount(@Param("projectId") String projectId, @Param("currDate") Long currDate);

	/**
	 * 查询出该项目下 的所有待认领的任务
	 * @param projectId 项目id
	 * @return
	 */
	@Select("select count(0) from prm_task where project_id = #{projectId} and (executor = '' or executor is null)")
	int findTobeclaimedTaskCount(String projectId);

	/**
	 * 查询出该项目下 按时完成的所有任务
	 * @param projectId 项目id
	 * @param currDate 当前日期 格式为 yyyy-MM-dd
	 * @return
	 */
	@Select("select count(0) from prm_task where project_id = #{projectId} and (SELECT FROM_UNIXTIME(prm_task.end_time/1000, '%Y-%m-%d')) >= (SELECT FROM_UNIXTIME(#{currDate}/1000, '%Y-%m-%d')) and task_status = '完成'")
	int findFinishontTimeTaskCount(@Param("projectId") String projectId, @Param("currDate") Long currDate);

	/**
	 * 查询出该项目下 按时完成的所有任务
	 * @param projectId 项目id
	 * @param currDate 当前日期 格式为 yyyy-MM-dd
	 * @return
	 */
	@Select("select count(0) from prm_task where project_id = #{projectId} and (SELECT FROM_UNIXTIME(prm_task.end_time/1000, '%Y-%m-%d')) < (SELECT FROM_UNIXTIME(#{currDate}/1000, '%Y-%m-%d')) and task_status = '完成'")
	int findOverdueCompletion(@Param("projectId")String projectId, @Param("currDate") Long currDate);

	/**
	 * 根据菜单的parent 查询出该菜单的分组id
	 * @param menuParent 菜单的分组id
	 * @return
	 */
	@Select("select relation_id relationId,relation_name relationName from prm_relation where relation_id = #{menuParent}")
    Relation findTaskGroupInfoByTaskMenuId(String menuParent);
}