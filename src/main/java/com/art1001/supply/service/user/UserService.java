package com.art1001.supply.service.user;



import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.user.UserInfoEntity;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<UserEntity> {

	public List<UserEntity> queryListByPage(Map<String, Object> parameter);

	public UserEntity findByName(String accountName);
	
	public int insert(UserEntity userEntity, String password);
	
	public UserEntity findById(String id);

	public int update(UserEntity userEntity);
	
	public int updateOnly(UserEntity userEntity) throws ServiceException;
	
	public int updatePassword(UserEntity userEntity, String password) throws ServiceException;
    
    public int deleteBatchById(List<String> userIds);

	/**
	 * 分页查询用户
	 */
	List<UserEntity> findListPager(Pager pager);

	/**
	 * 获取总条数
	 */
	int findCount();

	/**
	 * 根据id查询多个用户
	 * @param memberIds 用户id的数组
	 * @return
	 */
    List<UserEntity> findManyUserById(String memberIds);

	/**
	 * 根据项目id查找该项目下所有的成员信息
	 * @param projectId 项目编号
	 * @return
	 */
	List<UserEntity> findProjectAllMember(String projectId);

	/**
	 * 查询出项目下 存在该任务中的成员信息  和 不存在该任务中的成员信息
	 * @param task 任务实体信息
	 * @return
	 */
	public Map<String,List<UserEntity>> findUserByIsExistTask(Task task);

	/**
	 * 查询该任务下 除执行者外所有的参与者的详细信息
	 * @param taskId 任务id
	 * @param status 身份
	 * @return
	 */
    List<UserEntity> findTaskMemberInfo(String taskId, String status);

	/**
	 * 根据任务id 查询出该任务的执行者信息
	 * @param taskId 任务id
	 * @return
	 */
	UserEntity findExecutorByTask(String taskId);

	/**
	 * 根据用户id查询信息
	 * @param executor 用户id
	 * @return
	 */
	UserEntity findUserInfoById(String executor);

	/**
	 * 查询出任务的创建者信息
	 * @param taskId 任务的id
	 * @return
	 */
	UserEntity findTaskCreate(String taskId);

	/**
	 * 根据用户 id 查询出用户的信息
	 * @param uId 用户id
	 * @return
	 */
	UserEntity findUserById(String uId);

	/**
	 * 根据用户的id反向选择用户
	 * @param projectId
	 * @return
	 */
	List<UserEntity> reverseFindUser(String projectId,String[] uId);

	//根据关键字模糊查询用户
	List<UserEntity> findByKey(String keyword);

	/**
	 * 根据用户id 查询出当前用户的名称
	 * @param uId 用户id
	 * @return 用户名称
	 */
    String findUserNameById(String uId);
}