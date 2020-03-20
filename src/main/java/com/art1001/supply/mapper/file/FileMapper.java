package com.art1001.supply.mapper.file;

import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FileApiBean;
import com.art1001.supply.entity.file.FileTree;
import com.art1001.supply.entity.file.FileTreeShowVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * filemapper接口
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {

	/**
	 * 通过id获取单条file数据
	 * 
	 * @param fileId
	 * @return
	 */
	File findFileById(String fileId);

	/**
	 * 通过id删除file数据
	 * 
	 * @param id
	 */
	void deleteFileById(String id);

	List<FileTree> querySubFileList(String fileId);

	List<FileTree> queryFileListByUserId(String userId);

	List<FileTree> queryFileByUserId(String userId);
	/**
	 * 查询出该文件夹下的所有子文件夹及文件
	 * @param fileId 父级id，顶级目录为 0
	 * @return
	 */
	//Page<File> findChildFile(IPage<File> page, @Param("fileId") String fileId);

	List<File> findChildFile(@Param("fileId") String fileId);
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
	 * 移动单个文件
	 * @param parentId 目标文件夹id
	 * @param projectId 目标项目Id
	 * @param fileId 要移动的文件/文件夹的Id
	 */
	void moveFile(String parentId,String projectId,String fileId);

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
	@Update("update prm_file set file_del = 0 ,update_time = #{currTime} where file_id = #{fileId}")
	void recoveryFile(@Param("fileId") String fileId, @Param("currTime") Long currTime);

	/**
	 * 查询某个文件夹下的公开文件
	 * @param parentId 父文件夹id
	 * @return
	 */
    List<File> findPublicFile(String parentId);


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

	@Select("select file_id as fileId,file_name as fileName,file_url as fileUrl,ext,size from prm_file where public_id = #{publicId}")
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

	/**
	 * 查询出该项目的最上级文件夹的id
	 * @param projectId 项目id
	 * @return
	 */
	String selectParentId(String projectId);

	/**
	 * 根据项目id获取该项目下的根文件夹
	 * @param projectId 项目id
	 * @return 文件树形图信息
	 */
    List<FileTreeShowVO> selectTreeFolderByProjectId(@Param("projectId") String projectId);

	/**
	 * 根据父级id获取该项目下的根文件夹
	 * @param parentId 父级id
	 * @return 文件树形图信息
	 */
	List<FileTreeShowVO> selectTreeChildFolder(@Param("parentId") String parentId);

	/**
	 * 获取一个项目的所有文件夹
	 * @param fileId 根目录id
	 * @return 文件夹信息
	 */
	List<File> selectProjectAllFolder(@Param("fileId") String fileId);

	/**
	 * 获取我创建的文件并且排序
	 * @param order 排序规则(名称,大小,创建时间)
	 * @return 我创建的文件数据
	 */
    List<File> created(@Param("order") String order, @Param("userId")String userId);

	List<File> createdBySize(@Param("userId") String userId,@Param("company")String company);

	/**
	 * 根据文件名称在项目中进行模糊查询
	 * @param fileName 文件名称 (模糊查询)
	 * @param projectId 项目id
	 * @return
	 */
    List<File> seachByName(@Param("fileName") String fileName, @Param("projectId") String projectId);

    List<File> publicFile(@Param("publicId")String publicId);

	/**
	 * 获取绑定该标签的文件信息 (version2.0)
	 * @param tagId 标签id
	 * @return 文件集合
	 */
	List<File> selectBindTagInfo(@Param("tagId") Long tagId);

	/**
	 * 获取某个文件或者目录的所有上级目录id (一直到顶端目录)
	 * @param fileId 文件id
	 * @return 目录集合
	 */
	String selectParentFolders(@Param("fileId") String fileId);

	/**
	 * 获取该项目下的根目录信息
	 * @author heShaoHua
	 * @describe 暂无
	 * @param projectId 项目id
	 * @updateInfo 暂无
	 * @date 2019/5/30 10:00
	 * @return 根目录信息
	 */
	File selectProjectParentFolder(String projectId);

	/**
	 * 获取一个文件的在项目中最顶级的目录信息
	 * @param fileId 文件/目录id
	 * @return 根目录id
	 * @author heShaoHua
	 * @describe 暂无
	 * @updateInfo 暂无
	 * @date 2019/5/30 11:33
	 */
	File selectProjectRootFolderId(@Param("fileId") String fileId);

	/*
	* 通过文件id和项目id获取文件信息
	* @Param
	* */
	File findFileTier(String projectId);


	Page<File> findMateriaBaseFile(@Param("page")Page page, @Param("folderId") String folderId, @Param("downloadCount") Boolean downloadCount);

	/**
	 * 获取 某个目录下的所有子级目录id字符串（逗号隔开）
	 * @param folderId 当前目录id
	 * @return id字符串
	 */
    String selectChildFolderIds(@Param("folderId") String folderId);

    void updateAll(@Param("userId")String userId,@Param("id") String id);

	void updateAllUser(@Param("userId") String userId,@Param("id") String id);
}