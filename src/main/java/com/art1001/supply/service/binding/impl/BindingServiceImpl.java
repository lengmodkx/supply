package com.art1001.supply.service.binding.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import javax.annotation.Resource;

import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.binding.BindingVO;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskLog;
import com.art1001.supply.entity.task.TaskLogVO;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.binding.BindingMapper;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.IdGen;
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
	 */
	@Override
	public TaskLogVO deleteBindingById(String id){
        Binding bindingById = bindingMapper.findBindingById(id);
        Task taskByTaskId = taskService.findTaskByTaskId(bindingById.getBindId());
        bindingMapper.deleteBindingById(id);
        if(BindingConstants.BINDING_TASK_NAME.equals(bindingById.getPublicType())){
            return taskService.saveTaskLog(taskByTaskId,TaskLogFunction.A7.getName()+" "+ taskByTaskId.getTaskName());
        }
        if(BindingConstants.BINDING_FILE_NAME.equals(bindingById.getPublicType())){
            return taskService.saveTaskLog(taskByTaskId,TaskLogFunction.A6.getName()+" "+ taskByTaskId.getTaskName());
        }
        if(BindingConstants.BINDING_SCHEDULE_NAME.equals(bindingById.getPublicType())){
            return taskService.saveTaskLog(taskByTaskId,TaskLogFunction.A5.getName()+" "+ taskByTaskId.getTaskName());
        }
        if(BindingConstants.BINDING_SHARE_NAME.equals(bindingById.getPublicType())){
            return taskService.saveTaskLog(taskByTaskId,TaskLogFunction.A4.getName()+" "+ taskByTaskId.getTaskName());
        }
        return null;
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
		binding.setId(IdGen.uuid());
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
	public List listBindingInfoByPublicId(String publicId) {
		List<Binding> list = bindingMapper.listBindingInfoByPublicId(publicId);
		List bindings = new ArrayList();
		for (Binding b : list) {
			BindingVO bvo = new BindingVO();
			bvo.setId(b.getId());
			if(Objects.equals(b.getPublicType(),BindingConstants.BINDING_TASK_NAME)){
				bvo.setTask(taskService.findTaskByTaskId(b.getBindId()));
				bvo.setPublicType(BindingConstants.BINDING_TASK_NAME);
			}
			if(Objects.equals(b.getPublicType(),BindingConstants.BINDING_FILE_NAME)){
				bvo.setFile(fileService.findFileById(b.getBindId()));
				bvo.setPublicType(BindingConstants.BINDING_FILE_NAME);
			}
			if(Objects.equals(b.getPublicType(),BindingConstants.BINDING_SCHEDULE_NAME)){
				bvo.setSchedule(scheduleService.findScheduleById(b.getBindId()));
				bvo.setPublicType(BindingConstants.BINDING_SCHEDULE_NAME);
			}
			if(Objects.equals(b.getPublicType(),BindingConstants.BINDING_SHARE_NAME)){
				bvo.setShare(shareService.findById(b.getBindId()));
				bvo.setPublicType(BindingConstants.BINDING_SHARE_NAME);
			}
			bindings.add(bvo);
		}
		return bindings;
	}
}