package com.art1001.supply.mapper.user;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.user.UserInfoEntity;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserMapper extends BaseMapper<UserEntity> {
	
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
	 * @param memberIds 逗号隔开的用户id
	 * @return
	 */
    List<UserEntity> findManyUserById(@Param("memberIds") String memberIds);

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
	List<UserEntity> findTaskMemberInfo(@Param("taskId") String taskId, @Param("status") String status);

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

	/**
	 * 查询该用户的详细信息
	 * @param uId 用户id
	 * @return
	 */
	UserInfoEntity findUserInfoByUserId(String uId);

	/**
	 * 查询出任务的创建者信息
	 * @param taskId 任务id
	 * @return
	 */
    UserEntity findTaskCreate(String taskId);

	/**
	 * 根据用户id查询出用户的信息
	 * @param uId 用户id
	 * @return
	 */
	UserEntity findUserById(String uId);

	/**
	 * 根据用户的id反向选择用户
	 * @param map 包含  项目id  和 用户的id
	 * @return
	 */
	List<UserEntity> reverseFindUser(Map<String,Object> map);

	//根据关键字模糊查询用户
	List<UserEntity> findByKey(String keyword);

	/**
	 * 根据用户id 查询出当前用户的名称
	 * @param uId 用户id
	 * @return 用户名称
	 */
	@Select("select u_name from tb_user where u_id = #{uId}")
    String findUserNameById(String uId);
}
