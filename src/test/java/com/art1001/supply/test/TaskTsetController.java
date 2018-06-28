//package com.art1001.supply.test;
//
//import com.alibaba.fastjson.JSONObject;
//import com.art1001.supply.entity.file.File;
//import com.art1001.supply.entity.project.Project;
//import com.art1001.supply.entity.schedule.Schedule;
//import com.art1001.supply.entity.share.Share;
//import com.art1001.supply.entity.tag.Tag;
//import com.art1001.supply.entity.task.*;
//import com.art1001.supply.entity.user.UserEntity;
//import com.art1001.supply.exception.ServiceException;
//import com.art1001.supply.service.tag.TagService;
//import com.art1001.supply.service.task.TaskLogService;
//import com.art1001.supply.service.task.TaskMemberService;
//import com.art1001.supply.service.task.TaskService;
//import com.art1001.supply.service.user.UserService;
//import org.junit.Test;
//
//import javax.annotation.Resource;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author heshaohua
// * @Title: TaskTsetController
// * @Description: 任务功能模块测试类
// * @date 2018/6/9 8:44
// **/
//public class TaskTsetController extends TestBase{
//
//    @Resource
//    private UserService userService;
//    @Resource
//   private TaskService taskService;
////
//    @Resource
//    private TagService tagService;
////
//    @Resource
//   private TaskLogService taskLogService;
//
//   @Resource
//    private TaskMemberService taskMemberService;
//
//
//
//
//
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
//        //task.setTaskId(gend);
//        //保存任务信息到数据库
//        //taskService.saveTask(startTime, endTime, remindTime, task);
//        jsonObject.put("msg","添加任务成功!");
//    }
////
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
////
////    /**
////     * 将任务 (移入回收站/回复) 的测试方法
////     * @return
////     */
////    @Test
////    public void moveToRecycleBin() {
////        String taskId = "11111";
////        String taskDel = "1";
////        try {
////            //int result = taskService.moveToRecycleBin(taskId, taskDel);
////        } catch (ServiceException e) {
////            System.out.println(e.getMessage());
////        }
////    }
////
////    /**
////     * 永久删除任务的测试方法
////     */
////    @Test
////    public void delTask(){
////        String taskId = "11111";
////        try {
////            int result = taskService.deleteTaskByTaskId(taskId);
////            System.out.println(result);
////        } catch (ServiceException e){
////            System.out.println(e.getMessage());
////        }
////
////    }
////
////    /**
////     * 完成任务(2=完成 1=未完成)
////     */
////    @Test
////    public void changeTaskStatus(){
////        String taskId = "11111";
////        String taskStatus = "1";
////        try {
////            //int result = taskService.changeTaskStatus(taskId,taskStatus);
////           // System.out.println(result);
////        } catch (ServiceException e){
////            System.out.println(e.getMessage());
////        }
////    }
////
////    /**
////     * 修改该任务信息的测试方法
////     */
////    @Test
////    public void updateTask(){
////        Task task = new Task();
////        try {
////            taskService.updateTask(task);
////        } catch (ServiceException e){
////            System.out.println(e.getMessage());
////        }
////    }
////
////    /**
////     * 更新任务时间( 开始 / 结束 / 提醒)
////     */
////    @Test
////    public void updateTaskTime(){
////        String startTime = "1995-3-22";
////        String endTime = "2018-9-9";
////        String taskId = "11111";
////        String remindTime = "2018-6-9";
////        try {
////            //int result = taskService.updateTaskTime(taskId,startTime,endTime,remindTime);
////        } catch (Exception e){
////            System.out.println(e.getMessage());
////        }
////    }
////
//    @Test
//    public void addTask1(){
//        Task task = new Task();
//        Project project = new Project();
//        project.setProjectId("1");
//        project.setProjectName("A项目");
//        project.setStartTime(System.currentTimeMillis());
//
//        UserEntity userEntity1 = new UserEntity();
//        userEntity1.setId("1000001");
//        userEntity1.setCreateTime(new Date());
//        userEntity1.setUserName("测试用户A");
//
//        UserEntity userEntity2 = new UserEntity();
//        userEntity2.setId("1000002");
//        userEntity2.setCreateTime(new Date());
//        userEntity2.setUserName("测0试用户B");
//
//        UserEntity userEntity3 = new UserEntity();;
//        userEntity3.setId("1000003");
//        userEntity3.setCreateTime(new Date());
//        userEntity3.setUserName("测试用户C");
//
//        UserEntity[] userEntity = {userEntity1,userEntity2,userEntity3};
////        try {
////            //保存任务信息到数据库
////            taskService.saveTask(userEntity,project,task);
//////            List<TaskLog> taskLogAllList = taskLogService.findTaskLogAllList();
//////            for (TaskLog log: taskLogAllList) {
//////                System.out.println(log.getContent());
//////            }
////        } catch (Exception e){
////            System.out.println(e.getMessage());
////        }
//    }
////
////    @Test
////    public void addTags(){
////        String taskId = "11111";
////        String[] oldTags = {"1","2"};
////        Tag tag = new Tag();
////        tag.setTagName("121212");
////        try {
////            int result = tagService.saveTag(tag,oldTags,taskId);
////        } catch (Exception e){
////            System.out.println(e.getMessage());
////        }
////    }
////
//    @Test
//    public void addRaly(){
//        Task task = null;
//        File file = new File();
//        file.setFileName("何少华");
//        Schedule schedule = null;
//        Share share = null;
//        TaskMember taskMember = new TaskMember();
//        String taskId = "1";
//        TaskLogVO taskLogVO = taskMemberService.saveTaskMember(task,file,share,schedule,taskMember,taskId);
//        File file1 = taskLogVO.getFile();
//        System.out.println(file1.getFileName());
//
//    }
//
//    @Test
//    public void aa(){
//        Task task = null;
//        File file = new File();
//        file.setFileName("何少华");
//        Share share = null;
//        Schedule schedule = null;
//        String taskId = "11111";
//        String taskRelyId = "471e6fca915f4a62a6fca20aa96e5f93";
//        TaskLogVO taskLogVO = taskMemberService.deleteTaskMemberById(task, file, share, schedule, taskId, taskRelyId);
//        System.out.println(taskLogVO.getFile().getFileName());
//    }
//
//    @Test
//    public void turnToFatherLevel(){
//        Task task = new Task();
//        task.setParentId("11111");
//        task.setTaskId("22222");
//        task.setTaskName("测试任务");
//        try {
//            TaskLogVO taskLogVO = taskService.turnToFatherLevel(task);
//            System.out.println(taskLogVO.getContent());
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void addTags(){
//        Tag tag = new Tag();
//        tag.setTagId(System.currentTimeMillis());
//        tag.setTagName("测试标签12");
//        String projectId = "1";
//        String taskId = "11111";
//        try {
//            //根据标签名称查询 当前存不存在数据库 如果存在直接绑定到当前任务,如果不存在则先插入标签 在绑定到当前任务
//            int countByTagName = tagService.findCountByTagName(projectId, tag.getTagName());
//            if(countByTagName == 0){
//                tag.setTagId(System.currentTimeMillis());
//                tagService.saveTag(tag);
//            }
//            //更新当前任务的标签信息
//            TaskLogVO taskLogVO = taskService.addTaskTags(tag, taskId,countByTagName);
//            if(taskLogVO.getResult() > 0){
//
//            } else{
//
//            }
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void removeTaskTag(){
//        String taskId = "11111";
//        Tag tag = new Tag();
//        tag.setTagId(Long.valueOf("1528867589095"));
//        Tag tag1 = new Tag();
//        tag1.setTagId(Long.valueOf("1528867589095"));
//        //tag1.setTagName("测试标签A");
//        Tag tag2 = new Tag();
//        tag2.setTagId(Long.valueOf("1528867565210"));
//        //tag2.setTagName("测试标签B");
//        Tag tag3 = new Tag();
//        tag3.setTagId(Long.valueOf("1528867545131"));
//        //tag3.setTagName("测试标签C");
//        Tag[] tags = {tag1,tag2,tag3};
//        try {
//            int result = taskService.removeTaskTag(tags,tag,taskId);
//        } catch (Exception e){
//        }
//    }
//
//    @Test
//    public void updateTaskRemindTime(){
//        Task task = new Task();
//        task.setTaskId("11111");
//        task.setStartTime(Long.valueOf("1536422400000"));
//        task.setRemind("任务开始时提醒");
//        try {
//            TaskLogVO taskLogVO = taskService.updateTaskRemindTime(task,null);
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void removeTaskStartAndEndTime(){
//        Task task = new Task();
//        task.setTaskId("11111");
//        task.setEndTime(Long.valueOf("1536422400000"));
//        try {
//            TaskLogVO taskLogVO = taskService.removeTaskStartAndEndTime(task);
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//
////    @Test
////    public void addTaskMember(){
////        Task task = new Task();
////        task.setTaskId("11111");
////        UserEntity userEntity1 = new UserEntity();
////        userEntity1.setId("1000001");
////        System.out.println(new Date());
////        userEntity1.setCreateTime(new Date());
////        userEntity1.setUserName("测试用户A");
////        UserEntity userEntity2 = new UserEntity();
////        userEntity2.setId("1000002");
////        userEntity2.setCreateTime(new Date());
////        userEntity2.setUserName("测0试用户B");
////        UserEntity userEntity3 = new UserEntity();;
////        userEntity3.setId("1000003");
////        userEntity3.setCreateTime(new Date());
////        userEntity3.setUserName("测试用户C");
////        UserEntity[] userEntity = {userEntity1};
////        try {
////            TaskLogVO taskLogVO = taskService.addTaskMember(task,userEntity);
////        } catch (Exception e){
////            System.out.println(e.getMessage());
////        }
////    }
//
//    @Test
//    public void removeTaskMember(){
//        Task task = new Task();
//        task.setTaskId("11111");
//        UserEntity userEntity1 = new UserEntity();
//        userEntity1.setId("1000001");
//        System.out.println(new Date());
//        userEntity1.setCreateTime(new Date());
//        userEntity1.setUserName("测试用户A");
//        UserEntity userEntity2 = new UserEntity();
//        userEntity2.setId("1000002");
//        userEntity2.setCreateTime(new Date());
//        userEntity2.setUserName("测0试用户B");
//        UserEntity[] userEntity = {userEntity1,userEntity2};
//        try {
//            TaskLogVO taskLogVO = taskService.removeTaskMember(task,userEntity1);
//            System.err.println(taskLogVO.getContent());
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void clickFabulous(){
//        Task task = new Task();
//        task.setFabulousCount(1);
//        task.setTaskId("11111");
//        try {
//            int result = taskService.clickFabulous(task);
//            if(result > 0){
//
//            }
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void cancelFabulous(){
//        Task task = new Task();
//        task.setFabulousCount(1);
//        task.setTaskId("11111");
//        try {
//            int result = taskService.cancelFabulous(task);
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//
////    @Test
////    public void addSubLevelTasks(){
////        Task currentTask = new Task();
////        currentTask.setTaskId("11111");
////        Task subLevel = new Task();
////        String projectId = "";
////        try {
////            TaskLogVO taskLogVO = taskService.addSubLevelTasks(currentTask,subLevel);
////        } catch (Exception e){
////            System.out.println(e.getMessage());
////        }
////    }
//
//    @Test
//    public void resetSubLevelTask(){
//        Task task = new Task();
//        task.setTaskId("fb86676889114caf9b68e4c3dfb637bc");
//        task.setTaskStatus("重新开始");
//        try {
//            TaskLogVO taskLogVO = taskService.resetAndCompleteSubLevelTask(task);
//        } catch (Exception e){
//            System.err.println(e.getMessage());
//        }
//    }
//
////    @Test
////    public void copyTask(){
////        Task task = new Task();
////        task.setTaskId("11111");
////        task.setProjectId("1");
////        task.setTaskMenuId("12");
////        try {
////            TaskLogVO taskLogVO = taskService.copyTask(task, relation, newTaskMenuVO);
////
////        } catch (Exception e){
////            System.out.println(e.getMessage());
////        }
////    }
//
//    @Test
//    public void collectTask(){
//        Task task = new Task();
//        task.setProjectId("1");
//        task.setTaskId("11111");
//        try {
//            int result = taskService.collectTask(task);
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void judgeCollectTask(){
//        String memberId = "12";
//        String taskId = "11111";
//        Task tak = new Task();
//        tak.setMemberId(memberId);
//        tak.setTaskId(taskId);
//        boolean b = taskService.judgeCollectTask(tak);
//        System.err.println(b);
//
//    }
//
//    @Test
//    public void cancelCollectTask(){
//        Task task = new Task();
//        task.setTaskId("11111");
//        try {
//            int result = taskService.cancelCollectTask(task);
//        } catch (Exception  e){
//            System.err.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void SettingUpPrivacyPatterns(){
//        Task task = new Task();
//        task.setTaskId("11111");
//        task.setPrivacyPattern(1);
//        try {
//            int result = taskService.SettingUpPrivacyPatterns(task);
//        } catch (Exception e){
//            System.err.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void findTaskById(){
//        String id = "11111";
//        try {
//            Task task = taskService.findTaskByTaskId(id);
//            System.err.println(task.getTaskName());
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void findProjectAllMember(){
//        Task task = new Task();
//        task.setTaskId("11111");
//        task.setProjectId("1");
////        String projectId  = "1";
////        try {
////            List<UserEntity> list = taskService.findProjectAllMember(projectId);
////            for (UserEntity userEntity : list) {
////                System.out.println(userEntity.getId());
////            }
////        } catch (Exception e){
////            System.out.println(e.getMessage());
////        }
//        Map<String, List<UserEntity>> userByIsExistTask = userService.findUserByIsExistTask(task);
//        List<UserEntity> existList = userByIsExistTask.get("existList");
//        List<UserEntity> notexistList = userByIsExistTask.get("notExistList");
//        for (UserEntity userEntity : existList) {
//            System.err.println("存在的用户的id:\t"+ userEntity.getId()+"\t");
//        }
//        for (UserEntity userEntity : notexistList) {
//            System.err.println("不存在的用户的id:\t"+ userEntity.getId()+"\t");
//        }
//    }
//
//    @Test
//    public void updateOther(){
//        Task task = new Task();
//        task.setTaskId("11111");
//        task.setOther("测试修改任务其他!");
//        try {
//            TaskLogVO taskLogVO = taskService.updateTask(task);
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//
////    @Test
////    public void intelligenceGroup(){
////        String status = "";
////        try {
////            List<Task> taskList = taskService.intelligenceGroup(status);
////            for (Task task: taskList) {
////                System.out.println(new Date(task.getCreateTime()));
////                System.out.println(task.getTaskId());
////            }
////        } catch (Exception e){
////            System.out.println(e.getMessage());
////            throw new AjaxException(e);
////        }
////    }
////
//    @Test
//    public void taskMenu(){
//        String menuId = "1";
//        try{
//            List<Task> taskList = taskService.taskMenu(menuId);
//            for (Task task: taskList) {
//                System.out.println(new Date(task.getCreateTime()));
//                System.out.println(task.getTaskId());
//            }
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
////
////    @Test
////    public void findTaskByExecutor(){
////        String uId= "4";
////        try {
////            List<Task> taskList = taskService.findTaskByExecutor(uId);
////            for (Task task: taskList) {
////                System.out.println(task.getTaskId());
////            }
////        } catch (Exception e){
////            System.out.println(e.getMessage());
////        }
////    }
////
//    @Test
//    public void waitClaimTask(){
//        String projectId= "1";
//        try {
//            List<Task> taskList = taskService.waitClaimTask(projectId);
//            for (Task task: taskList) {
//                System.out.println(task.getTaskId());
//            }
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//    @Test
//    public void removeExecutor(){
//        String taskId = "ce032bf23f324630afe8d26053a37a35";
//        try {
//            taskService.removeExecutor(taskId);
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//
////    @Test
////    public void updateTaskExecutor(){
////        String uId = "4";
////        String taskId = "11111";
////        try {
////            int result = taskService.updateTaskExecutor(taskId,uId);
////        } catch (Exception e){
////            System.out.println(e.getMessage());
////        }
////    }
//    @Test
//    public void addAndRemoveTaskMember(){
//        Task task = new Task();
//        task.setTaskId("11111");
//
//        UserEntity userEntity1 = new UserEntity();
//        userEntity1.setId("1000001");
//        userEntity1.setCreateTime(new Date());
//        userEntity1.setUserName("测试用户A");
//
//        UserEntity userEntity2 = new UserEntity();
//        userEntity2.setId("1000002");
//        userEntity2.setCreateTime(new Date());
//        userEntity2.setUserName("测0试用户B");
//
//        UserEntity userEntity3 = new UserEntity();;
//        userEntity3.setId("1000003");
//        userEntity3.setCreateTime(new Date());
//        userEntity3.setUserName("测试用户C");
//
//        UserEntity[] addUserEntity = {userEntity1,userEntity2,userEntity3};
//
//
//        UserEntity userEntity11 = new UserEntity();
//        userEntity11.setId("1000001");
//        userEntity11.setCreateTime(new Date());
//        userEntity11.setUserName("测试用户A");
//
//        UserEntity userEntity22 = new UserEntity();
//        userEntity22.setId("1000002");
//        userEntity22.setCreateTime(new Date());
//        userEntity22.setUserName("测0试用户B");
//
//
//        UserEntity[] removeUserEntity = {userEntity11,userEntity22};
//        try {
//            TaskLogVO taskLogVO = taskService.addAndRemoveTaskMember(task,addUserEntity,removeUserEntity);
//            System.err.println(taskLogVO.getContent());
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void test(){
//        List<UserEntity> projectAllMember = taskService.findProjectAllMember("1ebf849e773e4335babcb52202e4f420", "117194df60fdcf4928ec4edab70508c");
//        for (UserEntity userEntity: projectAllMember) {
//            System.out.println(userEntity.getId());
//        }
//    }
//
////    @Test
////    public void recoveryTask(){
////        String taskId = "11111";
////        String menuId = "175d30a321bf4d749fab79a81b2eddc0";
////        try {
////            taskService.recoveryTask(taskId,menuId);
////        } catch (Exception e){
////            throw new AjaxException(e);
////        }
////    }
//
//    @Test
//    public void test1(){
//        String menuId  = "d229c5f3a73749829b0d9b32ade26ce6";
//        List<Task> tasks = taskService.taskMenu(menuId);
//        for (Task task: tasks) {
//            System.out.println(task.getTaskName()+"\t"+task.getTaskMember().getMemberImg());
//        }
//    }
//
//}
