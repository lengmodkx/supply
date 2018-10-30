package com.art1001.supply.timer;

import com.art1001.supply.entity.chat.Chat;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.service.chat.ChatService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author heshaohua
 * @Title: Timer
 * @Description: TODO
 * @date 2018/10/23 16:28
 **/
@Slf4j
@Component
public class Timer {

    @Resource
    private TaskService taskService;

    @Resource
    private FileService fileService;

    @Resource
    private ScheduleService scheduleService;

    @Resource
    private ShareService shareService;

    @Resource
    private ChatService chatService;

    @Resource
    private ProjectMemberService projectMemberService;

    @Resource
    private RelationService relationService;

    @Resource
    private TagService tagService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void clearProjectInfo(){
        taskService.remove(new QueryWrapper<Task>().notInSql("project_id", "select project_id from prm_project"));
        log.info("删除没有项目的任务信息");

        fileService.remove(new QueryWrapper<File>().notInSql("project_id", "select project_id from prm_project"));
        log.info("删除没有项目的文件信息");

        scheduleService.remove(new QueryWrapper<Schedule>().notInSql("project_id", "select project_id from prm_project"));
        log.info("删除没有项目的日程信息");

        shareService.remove(new QueryWrapper<Share>().notInSql("project_id", "select project_id from prm_project"));
        log.info("删除没有项目的分享信息");

        //清除群聊信息
        chatService.remove(new QueryWrapper<Chat>().notInSql("project_id","select project_id from prm_project"));

        //清除项目的成员信息
        projectMemberService.remove(new QueryWrapper<ProjectMember>().notInSql("project_id","select project_id from prm_project"));

        //清除项目的分组/菜单信息
        relationService.remove(new QueryWrapper<Relation>().notInSql("project_id","select project_id from prm_project"));

        //清除标签信息
        tagService.remove(new QueryWrapper<Tag>().notInSql("project_id","select project_id from prm_project"));
    }

}
