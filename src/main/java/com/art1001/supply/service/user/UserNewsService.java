package com.art1001.supply.service.user;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.user.UserNews;

/**
 * Service接口
 */
public interface UserNewsService {

	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<UserNews> findUserNewsPagerList(Pager pager);

	/**
	 * 通过id获取单条数据
	 * 
	 * @param id
	 * @return
	 */
	public UserNews findUserNewsById(String id);

	/**
	 * 通过id删除数据
	 * 
	 * @param id
	 */
	public void deleteUserNewsById(String id);

	/**
	 * 修改数据
	 * 
	 * @param userNews
	 */
	public void updateUserNews(UserNews userNews);

	/**
	 * 保存数据
	 * 
	 * @param userNews
	 */
	public void saveUserNews(UserNews userNews);

	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	public List<UserNews> findUserNewsAllList();

	/**
	 * 根据 信息id 和用户id  查询 用户有没有这消息的记录
	 * @param publicId 信息id
	 * @param userId 用户id
	 * @return
	 */
    int findUserNewsByPublicId(String publicId, String userId);

	/**
	 * 根据信息id 用户id 查询出 这条信息的消息数
	 * @param publicId 信息id
	 * @param userId 用户id
	 * @return 消息数
	 */
	Integer findNewsCountByPublicId(String publicId, String userId);

	/**
	 * 根据用户id  查询出用户的未读消息条数
	 * @param userId 用户id
	 * @return
	 */
	int findUserNewsCount(String userId);

	/**
	 * 根据用户id 查询出该用户的 全部消息
	 * @param userId 用户id
	 * @return
	 */
	List<UserNews> findAllUserNewsByUserId(String userId);

	/**
	 *	保存用户的消息信息
	 * @param users 受影响的用户数组
	 * @param publicId 哪条信息的消息
	 * @param publicType 信息的类型(任务,文件,日程,分享)
	 * @param content 消息内容
	 * @param isChat 是否是聊天信息
	 */
	void saveUserNews(String[] users, String publicId, String publicType, String content, int isChat);

}