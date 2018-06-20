package com.art1001.supply.service.file;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.File;
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
	 String uploadFile(String projectId, String parentId, MultipartFile multipartFile, HttpServletRequest request) throws Exception;

	 void updateFile(File file);

	/**
	 * 保存file数据
	 */
	 void saveFile(File file);

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
     * @param parentId 要创建的目录的父级id
     * @param fileName 创建的目录名称
     */
	void createFolder(String parentId, String fileName);

    /**
     * 上传文件
     */
	void uploadFile(File file);

    /**
     * 查询当前文件目录下的文件夹及文件
     *
     * @param projectId 关联项目id
     * @param parentId 父级id，顶级目录为 0
	 * @param isDel 删除标识
     * @return List<File>
     */
	List<File> findChildFile(String projectId, String parentId, Integer isDel);

}