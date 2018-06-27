package com.art1001.supply.mapper.user;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.user.UserInfoEntity;
import com.art1001.supply.mapper.base.BaseMapper;
import com.art1001.supply.entity.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper extends BaseMapper<UserEntity, String> {
	
	/**
	 * 添加用户和角色对应关系
	 * @param userEntity
	 * @return
	 */
	public int insertUserRole(UserEntity userEntity);
	
	/**
	 * 更新用户和角色对应关系
	 * @param userEntity
	 * @return
	 */
	public int updateUserRole(UserEntity userEntity);
	
	/**
	 * 删除用户和角色对应关系
	 * @param userIds
	 * @return
	 */
	public int deleteBatchUserRole(List<String> userIds);
	
	/**
	 * 添加用户个人资料信息
	 * @param userEntity
	 * @return
	 */
	public int insertUserInfo(UserEntity userEntity);
	
	/**
	 * 更新用户个人资料信息
	 * @param userEntity
	 * @return
	 */
	public int updateUserInfo(UserEntity userEntity);

	/**
	 * 分页查询用户列表
	 */
    List<UserEntity> findListPager(Pager pager);

	int findCount();

	/**
	 * 查询多个指定id的用户
	 * @param memberId 用户id的数组
	 * @return
	 */
    List<UserEntity> findManyUserById(String[] memberId);

	/**
	 * 根据项目id 查询该项目下所有的用户信息
	 * @param projectId 项目id
	 * @return
	 */
	List<UserEntity> findProjectAllMember(String projectId);

	/**
	 * 查询存在于该任务中的成员信息
	 * @param task
	 * @return
	 */
	List<UserEntity> findUserByExistTask(Task task);

	/**
	 * 查询不存在于该任务中的成员信息
	 * @param task
	 * @return
	 */
	List<UserEntity> findUserByNotExistTask(Task task);

	/**
	 * 查询到该任务的成员信息
	 *
	 * @param taskId 任务id
	 * @param status 要查询的成员身份属于什么
	 * @return
	 */
	List<UserInfoEntity> findTaskMemberInfo(@Param("taskId") String taskId, @Param("status") String status);

	/**
	 * 根据任务id 查询出任务下的执行者信息
	 * @param taskId 任务id
	 * @return
	 */
	UserEntity findExecutorByTask(String taskId);

	/**
	 * 根据用户 id  查询出用户的信息(不保括角色信息)
	 * @param id 用户id
	 * @return
	 */
	UserEntity findUserInfoById(String id);
}
