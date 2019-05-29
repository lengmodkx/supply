package com.art1001.supply.mapper.binding;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.binding.Binding;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * bindingmapper接口
 */
@Mapper
public interface BindingMapper extends BaseMapper<Binding> {

	/**
	 * 查询分页binding数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<Binding> findBindingPagerList(Pager pager);

	/**
	 * 通过id获取单条binding数据
	 * 
	 * @param id
	 * @return
	 */
	Binding findBindingById(String id);

	/**
	 * 通过id删除binding数据
	 * 
	 * @param id
	 */

	void deleteBindingById(@Param("id") String id,@Param("bindingId") String bindingId);

	/**
	 * 修改binding数据
	 * 
	 * @param binding
	 */
	void updateBinding(Binding binding);

	/**
	 * 保存binding数据
	 * 
	 * @param binding
	 */
	void saveBinding(Binding binding);

	/**
	 * 获取所有binding数据
	 * 
	 * @return
	 */
	List<Binding> findBindingAllList();

	/**
	 * 通过
	 * @param publicId
	 * @return
	 */
	List<Binding> findBindingList(String publicId);

	/**
	 * 查询出该目标的所有关联信息
	 * @param publicId 目标id
	 * @return 该目标关联数据
	 */
	List<Binding> listBindingInfoByPublicId(String publicId);

	/**
	 * 查询库中存不存在 该条记录
	 * @param publicId 关联的信息id
	 * @param bindId 被关联的信息id
	 * @return 库中的记录行数
	 */
    String[] getBindingRecord(@Param("publicId") String publicId,@Param("bindId") String[] bindId);

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
	 * @param publicId 记录关联的 publicId (哪个信息关联的其他信息)
	 */
    void deleteManyByPublicId(List<String> publicId);

	/**
	 * 删除多条
	 * @param publicId 信息id
	 * @param bindList 绑定信息id
	 */
	void deleteBatch(@Param("publicId") String publicId, @Param("binds") List<String> bindList);

	/**
	 * 插入批处理
	 * @param bindList 关联信息的id 集合
	 * @param publicId 当前要绑定其他信息的信息id
	 * @param publicType 绑定的信息类型
	 */
	void insertBatch(@Param("bindList") List<String> bindList, @Param("publicId") String publicId, @Param("publicType") String publicType);

	/**
	 * 更新关联信息的json 数据
	 * @param id 更新的数据id
	 */
    void updateJson(@Param("id") String id, @Param("obj") Object object, @Param("type") String type);

    /*
    * 查询是否有重复的绑定
    * */
    int findCountById(Binding binding);
}