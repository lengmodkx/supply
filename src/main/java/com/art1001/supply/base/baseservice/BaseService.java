package com.art1001.supply.base.baseservice;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 
 * service层接口
 * 
 * @author wangyafeng 2016年7月12日 下午3:03:19
 *
 * @param <T>
 *            泛型对象
 * @param <ID>
 *            ID
 */
public interface BaseService<T, ID extends Serializable> {

	/**
	 * 
	 * 插入对象
	 * 
	 * @param t
	 *            插入的对象
	 * @return int 返回影响的行数
	 */
	 int insert(T t);

	/**
	 * 
	 * 批量插入对象
	 * 
	 * @param t
	 *            插入的对象
	 * @return int 返回影响的行数
	 */
	 int insertBatch(List<T> t);

	/**
	 * 
	 * 批量删除
	 * 
	 * @param ids
	 *            删除的id
	 * @return int 返回影响的行数
	 */
	 int deleteBatchById(List<ID> ids);

	/**
	 * 
	 * deleteById 根据id删除
	 * 
	 * @param id
	 *            id
	 * @return int 返回影响的行数
	 */
	 int deleteById(ID id);

	/**
	 * 
	 * 根据id删除
	 * 
	 * @param id
	 *            id
	 * @return int 返回影响的行数
	 */
	 int deleteById(String id);

	/**
	 * 
	 * 更新对象
	 * 
	 * @param t
	 *            更新的对象
	 * @return int 返回影响的行数
	 */
	 int update(T t);

	/**
	 * 
	 * 根据参数查询对象
	 * 
	 * @param parameter
	 *            查询参数map
	 * @return T 返回查询的对象
	 */
	 T find(Map<String, Object> parameter);

	/**
	 * 
	 * 根据id查询对象
	 * 
	 * @param id
	 *            id
	 * @return T 返回查询的对象
	 */
	 T findById(ID id);

	/**
	 * 
	 * 根据id查询对象
	 * 
	 * @param id
	 *            id
	 * @return T 返回查询的对象
	 */
	 T findById(String id);

	/**
	 * 
	 * 根据名称查询
	 * 
	 * @param name
	 *            名称
	 * @return T 返回查询的对象
	 */
	 T findByName(String name);

	/**
	 * 
	 * 根据参数查询全部对象
	 * 
	 * @param parameter
	 *            查询参数map
	 * @return List<T> 返回查询的对象集合
	 */
	 List<T> queryListAll(Map<String, Object> parameter);

	/**
	 * 
	 * 根据分页参数查询对象
	 * 
	 * @param parameter
	 *            查询参数map
	 * @return List<T> 返回查询的对象集合
	 */
	 List<T> queryListByPage(Map<String, Object> parameter);

	/**
	 * 
	 * 根据参数查询对象总条数
	 * 
	 * @param parameter
	 *            查询参数map
	 * @return int 返回查询的对象总条数
	 */
	int count(Map<String, Object> parameter);

}
