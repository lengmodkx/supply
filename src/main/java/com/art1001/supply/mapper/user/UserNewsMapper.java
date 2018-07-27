package com.art1001.supply.mapper.user;

import java.util.List;
import com.art1001.supply.entity.user.UserNews;
import com.art1001.supply.entity.base.Pager;
import org.apache.ibatis.annotations.Mapper;

/**
 * mapper接口
 */
public interface UserNewsMapper {

	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<UserNews> findUserNewsPagerList(Pager pager);

	/**
	 * 通过id获取单条数据
	 * 
	 * @param id
	 * @return
	 */
	UserNews findUserNewsById(String id);

	/**
	 * 通过id删除数据
	 * 
	 * @param id
	 */
	void deleteUserNewsById(String id);

	/**
	 * 修改数据
	 * 
	 * @param userNews
	 */
	void updateUserNews(UserNews userNews);

	/**
	 * 保存数据
	 * 
	 * @param userNews
	 */
	void saveUserNews(UserNews userNews);

	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	List<UserNews> findUserNewsAllList();

}