package com.art1001.supply.test;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskMember;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.service.task.TaskService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @Title: TaskTsetController
 * @Description: 任务功能模块测试类
 * @date 2018/6/9 8:44
 **/
public class TaskTsetController extends TestBase{

    @Resource
    private TaskService taskService;

    /**
     * 添加任务的测试方法
     */
    @Test
    public void addTask(){
        Task task = new Task();
        task.setTaskId("1000");
        task.setUpdateTime(System.currentTimeMillis());
        task.setCreateTime(System.currentTimeMillis());
        task.setTaskDel(0);
        task.setTaskStatus("0");
        task.setEndTime(System.currentTimeMillis());
        task.setExecutor("1");
        task.setLevel(1);
        task.setMemberId("1");
        task.setParentId("1");
        task.setPriority("1");
        task.setProjectId("1");
        task.setTaskType("1");
        task.setUpdatePerson("2");
        JSONObject jsonObject = new JSONObject();
        String id = "";
//        try {
//            //获取当前session中的用户,如果有用户则为该任务的创建人
//            id = String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("id"));
//            if(id == null || id.equals("")){
//                jsonObject.put("msg","用户登陆超时，请重新登陆！");
//                jsonObject.put("result","0");
//                return jsonObject;
//            }
            task.setTaskId(id);
            //保存任务信息到数据库
            //taskService.saveTask(startTime, endTime, remindTime, task);
            jsonObject.put("msg","添加任务成功!");
//        } catch (Exception e){
//            jsonObject.put("error","任务添加失败!");
//        }

    }

    /**
     * 移动任务至其他组的测试方法
     */
    @Test
    public void mobileTask(){
       Task t = new Task();
       t.setTaskId("11111");
       t.setTaskMenuId("1");
       try {
            //修改该任务的任务组编号
            int result = taskService.updateTask(t);
        } catch (ServiceException e){
           System.out.println(e.getMessage());
        }
        return ;
    }

    /**
     * 将任务 (移入回收站/回复) 的测试方法
     * @return
     */
    @Test
    public void moveToRecycleBin() {
        String taskId = "11111";
        String taskDel = "1";
        try {
            int result = taskService.moveToRecycleBin(taskId, taskDel);
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 永久删除任务的测试方法
     * @param taskId 要删除的任务id
     */
    @Test
    public void delTask(){
        String taskId = "11111";
        try {
            int result = taskService.deleteTaskByTaskId(taskId);
            System.out.println(result);
        } catch (ServiceException e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * 完成任务(2=完成 1=未完成)
     * @param taskId 当前任务id
     * @param taskStatus 当前任务状态
     */
    @Test
    public void changeTaskStatus(){
        String taskId = "11111";
        String taskStatus = "1";
        try {
            int result = taskService.changeTaskStatus(taskId,taskStatus);
            System.out.println(result);
        } catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * 修改该任务信息的测试方法
     * @param task 需要任务的实体信息
     */
    @Test
    public void updateTask(){
        Task task = new Task();
        try {
            taskService.updateTask(task);
        } catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * 更新任务时间( 开始 / 结束 / 提醒)
     * @param startTime 任务开始时间
     * @param endTime 任务结束时间
     * @param taskId 任务id
     * @param remindTime 任务提醒时间
     */
    @Test
    public void updateTaskTime(){
        String startTime = "1995-3-22";
        String endTime = "2018-9-9";
        String taskId = "11111";
        String remindTime = "2018-6-9";
        try {
            int result = taskService.updateTaskTime(taskId,startTime,endTime,remindTime);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void addTask1(){
        Task task = new Task();
        Project project = new Project();
        project.setProjectName("A项目");
        project.setStartTime(System.currentTimeMillis());
        String[] memberId = {"4","21","33333"};
        String id = "11111";
        try {
            task.setMemberId(id);
            //保存任务信息到数据库
            taskService.saveTask(memberId,project,task);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }



}
