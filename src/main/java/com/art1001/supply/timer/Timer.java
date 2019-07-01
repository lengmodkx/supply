package com.art1001.supply.timer;

import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.chat.Chat;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.quartz.QuartzInfo;
import com.art1001.supply.entity.relation.Relation;
import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.entity.task.TaskRemindRule;
import com.art1001.supply.entity.user.UserNews;
import com.art1001.supply.quartz.QuartzService;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.chat.ChatService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.quartz.QuartzInfoService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.task.TaskRemindRuleService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.service.user.UserNewsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

    @Resource
    private PublicCollectService publicCollectService;

    @Resource
    private LogService logService;

    @Resource
    private UserNewsService userNewsService;

    @Resource
    private BindingService bindingService;

    @Resource
    private TaskRemindRuleService taskRemindRuleService;

    @Resource
    private QuartzInfoService quartzInfoService;

    @Resource
    private QuartzService quartzService;

    private static final String PROJECT_SQL = "select project_id from prm_project";
    private static final String TASK_SQL = "SELECT task_id FROM prm_task";
    private static final String FILE_SQL = "SELECT file_id FROM prm_file";
    private static final String SHARE_SQL = "SELECT id FROM prm_share";
    private static final String SCHEDULE_SQL = "SELECT schedule_id from prm_schedule";

    @Scheduled(cron = "0 1 0 * * ?")
    public void clearProjectInfo(){
        taskService.remove(new QueryWrapper<Task>().notInSql("project_id", PROJECT_SQL));
        log.info("删除没有项目的任务信息");

//        fileService.remove(new QueryWrapper<File>().notInSql("project_id", PROJECT_SQL));
//        log.info("删除没有项目的文件信息");

        scheduleService.remove(new QueryWrapper<Schedule>().notInSql("project_id", PROJECT_SQL));
        log.info("删除没有项目的日程信息");

        shareService.remove(new QueryWrapper<Share>().notInSql("project_id", PROJECT_SQL));
        log.info("删除没有项目的分享信息");

        //清除群聊信息
        chatService.remove(new QueryWrapper<Chat>().notInSql("project_id",PROJECT_SQL));

        //清除项目的成员信息
        projectMemberService.remove(new QueryWrapper<ProjectMember>().notInSql("project_id",PROJECT_SQL));

        //清除项目的分组/菜单信息
        relationService.remove(new QueryWrapper<Relation>().notInSql("project_id",PROJECT_SQL));

        //清除标签信息
        tagService.remove(new QueryWrapper<Tag>().notInSql("project_id",PROJECT_SQL));
    }

    /**
     * 清除任务的quartz 信息
     */
    @Scheduled(cron = "0 3 0 * * ?")
    public void clearRemindInfo(){
        List<TaskRemindRule> reminds = taskRemindRuleService.list(new QueryWrapper<TaskRemindRule>().select("id").notInSql("task_id", TASK_SQL));
        List<String> remindIds = new ArrayList<>();
        reminds.forEach(item -> {
            remindIds.add(item.getId());
        });
        List<QuartzInfo> quartzInfos = quartzInfoService.list(new QueryWrapper<QuartzInfo>().in("remind_id", remindIds));

        quartzInfos.forEach(item -> {
            TriggerKey triggerKey = new TriggerKey(item.getJobName(),item.getTriggerGroup());
            JobKey jobKey = new JobKey(item.getJobName(),item.getJobGroup());
            try {
                quartzService.removeJob(quartzService.getScheduler(),triggerKey,jobKey);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        });

        //清除任务的提醒模式
        taskRemindRuleService.remove(new QueryWrapper<TaskRemindRule>().notInSql("task_id",TASK_SQL));

        //清除提醒模式的quartz信息
        quartzInfoService.remove(new QueryWrapper<QuartzInfo>().notInSql("remind_id","select * from (select id from prm_task_remind) as A"));
    }

    /**
     * 每天0点5分 清除(任务,文件,分享,日程) 的残留信息
     */
    @Scheduled(cron = "0 5 0 * * ?")
    public void clearItemInfo(){
        //清除收藏数据
        publicCollectService.remove(new QueryWrapper<PublicCollect>()
                .notInSql("public_id",TASK_SQL)
                .notInSql("public_id",FILE_SQL)
                .notInSql("public_id",SHARE_SQL)
                .notInSql("public_id",SCHEDULE_SQL));

        //清除日志数据
        logService.remove(new QueryWrapper<Log>()
                .notInSql("public_id",TASK_SQL)
                .notInSql("public_id",FILE_SQL)
                .notInSql("public_id",SHARE_SQL)
                .notInSql("public_id",SCHEDULE_SQL));

        //删除用户的消息信息
        userNewsService.remove(new QueryWrapper<UserNews>()
                .notInSql("news_public_id",TASK_SQL)
                .notInSql("news_public_id",FILE_SQL)
                .notInSql("news_public_id",SHARE_SQL)
                .notInSql("news_public_id",SCHEDULE_SQL));

        //清除关联信息
        bindingService.remove(new QueryWrapper<Binding>()
                .notInSql("public_id",TASK_SQL)
                .notInSql("public_id",FILE_SQL)
                .notInSql("public_id",SHARE_SQL)
                .notInSql("public_id",SCHEDULE_SQL));

        //清除被关联信息
        bindingService.remove(new QueryWrapper<Binding>()
                .notInSql("bind_id",TASK_SQL)
                .notInSql("bind_id",FILE_SQL)
                .notInSql("bind_id",SHARE_SQL)
                .notInSql("bind_id",SCHEDULE_SQL));

        //清除子任务信息
        taskService.remove(new QueryWrapper<Task>().notInSql("parent_id","select * from (select task_id from prm_task) as a").ne("parent_id","0"));
    }

}
