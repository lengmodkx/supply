package com.art1001.supply.service.file;

import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FileApiBean;
import com.art1001.supply.entity.project.Project;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
 * fileService接口
 */
 public interface FileService extends IService<File> {

	/**
	 * 通过id获取单条file数据
	 */
	 File findFileById(String id);

	/**
	 * 通过id删除file数据
	 */
	 void deleteFileById(String id);


	 void saveFile(String files,String publicId,String projectId);

	/**
	 * 项目创建后初始化文件目录
	 * @param project 项目信息
	 * @return 项目根文件夹的id
	 */
	String initProjectFolder(Project project);


    /**
     * 创建目录
     *
     * @param projectId 项目id
     * @param parentId 要创建的目录的父级id
     * @param fileName 创建的目录名称
     */
	File createFolder(String projectId, String parentId, String fileName);

    /**
     * 查询当前文件目录下的文件夹及文件
     * @param parentId 父级id，顶级目录为 0
     * @return List<File>
     */
	List<File> findChildFile(String parentId);

	/**
	 * 移动文件
	 *
	 * @param fileIds 源文件id数组
	 * @param folderId 目标目录id
	 */
    void moveFile(String[] fileIds, String folderId);

    /**
     * 获取上级文件路径
     */
    String findProjectUrl(String projectId);

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
	 * 移入回收站
	 *
	 * @param fileIds ids
	 */
	void moveToRecycleBin(String[] fileIds);

	/**
	 * 获取一个文件夹下所有的子目录
	 * @param fileId 要获取的文件夹id
	 * @return List<File>
	 */
	List<File> findChildFolder(String fileId);

	/**
	 * 查询出该文件的所有参与者id
	 * @param fileId 文件id
	 * @return 参与者id
	 */
    String findJoinId(String fileId);

	/**
	 * 添加或者移除文件的参与者
	 * @param fileId 文件id
	 * @param newJoin 新的参与者id
	 * @return 影响行数
	 */
	void addAndRemoveFileJoin(String fileId, String newJoin);

	/**
	 * 根据文件id 查询出文件名
	 * @param publicId 文件id
	 * @return 文件名
	 */
    String findFileNameById(String publicId);

	/**
	 * 查询出我参与的所有文件
	 * @return
	 */
	List<File> findJoinFile();

	/**
	 * 查询出在该项目回收站中的文件
	 * @param projectId 项目id
	 * @return
	 */
	List<RecycleBinVO> findRecycleBin(String projectId,String type);

	/**
	 * 恢复文件
	 * @param fileId 文件的id
	 */
	void recoveryFile(String fileId);

	/**
	 * 查询某个文件夹下的公开文件
	 * @param parentId 父文件夹id
	 * @return
	 */
	List<File> findPublicFile(String parentId);


    List<File> findFileByPublicId(String publicId);

	String findFileId();

	/**
	 * 加载出该项目的所有文件数据
	 * @param projectId 项目id
	 * @param fileId 文件id
	 * @return
	 */
	List<File> findProjectFile(String projectId, String fileId);

	/**
	 * 插入多条文件信息
	 * @param files 文件id
	 */
	void saveFileBatch(String projectId, String files, String parentId,String publicId);

	/**
	 * 查询分享部分信息 (项目名称,文件名称,文件后缀名,文件url)
	 * @param id 文件id
	 * @return
	 */
	FileApiBean findFileApiBean(String id);

	/**
	 * 查询出项目下的根文件夹的id
	 * @param projectId 项目id
	 * @return
	 */
	String findParentId(String projectId);
}