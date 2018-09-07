package com.art1001.supply.mapper.binding;

import java.util.List;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.base.Pager;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * bindingmapper接口
 */
@Mapper
public interface BindingMapper {

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
	@Delete("delete from prm_binding where public_id = #{id} and bind_id = #{bindingId}")
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
	List<Binding> findBindingList(@Param("publicId")String publicId);

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
	@Delete("delete from prm_binding where public_id = #{publicId} or bind_id = #{publicId}")
    void deleteByPublicId(String publicId);

	/**
	 * 删除多条关联信息
	 * @param publicId 记录关联的 publicId (哪个信息关联的其他信息)
	 */
    void deleteManyByPublicId(List<String> publicId);
}