package com.art1001.supply.service.file.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.PublicFile;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.file.PublicFileMapper;
import com.art1001.supply.service.file.PublicFileService;
import com.art1001.supply.util.IdGen;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.file.PublicFile;

/**
 * 公共文件库ServiceImpl
 */
@Service
public class PublicFileServiceImpl implements PublicFileService {

	/** 公共文件库Mapper接口*/
	@Resource
	private PublicFileMapper publicFileMapper;
	
	/**
	 * 查询分页公共文件库数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<PublicFile> findPublicFilePagerList(Pager pager){
		return publicFileMapper.findPublicFilePagerList(pager);
	}

	/**
	 * 通过id获取单条公共文件库数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public File findPublicFileById(String id){
		return publicFileMapper.findPublicFileById(id);
	}

	/**
	 * 通过id删除公共文件库数据
	 * 
	 * @param id
	 */
	@Override
	public void deletePublicFileById(String id){
		publicFileMapper.deletePublicFileById(id);
	}

	/**
	 * 修改公共文件库数据
	 * 
	 * @param publicFile
	 */
	@Override
	public void updatePublicFile(PublicFile publicFile){
		publicFileMapper.updatePublicFile(publicFile);
	}
	/**
	 * 保存公共文件库数据
	 * 
	 * @param publicFile
	 */
	@Override
	public void savePublicFile(PublicFile publicFile){
		publicFile.setFileId(IdGen.uuid());
		publicFileMapper.savePublicFile(publicFile);
	}
	/**
	 * 获取所有公共文件库数据
	 * 
	 * @return
	 */
	@Override
	public List<PublicFile> findPublicFileAllList(){
		return publicFileMapper.findPublicFileAllList();
	}

	/**
	 * 查询出 模型库的信息
	 * @param publicName 模型库的名字
	 * @return
	 */
	@Override
	public PublicFile findPublicFolder(String publicName) {
		return publicFileMapper.findPublicFolder(publicName);
	}

	/**
	 * 查询出该文件夹的 子文件 及文件夹
	 * @param parentId 父文件夹id
	 * @return
	 */
	@Override
	public List<PublicFile> findChildFile(String parentId) {
		return publicFileMapper.findChildFile(parentId);
	}

	/**
	 * 在公共模型库下创建文件夹
	 * @param folderName 文件夹名称
	 * @param parentId 父文件夹id
	 * @return
	 */
	@Override
	public void createPublicFolder(String folderName, String parentId) {
		//查询出该名称的文件数量
        int fileNameCount = findFileNameCount(folderName, parentId);
        if(fileNameCount > 0){
            throw new ServiceException("该名称已存在!");
        }
        PublicFile publicFile = new PublicFile();
		publicFile.setFileId(IdGen.uuid());
		publicFile.setFileName(folderName);
		publicFile.setParentId(parentId);
		publicFile.setCreateTime(System.currentTimeMillis());
		publicFile.setCatalog(1);
		publicFileMapper.savePublicFile(publicFile);
	}

	/**
	 * 查询同名的文件数量
	 * @param fileName 文件名称
	 * @param parentId 文件的父id
	 * @return
	 */
	@Override
	public int findFileNameCount(String fileName, String parentId) {
		return publicFileMapper.findFileNameCount(fileName,parentId);
	}
}