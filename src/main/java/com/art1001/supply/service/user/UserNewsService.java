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
	
}