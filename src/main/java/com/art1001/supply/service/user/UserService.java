package com.art1001.supply.service.user;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.entity.user.UserInfo;
import com.art1001.supply.entity.user.WeChatUser;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.wechat.login.dto.UpdateUserInfoRequest;
import com.art1001.supply.wechat.login.dto.WeChatDecryptResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserService extends IService<UserEntity> {

	public List<UserEntity> queryListByPage(Map<String, Object> parameter);

	public UserEntity findByName(String accountName);

	//查询部分用户信息
	UserInfo findInfo(String accountName);

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

	/**
	 * 注册微信用户到数据库
	 * @param snsUserInfo
	 * @return
	 */
	Map saveWeChatUserInfo(WeChatUser snsUserInfo);

	/**
	 * 根据用户名查询该用户存不存在
	 * 如果accountName为空,返回false
	 * @author heShaoHua
	 * @describe 暂无
	 * @param accountName 登录用户名
	 * @updateInfo 暂无
	 * @date 2019/6/11 11:01
	 * @return 结果
	 */
	Boolean checkUserIsExistByAccountName(String accountName);

	/**
	 * 根据id集合信息查询出用户信息
	 * @author heShaoHua
	 * @describe 暂无
	 * @param idList 用户id集合
	 * @updateInfo 暂无
	 * @date 2019/8/14 15:19
	 * @return 用户信息集合
	 */
	List<UserEntity> getUserListByIdList(Collection<String> idList);

    void resetPassword(String accountname, String newPassword);

	/**
	 * 绑定手机号
	 * @param phone 手机号
	 * @param code code码
	 * @param nickName 用户昵称
	 */
	void bindPhone(String phone, String code, String userId, String nickName);

	/**
	 * 根据 id accountName 去查询
	 * @param keyword 关键字
	 * @return 记录数
	 */
	int checkUserIsExist(String keyword);

    List<String> getAllUserId();

	List<String> getPhoneList();

	/**
	 * 保存小程序用户信息
	 * @param res 解密后的小程序用户信息
	 * @return 存储后的用户信息
	 */
	UserEntity saveWeChatAppUserInfo(WeChatDecryptResponse res);

	/**
	 * 手机号绑定微信
	 * @param snsUserInfo 微信用户信息
	 * @param accountName 手机号或者邮箱
	 */
    void bindWeChat(WeChatUser snsUserInfo, String accountName);

	/**
	 * 根据用户id查询出该用户对应的小程序openId
	 * @param userId 用户id
	 * @return 小程序openid
	 */
	String getAppOpenIdByUserId(String userId);
}