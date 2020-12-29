package com.art1001.supply.service.binding.impl;

import com.alibaba.fastjson.JSON;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FileApiBean;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.schedule.ScheduleApiBean;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.share.ShareApiBean;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskApiBean;
import com.art1001.supply.mapper.binding.BindingMapper;
import com.art1001.supply.mapper.file.FileMapper;
import com.art1001.supply.mapper.schedule.ScheduleMapper;
import com.art1001.supply.mapper.share.ShareMapper;
import com.art1001.supply.mapper.task.TaskMapper;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.task.TaskService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * bindingServiceImpl
 */
@Service
public class BindingServiceImpl extends ServiceImpl<BindingMapper, Binding> implements BindingService {

	/** bindingMapper接口*/
	@Resource
	private BindingMapper bindingMapper;

	@Resource
	private TaskMapper taskMapper;
	@Resource
	private FileMapper fileMapper;
	@Resource
	private ShareMapper shareMapper;
	@Resource
	private ScheduleMapper scheduleMapper;

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

	/**
	 * 删除多条关联信息
	 * @param publicId 记录关联的 publicId (哪个信息关联的其他信息)
	 */
	@Override
	public void deleteManyByPublicId(List<String> publicId) {
		bindingMapper.deleteManyByPublicId(publicId);
	}

	/**
	 * 保存关联信息
	 * @param publicId 信息id
	 * @param bindId 选择绑定的信息的id集合
	 * @param publicType 绑定信息的类型
	 */
	@Override
	public Object saveBindBatch(String publicId, String bindId, String publicType) {
		//移除自己
        List<String> idList = new ArrayList<String>();
        idList = new ArrayList<>(Arrays.asList(bindId.split(",")));
		idList.remove(publicId);
		//批量删除 重复关联的关联项
		bindingMapper.delete(new QueryWrapper<Binding>().eq("public_id",publicId).in("bind_id",idList));

		List<Binding> binds = new ArrayList<>();
		idList.forEach(id->{
			Binding binding = new Binding();
			//设置 谁绑定 的id
			binding.setPublicId(publicId);
			//设置被绑定的 信息的id
			binding.setBindId(id);
			//设置绑定的类型
			binding.setPublicType(publicType);
			if(publicType.equals(Constants.TASK)){
				binding.setBindContent(JSON.toJSONString(taskMapper.findTaskApiBean(id)));
			}
			if(publicType.equals(Constants.SHARE)){
				binding.setBindContent(JSON.toJSONString(shareMapper.selectShareApiBean(id)));
			}
			if(publicType.equals(Constants.FILE)){
				binding.setBindContent(JSON.toJSONString(fileMapper.selectFileApiBean(id)));
			}
			if(publicType.equals(Constants.SCHEDULE)){
				binding.setBindContent(JSON.toJSONString(scheduleMapper.selectScheduleApiBean(id)));
			}
			binds.add(binding);
		});
		saveBatch(binds);
		this.list(new QueryWrapper<Binding>().eq("public_id", publicId).in("bind_id",idList));
		return binds.stream().map(Binding::getBindContent).collect(Collectors.toList());
	}

	@Override
	public String getProjectId(String publicId){
		String projectId = "";
		Task task;
		File file;
		Schedule schedule;
		Share share;
		if((task = taskMapper.selectOne(new QueryWrapper<Task>().select("project_id").eq("task_id", publicId))) != null){
			if(StringUtils.isNotEmpty(task.getProjectId())){
				return task.getProjectId();
			}
		}

		if((share = shareMapper.selectOne(new QueryWrapper<Share>().select("project_id").eq("id",publicId))) != null){
			return share.getProjectId();
		}
		if((file = fileMapper.selectOne(new QueryWrapper<File>().select("project_id").eq("file_id",publicId))) != null){
			return file.getProjectId();
		}
		if((schedule = scheduleMapper.selectOne(new QueryWrapper<Schedule>().select("project_id").eq("schedule_id",publicId))) != null){
			return schedule.getProjectId();
		}
		return projectId;
	}

	/**
	 * 更新关联信息的json 数据
	 * @param id 更新的数据id
	 */
	@Override
	public void updateJson(String id, Object obj, String type) {
		bindingMapper.updateJson(id,obj,type);
	}

	/*
	* 查询是否有重复绑定
	* */
	@Override
	public int findCountById(String bindId, String publicType, String publicId) {
		Binding binding=new Binding();
		binding.setBindId(bindId);
		binding.setPublicId(publicId);
		binding.setPublicType(publicType);
		return bindingMapper.findCountById(binding);
	}

	@Override
	public void setBindingInfo(String publicId, File file,Task task,Share share,Schedule schedule){
		List<Binding> bindings = this.list(new QueryWrapper<Binding>().eq("public_id", publicId));
		List<TaskApiBean> tasks = new ArrayList<>();
		List<ScheduleApiBean> schedules = new ArrayList<>();
		List<FileApiBean> files = new ArrayList<>();
		List<ShareApiBean> shares = new ArrayList<>();
		bindings.forEach(item -> {
			if(item.getPublicType().equals(Constants.TASK)){
				tasks.add(JSON.parseObject(item.getBindContent(), TaskApiBean.class));
			}
			if(item.getPublicType().equals(Constants.SCHEDULE)){
				schedules.add(JSON.parseObject(item.getBindContent(), ScheduleApiBean.class));
			}
			if(item.getPublicType().equals(Constants.FILE)){
				files.add(JSON.parseObject(item.getBindContent(), FileApiBean.class));
			}
			if(item.getPublicType().equals(Constants.SHARE)){
				shares.add(JSON.parseObject(item.getBindContent(), ShareApiBean.class));
			}
		});
		if(file != null){
			file.setBindFiles(files);
			file.setBindTasks(tasks);
			file.setBindSchedules(schedules);
			file.setBindShares(shares);
		}
		if(task != null){
			task.setBindFiles(files);
			task.setBindTasks(tasks);
			task.setBindSchedules(schedules);
			task.setBindShares(shares);
		}
		if(share != null){
			share.setBindFiles(files);
			share.setBindTasks(tasks);
			share.setBindSchedules(schedules);
			share.setBindShares(shares);
		}
		if(schedule != null){
			schedule.setBindFiles(files);
			schedule.setBindTasks(tasks);
			schedule.setBindSchedules(schedules);
			schedule.setBindShares(shares);
		}
	}
}