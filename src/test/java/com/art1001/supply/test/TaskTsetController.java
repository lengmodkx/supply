package com.art1001.supply.test;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.*;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskLogService;
import com.art1001.supply.service.task.TaskMemberService;
import com.art1001.supply.service.task.TaskService;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heshaohua
 * @Title: TaskTsetController
 * @Description: 任务功能模块测试类
 * @date 2018/6/9 8:44
 **/
public class TaskTsetController extends TestBase{

    @Resource
   private TaskService taskService;
//
    @Resource
    private TagService tagService;
//
    @Resource
   private TaskLogService taskLogService;

   @Resource
    private TaskMemberService taskMemberService;
//
//    /**
//     * 添加任务的测试方法
//     */
//    @Test
//    public void addTask(){
//        Task task = new Task();
//        task.setTaskId("1000");
//        task.setUpdateTime(System.currentTimeMillis());
//        task.setCreateTime(System.currentTimeMillis());
//        task.setTaskDel(0);
//        task.setTaskStatus("0");
//        task.setEndTime(System.currentTimeMillis());
//        task.setExecutor("1");
//        task.setLevel(1);
//        task.setMemberId("1");
//        task.setParentId("1");
//        task.setPriority("1");
//        task.setProjectId("1");
//        task.setTaskType("1");
//        task.setUpdatePerson("2");
//        JSONObject jsonObject = new JSONObject();
//        String id = "";
////        try {
////            //获取当前session中的用户,如果有用户则为该任务的创建人
////            id = String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("id"));
////            if(id == null || id.equals("")){
////                jsonObject.put("msg","用户登陆超时，请重新登陆！");
////                jsonObject.put("result","0");
////                return jsonObject;
////            }
//            task.setTaskId(id);
//            //保存任务信息到数据库
//            //taskService.saveTask(startTime, endTime, remindTime, task);
//            jsonObject.put("msg","添加任务成功!");
////        } catch (Exception e){
////            jsonObject.put("error","任务添加失败!");
////        }
//
//    }
//
//    /**
//     * 移动任务至其他组的测试方法
//     */
//    @Test
//    public void mobileTask(){
//       Task t = new Task();
//       t.setTaskId("11111");
//       t.setTaskMenuId("1");
//       TaskMenuVO oldTaskMenuVO = new TaskMenuVO();
//        oldTaskMenuVO.setProjectId("1");
//        oldTaskMenuVO.setProjectName("项目A");
//        oldTaskMenuVO.setTaskGroupId("1");
//        oldTaskMenuVO.setTaskGroupName("分组A");
//        oldTaskMenuVO.setTaskMenuId("1");
//        oldTaskMenuVO.setTaskMenuName("菜单A");
//        TaskMenuVO newTaskMenuVO = new TaskMenuVO();
//        newTaskMenuVO.setProjectId("2");
//        newTaskMenuVO.setProjectName("项目B");
//        newTaskMenuVO.setTaskGroupId("2");
//        newTaskMenuVO.setTaskGroupName("分组B");
//        newTaskMenuVO.setTaskMenuId("2");
//        newTaskMenuVO.setTaskMenuName("菜单B");
//
//       try {
//            //修改该任务的任务组编号
//           TaskLogVO taskLogVO = taskService.mobileTask(t, oldTaskMenuVO, newTaskMenuVO);
//           System.out.println(taskLogVO.getResult());
//           System.out.println(taskLogVO.getContent());
//       } catch (ServiceException e){
//           System.out.println(e.getMessage());
//        }
//        return ;
//    }
//
//    /**
//     * 将任务 (移入回收站/回复) 的测试方法
//     * @return
//     */
//    @Test
//    public void moveToRecycleBin() {
//        String taskId = "11111";
//        String taskDel = "1";
//        try {
//            //int result = taskService.moveToRecycleBin(taskId, taskDel);
//        } catch (ServiceException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    /**
//     * 永久删除任务的测试方法
//     */
//    @Test
//    public void delTask(){
//        String taskId = "11111";
//        try {
//            int result = taskService.deleteTaskByTaskId(taskId);
//            System.out.println(result);
//        } catch (ServiceException e){
//            System.out.println(e.getMessage());
//        }
//
//    }
//
//    /**
//     * 完成任务(2=完成 1=未完成)
//     */
//    @Test
//    public void changeTaskStatus(){
//        String taskId = "11111";
//        String taskStatus = "1";
//        try {
//            //int result = taskService.changeTaskStatus(taskId,taskStatus);
//           // System.out.println(result);
//        } catch (ServiceException e){
//            System.out.println(e.getMessage());
//        }
//    }
//
//    /**
//     * 修改该任务信息的测试方法
//     */
//    @Test
//    public void updateTask(){
//        Task task = new Task();
//        try {
//            taskService.updateTask(task);
//        } catch (ServiceException e){
//            System.out.println(e.getMessage());
//        }
//    }
//
//    /**
//     * 更新任务时间( 开始 / 结束 / 提醒)
//     */
//    @Test
//    public void updateTaskTime(){
//        String startTime = "1995-3-22";
//        String endTime = "2018-9-9";
//        String taskId = "11111";
//        String remindTime = "2018-6-9";
//        try {
//            //int result = taskService.updateTaskTime(taskId,startTime,endTime,remindTime);
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void addTask1(){
//        Task task = new Task();
//        Project project = new Project();
//        project.setProjectName("A项目");
//        project.setStartTime(System.currentTimeMillis());
//        String[] memberId = {"4","21"};
//        String id = "11111";
//        try {
//            task.setMemberId(id);
//            //保存任务信息到数据库
//            taskService.saveTask(memberId,project,task);
//            List<TaskLog> taskLogAllList = taskLogService.findTaskLogAllList();
//            for (TaskLog log: taskLogAllList) {
//                System.out.println(log.getContent());
//            }
//        } catch (Exception e){
//        }
//    }
//
//    @Test
//    public void addTags(){
//        String taskId = "11111";
//        String[] oldTags = {"1","2"};
//        Tag tag = new Tag();
//        tag.setTagName("121212");
//        try {
//            int result = tagService.saveTag(tag,oldTags,taskId);
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//
    @Test
    public void addRaly(){
        Task task = null;
        File file = new File();
        file.setFileName("何少华");
        Schedule schedule = null;
        Share share = null;
        TaskMember taskMember = new TaskMember();
        String taskId = "1";
        TaskLogVO taskLogVO = taskMemberService.saveTaskMember(task,file,share,schedule,taskMember,taskId);
        File file1 = taskLogVO.getFile();
        System.out.println(file1.getFileName());

    }

    @Test
    public void aa(){
        Task task = null;
        File file = new File();
        file.setFileName("何少华");
        Share share = null;
        Schedule schedule = null;
        String taskId = "11111";
        String taskRelyId = "471e6fca915f4a62a6fca20aa96e5f93";
        TaskLogVO taskLogVO = taskMemberService.deleteTaskMemberById(task, file, share, schedule, taskId, taskRelyId);
        System.out.println(taskLogVO.getFile().getFileName());
    }

    @Test
    public void turnToFatherLevel(){
        Task task = new Task();
        task.setParentId("11111");
        task.setTaskId("22222");
        task.setTaskName("测试任务");
        try {
            TaskLogVO taskLogVO = taskService.turnToFatherLevel(task);
            System.out.println(taskLogVO.getContent());
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void addTags(){
        Tag tag = new Tag();
        tag.setTagId(System.currentTimeMillis());
        tag.setTagName("测试标签C");
        String projectId = "1";
        String taskId = "11111";
        try {
            //根据标签名称查询 当前存不存在数据库 如果存在直接绑定到当前任务,如果不存在则先插入标签 在绑定到当前任务
            int countByTagName = tagService.findCountByTagName(projectId, tag.getTagName());
            if(countByTagName == 0){
                tag.setTagId(System.currentTimeMillis());
                tagService.saveTag(tag);
            }
            //更新当前任务的标签信息
            TaskLogVO taskLogVO = taskService.addTaskTags(tag, taskId,countByTagName);
            if(taskLogVO.getResult() > 0){

            } else{

            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void removeTaskTag(){
        String taskId = "11111";
        Tag tag = new Tag();
        tag.setTagId(Long.valueOf("1528867589095"));
        Tag tag1 = new Tag();
        tag1.setTagId(Long.valueOf("1528867589095"));
        //tag1.setTagName("测试标签A");
        Tag tag2 = new Tag();
        tag2.setTagId(Long.valueOf("1528867565210"));
        //tag2.setTagName("测试标签B");
        Tag tag3 = new Tag();
        tag3.setTagId(Long.valueOf("1528867545131"));
        //tag3.setTagName("测试标签C");
        Tag[] tags = {tag1,tag2,tag3};
        try {
            int result = taskService.removeTaskTag(tags,tag,taskId);
        } catch (Exception e){
        }
    }


}
