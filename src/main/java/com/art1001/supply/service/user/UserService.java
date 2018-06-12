package com.art1001.supply.service.user;



import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.entity.user.UserEntity;

import java.util.List;
import java.util.Map;

public interface UserService {

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
	 * @param memberId 用户id的数组
	 * @return
	 */
    List<UserEntity> findManyUserById(String[] memberId);
}