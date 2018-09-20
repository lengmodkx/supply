package com.art1001.supply.service.file;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.FileVersion;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * fileService接口
 */
public interface FileVersionService extends IService<FileVersion> {

	/**
	 * 查询分页file数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	public List<FileVersion> findFileVersionPagerList(Pager pager);

	/**
	 * 通过id获取单条file数据
	 * 
	 * @param id
	 * @return
	 */
	public FileVersion findFileVersionById(String id);

	/**
	 * 通过id删除file数据
	 * 
	 * @param id
	 */
	public void deleteFileVersionById(String id);

	/**
	 * 修改file数据
	 * 
	 * @param fileVersion
	 */
	public void updateFileVersion(FileVersion fileVersion);

	/**
	 * 保存file数据
	 * 
	 * @param fileVersion
	 */
	public void saveFileVersion(FileVersion fileVersion);

	/**
	 * 把所有的master设置为0
	 */
	void updateMasterByFileId(String fileId);

	int findCountByFileId(String fileId);

	/**
	 * 获取所有file数据
	 * 
	 * @return
	 */
	public List<FileVersion> findFileVersionAllList();

	/**
	 * 查询文件的所有版本信息
	 */
	List<FileVersion> findByFileId(String fileId);

	/**
	 * 删除一个文件的所有历史版本信息
	 * @param fileId 文件id
	 */
	void deleteVersionInfoByFileId(String fileId);

}