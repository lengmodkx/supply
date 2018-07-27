package com.art1001.supply.service.user.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.mapper.user.UserNewsMapper;
import com.art1001.supply.service.user.UserNewsService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.user.UserNews;

/**
 * ServiceImpl
 */
@Service
public class UserNewsServiceImpl implements UserNewsService {

	/** Mapper接口*/
	@Resource
	private UserNewsMapper userNewsMapper;
	
	/**
	 * 查询分页数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<UserNews> findUserNewsPagerList(Pager pager){
		return userNewsMapper.findUserNewsPagerList(pager);
	}

	/**
	 * 通过id获取单条数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public UserNews findUserNewsById(String id){
		return userNewsMapper.findUserNewsById(id);
	}

	/**
	 * 通过id删除数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteUserNewsById(String id){
		userNewsMapper.deleteUserNewsById(id);
	}

	/**
	 * 修改数据
	 * 
	 * @param userNews
	 */
	@Override
	public void updateUserNews(UserNews userNews){
		userNewsMapper.updateUserNews(userNews);
	}
	/**
	 * 保存数据
	 * 
	 * @param userNews
	 */
	@Override
	public void saveUserNews(UserNews userNews){
		userNewsMapper.saveUserNews(userNews);
	}
	/**
	 * 获取所有数据
	 * 
	 * @return
	 */
	@Override
	public List<UserNews> findUserNewsAllList(){
		return userNewsMapper.findUserNewsAllList();
	}
	
}