package com.art1001.supply.service.binding.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
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
import com.art1001.supply.util.IdGen;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mchange.v1.identicator.IdList;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.binding.Binding;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * bindingServiceImpl
 */
@Service
public class BindingServiceImpl extends ServiceImpl<BindingMapper, Binding> implements BindingService {

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
	@Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
	@Override
	public void saveBindBatch(String publicId, String bindId, String publicType) {
		//移除自己
        List<String> idList = new ArrayList<String>();
        idList = Arrays.asList(bindId.split(","));
		idList.remove(publicId);
		//批量删除 重复关联的关联项
		bindingMapper.delete(new QueryWrapper<Binding>().eq("publicId",publicId).in("bindId",idList));

		List<Binding> binds = new ArrayList<>();
		for (int i = 0;i < idList.size();i++){
			Binding binding = new Binding();
			//设置 谁绑定 的id
			binding.setPublicId(publicId);
			//设置被绑定的 信息的id
			binding.setBindId(idList.get(i));
			//设置绑定的类型
			binding.setPublicType(publicType);
            if(publicType.equals(Constants.TASK)){
                binding.setBindContent(JSON.toJSONString(taskService.findTaskApiBean(idList.get(i))));
            }
            if(publicType.equals(Constants.SHARE)){
                binding.setBindContent(JSON.toJSONString(shareService.findShareApiBean(idList.get(i))));
            }
            if(publicType.equals(Constants.FILE)){
               	binding.setBindContent(JSON.toJSONString(fileService.findFileApiBean(idList.get(i))));
            }
            if(publicType.equals(Constants.SCHEDULE)){
            	binding.setBindContent(JSON.toJSONString(scheduleService.findScheduleApiBean(idList.get(i))));
            }
			binds.add(binding);
		}
		saveBatch(binds);
	}
}