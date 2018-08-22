package com.art1001.supply.service.binding.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;

import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.binding.BindingVo;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.mapper.binding.BindingMapper;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.task.TaskService;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.binding.Binding;

/**
 * bindingServiceImpl
 */
@Service
public class BindingServiceImpl implements BindingService {

	/** bindingMapper接口*/
	@Resource
	private BindingMapper bindingMapper;

	@Resource
	private TaskService taskService;
	@Resource
	private FileService fileService;
	@Resource
	private ShareService shareService;
	@Resource
	private ScheduleService scheduleService;

	/**
	 * 查询分页binding数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Binding> findBindingPagerList(Pager pager){
		return bindingMapper.findBindingPagerList(pager);
	}

	/**
	 * 通过id获取单条binding数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public Binding findBindingById(String id){
		return bindingMapper.findBindingById(id);
	}

	/**
	 * 通过id删除binding数据
	 * @param id
	 * @param bindingId
	 */
	@Override
	public void deleteBindingById(String id, String bindingId){
		bindingMapper.deleteBindingById(id,bindingId);
	}

	/**
	 * 修改binding数据
	 * 
	 * @param binding
	 */
	@Override
	public void updateBinding(Binding binding){
		bindingMapper.updateBinding(binding);
	}
	/**
	 * 保存binding数据
	 * 
	 * @param binding
	 */
	@Override
	public void saveBinding(Binding binding){
		bindingMapper.saveBinding(binding);
	}
	/**
	 * 获取所有binding数据
	 * 
	 * @return
	 */
	@Override
	public List<Binding> findBindingAllList(){
		return bindingMapper.findBindingAllList();
	}

	/**
	 * 功能: 查询出该目标关联的所有信息
	 * 数据处理:
	 * @param  publicId 目标id
	 * @return 返回关联数据
	 */
	@Override
	public BindingVo listBindingInfoByPublicId(String publicId) {
		List<Binding> list = bindingMapper.listBindingInfoByPublicId(publicId);
		BindingVo binding = new BindingVo();
		List<Task> taskList = new ArrayList<>();
		List<Share> shareList = new ArrayList<>();
		List<File> fileList = new ArrayList<>();
		List<Schedule> scheduleList = new ArrayList<>();
		for (Binding b : list) {
			if(Objects.equals(b.getPublicType(),BindingConstants.BINDING_TASK_NAME)){
				taskList.add(taskService.findTaskByTaskId(b.getBindId()));
				binding.setTaskList(taskList);
				binding.setPublicType(BindingConstants.BINDING_TASK_NAME);
			}
			if(Objects.equals(b.getPublicType(),BindingConstants.BINDING_FILE_NAME)){
				fileList.add(fileService.findFileById(b.getBindId()));
				binding.setFileList(fileList);
				binding.setPublicType(BindingConstants.BINDING_FILE_NAME);
			}
			if(Objects.equals(b.getPublicType(),BindingConstants.BINDING_SCHEDULE_NAME)){
				scheduleList.add(scheduleService.findScheduleById(b.getBindId()));
				binding.setScheduleList(scheduleList);
				binding.setPublicType(BindingConstants.BINDING_SCHEDULE_NAME);
			}
			if(Objects.equals(b.getPublicType(),BindingConstants.BINDING_SHARE_NAME)){
				shareList.add(shareService.findById(b.getBindId()));
				binding.setShareList(shareList);
				binding.setPublicType(BindingConstants.BINDING_SHARE_NAME);
			}
		}
		return binding;
	}

	/**
	 * 实现方法  查询库中存不存在此条关联记录
	 * @param publicId 关联的信息id
	 * @param bindId 被关联的信息id
	 * @return
	 */
	@Override
	public String[] getBindingRecord(String publicId, String[] bindId) {
		return bindingMapper.getBindingRecord(publicId,bindId);
	}

	/**
	 * 查询出该信息关联的所有信息
	 * @param publicId 关联信息id
	 * @return
	 */
	@Override
	public List<Binding> findBindingInfoByPublic(String publicId) {
		return bindingMapper.findBindingInfoByPublic(publicId);
	}

	/**
	 * 保存多条 关联信息
	 * @param newBindingList 关联信息的集合
	 */
	@Override
	public void saveMany(List<Binding> newBindingList) {
		bindingMapper.saveMany(newBindingList);
	}

	/**
	 * 删除关联信息
	 * @param publicId 记录关联的 publicId (哪个信息关联的其他信息)
	 */
	@Override
	public void deleteByPublicId(String publicId) {
		bindingMapper.deleteByPublicId(publicId);
	}
}