package com.art1001.supply.service.binding;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.binding.Binding;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * bindingService接口
 */
public interface BindingService extends IService<Binding> {

	/**
	 * 查询分页binding数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<Binding> findBindingPagerList(Pager pager);

	/**
	 * 通过id获取单条binding数据
	 * 
	 * @param id
	 * @return
	 */
	public Binding findBindingById(String id);

	/**
	 * 通过id删除binding数据
	 *
	 * @param id 当前绑定信息的id
	 * @param bindingId 被绑定信息的id
	 */
	public void deleteBindingById(String id, String bindingId);

	/**
	 * 修改binding数据
	 * 
	 * @param binding
	 */
	public void updateBinding(Binding binding);

	/**
	 * 保存binding数据
	 * 
	 * @param binding
	 */
	public void saveBinding(Binding binding);

	/**
	 * 获取所有binding数据
	 * 
	 * @return
	 */
	public List<Binding> findBindingAllList();

	/**
	 * 查询要关联的信息  在库中存不存在
	 * @param publicId 关联的信息id
	 * @param bindId 被关联的信息id数组
	 * @return 库中的记录行数
	 */
	String[] getBindingRecord(String publicId, String[] bindId);

	/**
	 * 查询出该信息关联的所有信息
	 * @param publicId 关联信息id
	 * @return
	 */
    List<Binding> findBindingInfoByPublic(String publicId);

	/**
	 * 保存多条 关联信息
	 * @param newBindingList 关联信息的集合
	 */
	void saveMany(List<Binding> newBindingList);

	/**
	 * 删除关联信息
	 * @param publicId 记录关联的 publicId (哪个信息关联的其他信息)
	 */
    void deleteByPublicId(String publicId);

	/**
	 * 删除多条关联信息
	 * @param publicId
	 */
	void deleteManyByPublicId(List<String> publicId);


	/**
	 * 添加多条关联关系
	 *
	 */
	List<Binding> saveBindBatch(String publicId, String bindId ,String publicType);

	/**
	 * 更新关联信息的json 数据
	 * @param id 更新的数据id
	 */
    void updateJson(String id, Object obj, String type);
}