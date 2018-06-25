package com.art1001.supply.mapper.file;

import java.util.List;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

	/**
	 * 查询当前文件目录下的文件夹及文件
	 *
	 * @param projectId 关联项目id
	 * @param parentId 父级id，顶级目录为 0
	 * @param isDel 删除标识
	 * @return List<File>
	 */
	List<File> findChildFile(@Param("projectId") String projectId, @Param("parentId") String parentId, @Param("isDel") Integer isDel);

	/**
	 * 查新该目录下的名称是否存在
	 *
	 * @param parentId 父级id
	 * @param fileName 目录名称
	 */
	int findByParentIdAndFileName(@Param("parentId") String parentId, @Param("fileName") String fileName);

	/**
	 * 根据项目id 和 文件 id 查询文件
	 * @param projectId 项目id
	 * @param fileId 上级id
	 */
    File findByProjectIdAndFileId(@Param("projectId") String projectId, @Param("fileId") String fileId);

	/**
	 * 根据项目id 和 上级 id 查询文件  查询子集
	 * @param projectId 项目id
	 * @param parentId 上级id
	 */
	List<File> findByProjectIdAndParentId(@Param("projectId") String projectId, @Param("parentId") String parentId);

	/**
	 * 获取项目顶级路径
	 */
	String findTopLevel(@Param("projectId") String projectId);
}