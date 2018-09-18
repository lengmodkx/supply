package com.art1001.supply.service.file;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.Project;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * fileService接口
 */
 public interface FileService {

	/**
	 * 查询分页file数据
	 */
	 List<File> findFilePagerList(Pager pager);

	/**
	 * 通过id获取单条file数据
	 */
	 File findFileById(String id);

	/**
	 * 通过id删除file数据
	 */
	 void deleteFileById(String id);

	/**
	 * 修改file数据
	 *
	 * @param projectId 项目id
	 * @param parentId  上级目录id
	 * @param multipartFile 文件
	 */
	 File uploadFile(String projectId, String parentId, MultipartFile multipartFile) throws Exception;

	 void updateFile(File file);

	/**
	 * 保存file数据
	 */
	 void saveFile(File file);

	 void saveFile(String files,String publicId,String projectId);
	/**
	 * 获取所有file数据
	 */
	 List<File> findFileAllList();

    /**
     * 项目创建后初始化文件目录
     */
	void initProjectFolder(Project project);


    /**
     * 查新该目录下的名称是否存在
     *
     * @param parentId 父级id
     * @param fileName 目录名称
     */
    int findByParentIdAndFileName(String parentId, String fileName);

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
     *
     * @param projectId 关联项目id
     * @param parentId 父级id，顶级目录为 0
     * @return List<File>
     */
	List<File> findChildFile(String projectId, String parentId);

	/**
	 * 移动文件
	 *
	 * @param fileIds 源文件id数组
	 * @param folderId 目标目录id
	 */
    void moveFile(String[] fileIds, String folderId);

	/**
	 * 复制文件
	 *
	 * @param fileIds 源文件id数组
	 * @param folderId 目标目录id
	 */
	void copyFile(String[] fileIds, String folderId);

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

	List<File> findTopLevel(String projectId);

	/**
	 * 更新tagId
	 */
    void updateTagId(String fileId, String tagIds);

	/**
	 * 根据项目id 查询出项目下的所有文件
	 * @param projectId 项目的id
	 * @return 文件信息集合
	 */
	List<File> findFileByProjectId(String projectId);

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
	 * 清空文件的标签
	 * @param fileId 文件的id
	 */
	void fileClearTag(String fileId);

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

	/**
	 * 上传文件到公开的文件库
	 * @param projectId 项目Id
	 * @param parentId
	 * @param file 文件 信息
	 */
    File uploadPublicFile(String projectId, String parentId, MultipartFile file);

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
    String findUidsByFileId(String fileId);

	/**
	 * 判断文件夹的名字 是否存在
	 * @param folderName 文件夹名字
	 * @param projectId 项目id
	 * @param parentId 当前目录id
	 * @return
	 */
    int findFolderIsExist(String folderName, String projectId,String parentId);

    void deleteFileByPublicId(String publicId);

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
	void saveFileBatch(String projectId, String files, String parnentId);

	/**
	 * 更新文件版本信息
	 * @param file 文件对象
	 * @param fileId 更新的文件id
	 */
	String updateVersion(MultipartFile file, String fileId);
}