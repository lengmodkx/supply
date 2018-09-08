package com.art1001.supply.mapper.file;

import java.util.List;

import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.PublicFile;
import com.art1001.supply.entity.file.PublicFile;
import com.art1001.supply.entity.base.Pager;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公共文件库mapper接口
 */
@Mapper
public interface PublicFileMapper {

	/**
	 * 查询分页公共文件库数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	List<PublicFile> findPublicFilePagerList(Pager pager);

	/**
	 * 通过id获取单条公共文件库数据
	 * 
	 * @param id
	 * @return
	 */
	File findPublicFileById(String id);

	/**
	 * 通过id删除公共文件库数据
	 * 
	 * @param id
	 */
	void deletePublicFileById(String id);

	/**
	 * 修改公共文件库数据
	 * 
	 * @param publicFile
	 */
	void updatePublicFile(PublicFile publicFile);

	/**
	 * 保存公共文件库数据
	 * 
	 * @param publicFile
	 */
	void savePublicFile(PublicFile publicFile);

	/**
	 * 获取所有公共文件库数据
	 * 
	 * @return
	 */
	List<PublicFile> findPublicFileAllList();

	/**
	 * 查询出 模型库的信息
	 * @param publicName 模型库的名字
	 * @return
	 */
    PublicFile findPublicFolder(String publicName);

	/**
	 * 查询出该文件夹的 子文件 及文件夹
	 * @param parentId 父文件夹id
	 * @return
	 */
    List<PublicFile> findChildFile(String parentId);
}