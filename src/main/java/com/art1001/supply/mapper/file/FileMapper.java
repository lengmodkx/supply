package com.art1001.supply.mapper.file;

import java.util.List;
import java.util.Map;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FileApiBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

/**
 * filemapper接口
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {

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
	 * @return List<File>
	 */
	List<File> findChildFile(@Param("projectId") String projectId, @Param("parentId") String parentId);

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

	/**
	 * 根据条件查询文件
	 */
	List<File> findFileList(File file);

	/**
	 * 根据id数组查询文件
	 * @param fileIds 数组
	 */
	List<File> findByIds(String[] fileIds);

	/**
	 * 获取项目url
	 */
    String findProjectUrl(String projectId);

	/**
	 * 移入回收站
	 *
	 * @param fileIds ids
	 */
	void moveToRecycleBin(String[] fileIds);

	/**
	 * 移动文件
	 * @param map 条件
	 * 		 fileIds 源文件id数组
	 * 		 folderId 目标目录id
	 */
	void moveFile(Map<String, Object> map);

	/**
	 * 查询子目录
	 */
	List<File> findChildFolder(String fileId);

	void updateTagId(@Param("fileId") String fileId, @Param("tagIds") String tagIds);

	/**
	 * 根据项目id 查询出该项目的所有文件信息
	 * @param projectId 项目id
	 * @return 项目的所有文件信息
	 */
    List<File> findFileByProjectId(String projectId);

    int chat(String fileId, String content);

	/**
	 * 返回该文件的所有参与者id
	 * @param fileId 文件id
	 * @return 参与者id
	 */
	@Select("select file_uids from prm_file where file_id = #{fileId}")
	String findJoinId(String fileId);

	/**
	 * 清空该文件的标签
	 * @param fileId 文件的id
	 */
	@Update("update prm_file set tag_id = '' where file_id = #{fileId}")
    void fileClearTag(String fileId);

	/**
	 * 根据文件id 查询出文件名
	 * @param publicId 文件id
	 * @return
	 */
	@Select("select file_name from prm_file where file_id = #{publicId}")
    String findFileNameById(String publicId);

	/**
	 * 查询我参与的所有文件
	 * @param userId 用户id
	 * @return
	 */
    List<File> findJoinFile(String userId);

	/**
	 * 查询出在该项目回收站中的文件
	 * @param projectId 项目id
	 * @return
	 */
    List<RecycleBinVO> findRecycleBin(@Param("projectId") String projectId, @Param("type") String type);

	/**
	 * 恢复文件
	 * @param fileId 文件id
	 */
	@Update("update prm_file set parent_id = '0',file_del = 0 where file_id = #{fileId}")
	void recoveryFile(String fileId);

	/**
	 * 查询某个文件夹下的公开文件
	 * @param parentId 父文件夹id
	 * @return
	 */
    List<File> findPublicFile(String parentId);

	/**
	 * 保存文件信息到公开文件表
	 * @param file 文件信息
	 */
	void savePublicFile(File file);

	/**
	 * 根据文件id 查询出该文件的 ids
	 * @param fileId 文件id
	 * @return
	 */
	@Select("select file_uids from prm_file where file_id = #{fileId}")
    String findUidsByFileId(String fileId);

	/**
	 * 判断文件夹的名字 是否存在
	 * @param folderName 文件夹名字
	 * @param projectId 项目id
	 * @param parentId 当前目录id
	 * @return
	 */
	@Select("select count(0) from prm_file where project_id = #{projectId} and parent_id = #{parentId}  and file_name = #{folderName}")
    int findFolderIsExist(@Param("folderName") String folderName, @Param("projectId") String projectId, @Param("parentId") String parentId);

	@Select("select file_id from prm_file where file_label=1")
	String findFileId();

	@Delete("delete from prm_file where public_id = #{publicId}")
	void deleteFileByPublicId(String publicId);

	@Select("select fileId,fileName,fileUrl, from prm_file where public_id = #{publicId}")
	List<File> findFileByPublicId(String publicId);

	/**
	 * 插入多条文件信息
	 * @param fileList 文件信息集合
	 */
    void saveFileBatch(List<File> fileList);

	/**
	 * 查询分享部分信息 (项目名称,文件名称,文件后缀名,文件url)
	 * @param id 文件id
	 * @return
	 */
    FileApiBean selectFileApiBean(String id);
}