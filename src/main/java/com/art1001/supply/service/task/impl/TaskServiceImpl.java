package com.art1001.supply.service.task.impl;

import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.task.Task;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.mapper.task.TaskMapper;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.IdGen;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.base.Pager;

/**
 * taskServiceImpl
 */
@Service
public class TaskServiceImpl implements TaskService {

	/** taskMapper接口*/
	@Resource
	private TaskMapper taskMapper;
	
	/**
	 * 查询分页task数据
	 * 
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Task> findTaskPagerList(Pager pager){
		return taskMapper.findTaskPagerList(pager);
	}

	/**
	 * 通过taskId获取单条task数据
	 * 
	 * @param taskId
	 * @return
	 */
	@Override 
	public Task findTaskByTaskId(String taskId){
		return taskMapper.findTaskByTaskId(taskId);
	}

	/**
	 * 通过taskId删除task数据
	 * 
	 * @param taskId
	 */
	@Override
	public void deleteTaskByTaskId(String taskId){
		taskMapper.deleteTaskByTaskId(taskId);
	}

	/**
	 * 修改task数据
	 *
	 * @param task
	 */
	@Override
	public int updateTask(Task task){
		try {
			int result = taskMapper.updateTask(task);
			return result;
		} catch (Exception e){
		    throw new ServiceException(e);
        }
	}

	/**
	 * 将添加的任务信息保存至数据库
	 * 
	 * @param task task信息
	 */
	@Override
	public void saveTask(Task task){
		try {
			//设置该任务的创建时间
			task.setCreateTime(System.currentTimeMillis());
			//设置该任务的id
			task.setTaskId(IdGen.uuid());
			//设置该任务的初始状态
			task.setTaskStatus("0");
			//设置该任务是否删除 0 未删除 1 已删除
			task.setTaskDel(0);
			//设置该任务的最后更新时间
			task.setUpdateTime(System.currentTimeMillis());
			//保存任务信息
			taskMapper.saveTask(task);
		} catch (Exception e){
			throw new ServiceException(e);
		}
	}
	/**
	 * 获取所有task数据
	 * 
	 * @return
	 */
	@Override
	public List<Task> findTaskAllList(){
		return taskMapper.findTaskAllList();
	}

    /**
     * 移入回收站/恢复任务
     * @param taskId 当前任务id
     * @param taskDel 当前任务是否已经在回收站
     * @return
     */
    @Override
    public int moveToRecycleBin(String taskId, String taskDel) {
        try {
           return taskMapper.moveToRecycleBin(taskId,taskDel);
        } catch (Exception e){
            throw new SystemException(e);
        }
    }

}