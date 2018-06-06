package com.art1001.supply.mapper.file;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.File;
import org.apache.ibatis.annotations.Mapper;

/**
 * filemapper接口
 */
@Mapper
public interface FileMapper {

	/**
	 * 查询分页file数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<File> findFilePagerList(Pager pager);

	/**
	 * 通过id获取单条file数据
	 * 
	 * @param id
	 * @return
	 */
	File findFileById(String id);

	/**
	 * 通过id删除file数据
	 * 
	 * @param id
	 */
	void deleteFileById(String id);

	/**
	 * 修改file数据
	 * 
	 * @param file
	 */
	void updateFile(File file);

	/**
	 * 保存file数据
	 * 
	 * @param file
	 */
	void saveFile(File file);

	/**
	 * 获取所有file数据
	 * 
	 * @return
	 */
	List<File> findFileAllList();

}