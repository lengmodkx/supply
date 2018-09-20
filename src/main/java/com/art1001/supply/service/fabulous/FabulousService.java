package com.art1001.supply.service.fabulous;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.fabulous.Fabulous;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 得赞Service接口
 */
public interface FabulousService extends IService<Fabulous> {

	/**
	 * 查询分页得赞数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<Fabulous> findFabulousPagerList(Pager pager);

	/**
	 * 通过fabulousId获取单条得赞数据
	 * 
	 * @param fabulousId
	 * @return
	 */
	public Fabulous findFabulousByFabulousId(String fabulousId);

	/**
	 * 通过fabulousId删除得赞数据
	 * 
	 * @param fabulousId
	 */
	public void deleteFabulousByFabulousId(String fabulousId);

	/**
	 * 修改得赞数据
	 * 
	 * @param fabulous
	 */
	public void updateFabulous(Fabulous fabulous);

	/**
	 * 获取所有得赞数据
	 * 
	 * @return
	 */
	public List<Fabulous> findFabulousAllList();

	/**
	 * @param publicId 信息id
	 * 删除某个信息的所有赞
	 */
	void deleteFabulousByInfoId(String publicId);

	/**
	 * @param publicId 信息id
	 * 删除多个信息的所有赞
	 */
	void deleteManyFabulousByInfoId(List<String> publicId);
	
}