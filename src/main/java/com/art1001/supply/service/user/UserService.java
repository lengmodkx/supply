package com.art1001.supply.service.user;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.ServiceException;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<UserEntity> {

	public List<UserEntity> queryListByPage(Map<String, Object> parameter);

	public UserEntity findByName(String accountName);
	
	public void insert(UserEntity userEntity, String password);
	
	public UserEntity findById(String id);
	
	public int updatePassword(UserEntity userEntity, String password) throws ServiceException;

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


	//根据关键字模糊查询用户
	List<UserEntity> findByKey(String keyword);

	/**
	 * 查询出某个项目下的所有成员信息
	 * @param projectId 项目id
	 * @return
	 */
    List<UserEntity> getProjectMembers(String projectId);
}