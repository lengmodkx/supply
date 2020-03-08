package com.art1001.supply.service.tag.impl;

import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FileRepository;
import com.art1001.supply.entity.role.ProRole;
import com.art1001.supply.entity.role.ProRoleUser;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.tag.TagRelation;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.tag.TagMapper;
import com.art1001.supply.mapper.tagrelation.TagRelationMapper;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.resource.ProResourcesRoleService;
import com.art1001.supply.service.role.ProRoleUserService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * tagServiceImpl
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper,Tag> implements TagService {

	/** tagMapper接口*/
	@Resource
	private TagMapper tagMapper;

	@Resource
	private TagRelationMapper tagRelationMapper;

	@Resource
	private TagRelationService tagRelationService;

	@Resource
	private TaskService taskService;

	@Resource
	private FileService fileService;

	@Resource
	private ShareService shareService;

	@Resource
	private ScheduleService scheduleServcie;

    /**
     * ElasticSearch 查询接口
     */
    @Autowired
    private FileRepository fileRepository;

    @Resource
	private ProRoleUserService proRoleUserService;


	/**
	 * 查询分页tag数据
	 *
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Tag> findTagPagerList(Pager pager){
		return tagMapper.findTagPagerList(pager);
	}

	/**
	 * 通过tagId获取单条tag数据
	 *
	 * @param tagId
	 * @return
	 */
	@Override
	public Tag findById(Integer tagId){
		return tagMapper.findById(tagId);
	}

	/**
	 * 通过tagId删除tag数据
	 *
	 * @param tagId
	 */
	@Override
	public void deleteTagByTagId(Long tagId){
		//删除和这个标签绑定的关系信息
		tagRelationMapper.deleteTagRelationByTagId(tagId);
		tagMapper.deleteTagByTagId(tagId);
	}

	/**
	 * 修改tag数据
	 *
	 * @param tag
	 */
	@Override
	public void updateTag(Tag tag) throws ServiceException{
		Tag oldTag = tagMapper.selectById(tag.getTagId());
		if(!oldTag.getTagName().equals(tag.getTagName())){
			if(tagMapper.findCountByTagName(tag.getProjectId(), tag.getTagName()) > 0){
				throw new ServiceException("标签名称已存在,无法修改!");
			}
		}
		tagMapper.updateTag(tag);
	}

	/**
	 * 保存tag数据
	 *
     * @param tag 标签实体信息
     */
	@Override
	public Tag saveTag(Tag tag){
		// 如果标签已经存在，则直接使用
		int count = tagMapper.findCountByTagName(tag.getProjectId(), tag.getTagName());
		if (count > 0) {
			throw new ServiceException();
		}

		tag.setCreateTime(System.currentTimeMillis());
		tag.setUpdateTime(System.currentTimeMillis());
		tag.setMemberId(ShiroAuthenticationManager.getUserId());
		tag.setIsDel(0);
        tagMapper.saveTag(tag);
        return tag;
    }
	/**
	 * 获取所有tag数据
	 *
	 * @return
	 */
	@Override
	public List<Tag> findTagAllList(){
		return tagMapper.findTagAllList();
	}

	@Override
	public int findCountByTagName(String projectId, String tagName) {
		return tagMapper.findCountByTagName(projectId, tagName);
	}

	@Override
	public List<Tag> findByProjectId(String projectId) {
		return tagMapper.findByProjectId(projectId);
	}

	@Override
	public Map<String, Object> findByTag(Tag tag) {
		Map<String, Object> map = new HashMap<>();
		return map;
	}

	@Override
	public List<Tag> findByIds(Integer[] idArr) {
		return tagMapper.findByIds(idArr);
	}

	/**
	 * 根据用户输入的标签名称模糊查询出标签
	 * @param tagName 标签名称
	 * @return 标签集合
	 */
	@Override
	public List<Tag> searchTag(String tagName,String projectId) {
		return tagMapper.searchTag(tagName,projectId);
	}

    /**
     * 移除标签
     * @param tagId 标签id
	 * @param publicId (任务 ,文件, 日程, 分享) id
	 * @param publicType (任务,文件,日程,分享) 类型
     */
	@Override
	public void removeTag(String publicId, String publicType, long tagId) {
		TagRelation tagRelation = new TagRelation();
		tagRelation.setTagId(tagId);
		this.setTagRelation(publicId,publicType,tagRelation);
		tagRelationMapper.deleteByPublicId(tagRelation);
	}

    /**
	 * 保存信息和标签的绑定关系
     * @param tagId 标签id
     * @param publicId (任务 ,文件, 日程, 分享) id
     * @param publicType (任务,文件,日程,分享) 类型
     */
    @Override
    public boolean addItemTag(long tagId, String publicId, String publicType) {
		TagRelation tagRelation = new TagRelation();
		this.setTagRelation(publicId,publicType,tagRelation);
		tagRelation.setTagId(tagId);
		tagRelation.setId(IdGen.uuid());
		return tagRelation.insert();
    }

	/**
	 * 保存多条tag
	 * @param newTagList 多个tag信息i
	 */
	@Override
	public void saveMany(List<Tag> newTagList) {
		tagMapper.saveMany(newTagList);
	}

	@Override
	public List<Tag> findByPublicId(String publicId, String publicType) {
		return tagMapper.findByPublicId(publicId,publicType);
	}

	/**
	 * 查询出在该项目回收站中的标签
	 * @param projectId 项目id
	 * @return
	 */
	@Override
	public List<RecycleBinVO> findRecycleBin(String projectId) {
		return tagMapper.findRecycleBin(projectId);
	}

	/**
	 * 将标签移入回收站
	 * @param tagId 标签id
	 */
	@Override
	public void moveToRecycleBin(String tagId) {
		tagMapper.moveToRecycleBin(tagId,System.currentTimeMillis());
	}

	/**
	 * 恢复标签
	 * @param tagId 标签id
	 */
	@Override
	public void recoveryTag(String tagId) {
		tagMapper.recoveryTag(tagId);
	}

	@Override
	public List<Tag> classification(List<Tag> tagList) {
		return null;
	}

	/**
	 * 创建标签的同时绑定到某个信息上
	 * @param tag 标签信息
	 * @param publicId 绑定信息的id
	 * @param publicType 绑定信息的类型(任务,文件,日程,分享)
	 * @return 是否创建并绑定成功
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Boolean addAndBind(Tag tag, String publicId, String publicType) {
		if(this.findCountByTagName(tag.getProjectId(),tag.getTagName()) > 0){
			throw new ServiceException("该标签已经存在不能重复添加!");
		}
		tag.setCreateTime(System.currentTimeMillis());
		tag.setUpdateTime(System.currentTimeMillis());
		tag.setMemberId(ShiroAuthenticationManager.getUserId());
		tagMapper.insert(tag);
		TagRelation tr = new TagRelation();
		tr.setTagId(tag.getTagId());
		if(Constants.TASK.equals(publicType)){
			tr.setTaskId(publicId);
		}
		if(Constants.FILE.equals(publicType)){
            Optional<File> file = fileRepository.findById(publicId);
            tr.setFileId(publicId);
		}if(Constants.SHARE.equals(publicType)){
			tr.setShareId(publicId);
		}
		if(Constants.SCHEDULE.equals(publicType)){
			tr.setScheduleId(publicId);
		}
		return tagRelationService.save(tr);
	}

	/**
	 * 该方法用于封装tagRelation对象信息
	 * @param publicId 绑定的信息id
	 * @param publicType 绑定的信息类型
	 * @param tr 要放入的实体
	 */
	private void setTagRelation(String publicId, String publicType, TagRelation tr) {
		if(Constants.TASK.equals(publicType)){
			tr.setTaskId(publicId);
		}else if(Constants.FILE.equals(publicType)){
			tr.setFileId(publicId);
		}else if(Constants.SCHEDULE.equals(publicType)){
			tr.setScheduleId(publicId);
		}else if(Constants.SHARE.equals(publicType)){
			tr.setShareId(publicId);
		} else{
			throw new ServiceException("指定的绑定信息错误！");
		}
	}

	/**
	 * 获取标签的绑定信息 (version 2.0)
	 * @param tagId 标签id
	 * @return 绑定信息
	 */
	@Override
	public Tag getTagBindInfo(Long tagId) {
		if(!this.checkIsExist(tagId)){
			throw new ServiceException("该标签不存在!");
		}
		Tag t = this.getById(tagId);
		t.setTaskList(taskService.getBindTagInfo(tagId));
		t.setFileList(fileService.getBindTagInfo(tagId));
		t.setScheduleList(scheduleServcie.getBindTagInfo(tagId));
		t.setShareList(shareService.getBindTagInfo(tagId));
		return t;
	}

	/**
	 * 根据标签id检查标签存不存在
	 * @param tagId 标签id
	 * @return 是否存在
	 */
	@Override
	public Boolean checkIsExist(Long tagId) {
		return tagMapper.selectCount(new QueryWrapper<Tag>().eq("tag_id", tagId)) > 0;
	}

	/**
	 * 根据项目id 查询出标签信息
	 * @param projectId 项目id
	 * @return 标签集合
	 */
	@SuppressWarnings({"Unchecked", "unchecked"})
	@Override
	public List<Tag> getByProjectId(String projectId) {
		LambdaQueryWrapper<Tag> eq = new QueryWrapper<Tag>().lambda().select(Tag::getTagId, Tag::getTagName, Tag::getBgColor).eq(Tag::getProjectId, projectId);
		return tagMapper.selectList(eq.orderByDesc(Tag::getCreateTime));
	}
}