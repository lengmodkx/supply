package com.art1001.supply.mapper.file;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.FileLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * filemapper接口
 */
@Mapper
public interface FileLogMapper {

	/**
	 * 查询分页file数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<FileLog> findFileLogPagerList(Pager pager);

	/**
	 * 通过id获取单条file数据
	 * 
	 * @param id
	 * @return
	 */
	FileLog findFileLogById(String id);

	/**
	 * 通过id删除file数据
	 * 
	 * @param id
	 */
	void deleteFileLogById(String id);

	/**
	 * 修改file数据
	 * 
	 * @param fileLog
	 */
	void updateFileLog(FileLog fileLog);

	/**
	 * 保存file数据
	 * 
	 * @param fileLog
	 */
	void saveFileLog(FileLog fileLog);

	/**
	 * 获取所有file数据
	 * 
	 * @return
	 */
	List<FileLog> findFileLogAllList();

}