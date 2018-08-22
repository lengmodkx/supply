package com.art1001.supply.mapper.file;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.FileVersion;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * filemapper接口
 */
@Mapper
public interface FileVersionMapper {

	/**
	 * 查询分页file数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<FileVersion> findFileVersionPagerList(Pager pager);

	/**
	 * 通过id获取单条file数据
	 * 
	 * @param id
	 * @return
	 */
	FileVersion findFileVersionById(String id);

	/**
	 * 通过id删除file数据
	 * 
	 * @param id
	 */
	void deleteFileVersionById(String id);

	/**
	 * 修改file数据
	 * 
	 * @param fileVersion
	 */
	void updateFileVersion(FileVersion fileVersion);

	/**
	 * 保存file数据
	 * 
	 * @param fileVersion
	 */
	void saveFileVersion(FileVersion fileVersion);

	/**
	 * 获取所有file数据
	 * 
	 * @return
	 */
	List<FileVersion> findFileVersionAllList();

    void updateMasterByFileId(String fileId);

	int findCountByFileId(String fileId);

	List<FileVersion> findByFileId(String fileId);

	/**
	 * 何少华
	 * 删除一个文件的所有历史版本信息
	 * @param fileId 文件id
	 */
	@Delete("delete from prm_file_version where file_id = #{fileId}")
	void deleteVersionInfoByFileId(String fileId);
}