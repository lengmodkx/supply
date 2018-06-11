package com.art1001.supply.mapper.user;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.mapper.base.BaseMapper;
import com.art1001.supply.entity.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;
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
}
