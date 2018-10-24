package com.art1001.supply.timer;

import com.art1001.supply.service.task.TaskService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @Title: Timer
 * @Description: TODO
 * @date 2018/10/23 16:28
 **/
@Slf4j
public class Timer {

    @Resource
    private TaskService taskService;

//    public void clearProjectInfo(){
//        taskService.remove(new QueryWrapper<Task>().notInSql("project_id","select project_id from prm_project"));
//        log.info("删除没有项目的任务信息:\t"+ taskService.removeNoProject() +"\t条");
//        log.info("删除没有项目的文件信息:\t"+ fileService.removeNoProject() +"\t条");
//        log.info("删除没有项目的分享信息:\t"+ shareService.removeNoProject() +"\t条");
//        log.info("删除没有项目的日程信息:\t"+ scheduleService.removeNoProject() +"\t条");
//
//        //清除群聊信息
//        chatService.clear();
//
//        //清除项目的成员信息
//        int a = projectMemberService.clear();
//
//        //清除项目的分组/菜单信息
//        int b = relationService.clear();
//
//        //清除标签信息
//        int c = tagService.clear();
//    }

}
