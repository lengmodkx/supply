package com.art1001.supply.mapper.user;

import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserInfo;
import com.art1001.supply.entity.user.UserVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserMapper extends BaseMapper<UserEntity> {

	/**
	 * 查询一个用户信息
	 * @param memberId 用户id
	 * @return
	 */
	UserEntity findById(String memberId);

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
	 * 根据用户 id  查询出用户的信息(不保括角色信息)
	 * @param id 用户id
	 * @return
	 */
	UserEntity findUserInfoById(String id);

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

	UserEntity findByName(String username);

	/**
	 * 查询出某个项目下的所有成员信息
	 * @param projectId 项目id
	 * @return
	 */
    List<UserEntity> selectProjectMembers(String projectId);

    List<String> getAllUserId();

	List<String> getPhoneList();

	//查询部分用户信息
	UserInfo findInfo(String accountName);


	/**
	 * 通过电话或姓名搜索所有企业成员
	 * @param orgId 企业id
	 * @param phone 电话或姓名信息
	 * @return
	 */
	List<UserEntity> getUserByOrgId(@Param("phone")String phone, @Param("orgId")String orgId);


	@Select("select user_name,password from tb_user where delete_status=0")
    List<UserEntity> selectAll();

    UserVO getHeadUserInfo(@Param("userId") String userId);


}
