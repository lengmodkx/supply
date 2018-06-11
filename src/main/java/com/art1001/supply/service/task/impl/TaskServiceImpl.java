package com.art1001.supply.service.task.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.annotation.Resource;

import com.art1001.supply.entity.task.Task;
import com.art1001.supply.mapper.task.TaskMapper;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.IdGen;
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
	 * 重写方法
	 * 查询分页task数据
	 * @param pager 分页对象
	 * @return
	 */
	@Override
	public List<Task> findTaskPagerList(Pager pager){
		return taskMapper.findTaskPagerList(pager);
	}

	/**
	 * 重写方法
	 * 通过taskId获取单条task数据
	 * @param taskId
	 * @return
	 */
	@Override 
	public Task findTaskByTaskId(String taskId){
		return taskMapper.findTaskByTaskId(taskId);
	}

	/**
	 * 重写方法
	 * 删除任务
	 * 通过taskId删除task数据
     * @param taskId 任务id
     */
	@Override
	public int deleteTaskByTaskId(String taskId){
        return taskMapper.deleteTaskByTaskId(taskId);
    }

	/**
	 * 重写方法
	 * 修改task数据
	 * @param task 任务信息
	 */
	@Override
	public int updateTask(Task task){
        task.setUpdateTime(System.currentTimeMillis());
        return taskMapper.updateTask(task);
	}

	/**
	 * 重写方法
	 * 将添加的任务信息保存至数据库
     * @param startTime 任务开始时间
     * @param endTime 任务结束时间
     * @param remindTime 任务提醒时间
     * @param repetitionTime 任务重复时间
     * @param task task信息
     */
	@Override
	public void saveTask(String startTime, String endTime, String remindTime,String repetitionTime,Task task) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //设置该任务的id
        task.setTaskId(IdGen.uuid());
        //设置该任务的创建时间
        task.setCreateTime(System.currentTimeMillis());
        try{
            //设置任务的开始时间
            if (startTime != null && startTime != "") {
                task.setStartTime(format.parse(startTime).getTime());
            }
            //设置任务结束时间
            if (endTime != null && endTime != "") {
                task.setEndTime(format.parse(endTime).getTime());
            }
            //设置任务提醒时间
            if (remindTime != null && remindTime != "") {
                task.setRemindTime(format.parse(remindTime).getTime());
            }
            //设置任务重复时间
            if(repetitionTime != null && repetitionTime != ""){
                task.setRepetitionTime(format.parse(repetitionTime).getTime());
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
//        //初始创建任务设置为父任务
//        task.setParentId("0");
        //设置该任务的最后更新时间
        task.setUpdateTime(System.currentTimeMillis());
        //设置该任务的初始状态
        task.setTaskStatus("1");
        //设置该任务是否删除 0 未删除 1 已删除
        task.setTaskDel(0);
        //保存任务信息
        taskMapper.saveTask(task);
	}

	/**
	 * 重写方法
	 * 获取所有task数据
	 * @return
	 */
	@Override
	public List<Task> findTaskAllList(){
		return taskMapper.findTaskAllList();
	}

    /**
	 * 重写方法
     * 移入回收站/恢复任务
     * @param taskId 当前任务id
     * @param taskDel 当前任务是否已经在回收站
     * @return
     */
    @Override
    public int moveToRecycleBin(String taskId, String taskDel) {
        return taskMapper.moveToRecycleBin(taskId,taskDel,System.currentTimeMillis());
    }

	/**
	 * 重写方法
	 * 修改当前任务状态
	 * @param taskId 任务id
	 * @return
	 */
	@Override
	public int changeTaskStatus(String taskId,String taskStatus) {
	    return taskMapper.changeTaskStatus(taskId,taskStatus,System.currentTimeMillis());
	}

    /**
     * 重写方法
     * 设定任务的时间(开始 / 结束)
     * @param startTime 任务开始时间
     * @param endTime 任务结束时间
     * @param remindTime 任务提醒时间
     * @return
     */
    @Override
    public int updateTaskTime(String taskId, String startTime, String endTime, String remindTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Task task = new Task();
        try {
            if(startTime != null &&  startTime != ""){
                task.setStartTime(format.parse(startTime).getTime());
            }
            if(endTime != null &&  endTime != ""){
                task.setEndTime(format.parse(endTime).getTime());
            }
            if(remindTime != null &&  remindTime != ""){
                task.setRemindTime(format.parse(remindTime).getTime());
            }
            task.setUpdateTime(System.currentTimeMillis());
            //执行更新操作传入数据库
            return taskMapper.updateTask(task);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断当前菜单有没有任务
     * @param taskMenuId 菜单id
     * @return
     */
    @Override
    public int findTaskByMenuId(String taskMenuId) {
        return taskMapper.findTaskByMenuId(taskMenuId);
    }

}