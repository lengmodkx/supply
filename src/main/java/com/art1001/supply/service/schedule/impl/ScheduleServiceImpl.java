package com.art1001.supply.service.schedule.impl;

import com.art1001.supply.entity.base.Pager;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.schedule.*;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.tag.TagRelation;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.mapper.schedule.ScheduleMapper;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.schedule.ScheduleService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.DateUtils;
import com.art1001.supply.util.IdGen;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * scheduleServiceImpl
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper,Schedule> implements ScheduleService {

    /**
     * scheduleMapper接口
     */
    @Resource
    private ScheduleMapper scheduleMapper;

    @Resource
    private UserService userService;

    @Resource
    private LogService logService;

    @Resource
    private TagRelationService tagRelationService;

    @Resource
    private TagService tagService;

    /**
     * 查询分页schedule数据
     *
     * @param pager 分页对象
     * @return
     */
    @Override
    public List<Schedule> findSchedulePagerList(Pager pager) {
        return scheduleMapper.findSchedulePagerList(pager);
    }

    /**
     * 通过id获取单条schedule数据
     *
     * @param id
     * @return
     */
    @Override
    public Schedule findScheduleById(String id) {
        Schedule scheduleById = scheduleMapper.findScheduleById(id);
        List<Tag> tags =new ArrayList<>();
        List<TagRelation>  tagRelations =tagRelationService.findTagRelationByScheduleId(id);
        if (tagRelations!=null&&tagRelations.size()>0){
            for (TagRelation tagr: tagRelations) {
                tags.add(tagService.findById((int) tagr.getTagId()));
            }
        }
        scheduleById.setTagList(tags);
        return scheduleById;
    }

    /**
     * 通过id删除schedule数据
     *
     * @param id
     */
    @Override
    public void deleteScheduleById(String id) {
        scheduleMapper.deleteScheduleById(id);
    }

    /**
     * 修改schedule数据
     *
     * @param schedule
     */
    @Override
    public Log updateSchedule(Schedule schedule) {
        StringBuilder content = new StringBuilder();
        //更新开始时间  结束时间
        if (!StringUtils.isEmpty(schedule.getScheduleName())) {
            content.append(ScheduleLogFunction.A.getName()).append(" ").append(ScheduleLogFunction.D.getName());
        }

        //更新重复规则
        if (!StringUtils.isEmpty(schedule.getRepeat())) {
            content.append(ScheduleLogFunction.A.getName()).append(" ").append(ScheduleLogFunction.E.getName());
        }

        //更新提醒模式
        if (!StringUtils.isEmpty(schedule.getRemind())) {
            content.append(ScheduleLogFunction.A.getName()).append(" ").append(ScheduleLogFunction.F.getName());
        }

        //更新日程地址
        if (!StringUtils.isEmpty(schedule.getAddress())) {
            content.append(ScheduleLogFunction.A.getName()).append(" ").append(ScheduleLogFunction.G.getName());
        }

        schedule.setUpdateTime(System.currentTimeMillis());
        Log log = new Log();
        if (!StringUtils.isEmpty(content.toString())) {
            log = logService.saveLog(schedule.getScheduleId(), content.toString(), 3);
        }
        scheduleMapper.updateSchedule(schedule);
        return log;
    }

    /**
     * 保存schedule数据
     * @param schedule
     */
    @Override
    public void saveSchedule(Schedule schedule) {
        schedule.setScheduleId(IdGen.uuid());
        schedule.setMemberId(ShiroAuthenticationManager.getUserId());
        schedule.setCreateTime(System.currentTimeMillis());
        this.save(schedule);
    }

    /**
     * 获取所有schedule数据
     *
     * @return
     */
    @Override
    public List<Schedule> findScheduleAllList() {
        return scheduleMapper.findScheduleAllList();
    }

    @Override
    public List<Schedule> findByIds(String[] scheduleIds) {
        return scheduleMapper.findByIds(scheduleIds);
    }

    @Override
    public List<ScheduleVo> findScheduleGroupByCreateTime(Long currTime, String projectId) {
        return scheduleMapper.findScheduleGroupByCreateTime(currTime, projectId);
    }

    /**
     * 查询出该用户参与的近三天的日程
     * @return
     */
    @Override
    public List<Schedule> findScheduleByUserIdAndByTreeDay() {
        return scheduleMapper.findScheduleByUserIdAndByTreeDay(ShiroAuthenticationManager.getUserId());
    }

    /**
     * 数据:根据项目查询出该项目下的所有日程信息
     * @param projectId 项目id
     * @return 日程的实体集合
     */
    @Override
    public List<Schedule> findScheduleListByProjectId(String projectId) {
        List<Schedule> schedules = scheduleMapper.findScheduleListByProjectId(projectId);

        Iterator<Schedule> iterator = schedules.iterator();
        while(iterator.hasNext()){
            Schedule next = iterator.next();

            //查询日程的标签
            List<Tag> tags =new ArrayList<>();
            List<TagRelation>  tagRelations =tagRelationService.findTagRelationByScheduleId(next.getScheduleId());
            if (tagRelations!=null&&tagRelations.size()>0){
                for (TagRelation tagr: tagRelations) {
                    tags.add(tagService.findById((int) tagr.getTagId()));
                }
            }
            next.setTagList(tags);

            if(next.getPrivacyPattern() == 1){
                if(!Arrays.asList(next.getMemberIds().split(",")).contains(ShiroAuthenticationManager.getUserId())){
                    iterator.remove();
                }
            }
        }
        return schedules;
    }

    /**
     * 查询以前的日程
     *
     * @param currTime       当前的时间
     * @param identification 标识
     * @return 返回日程信息
     */
    @Override
    public List<Schedule> findBeforeSchedule(long currTime, String projectId, int identification) {
        return scheduleMapper.findBeforeSchedule(currTime, projectId, identification);
    }

    /**
     * 添加或者移除参与者
     *
     * @param scheduleId 日程的id
     * @param memberIds  参与者信息
     */
    @Override
    public void updateMembers(String scheduleId, String memberIds) {
        //查询出当前文件中的 参与者id
        Schedule scheduleById = scheduleMapper.findScheduleById(scheduleId);

        //log日志
        Log log = new Log();
        StringBuilder logContent = new StringBuilder();

        //将数组转换成集合
        List<String> oldJoin = Arrays.asList(scheduleById.getMemberIds().split(","));
        List<String> newJoin = Arrays.asList(memberIds.split(","));

        //比较 oldJoin 和 newJoin 两个集合的差集  (移除)
        List<String> reduce1 = oldJoin.stream().filter(item -> !newJoin.contains(item)).collect(Collectors.toList());
        if (reduce1 != null && reduce1.size() > 0) {
            logContent.append(TaskLogFunction.B.getName()).append(" ");
            for (String uId : reduce1) {
                UserEntity userEntity = userService.findById(uId);
                logContent.append(userEntity.getUserName()).append(" ");
            }
        }
        //比较 newJoin  和 oldJoin 两个集合的差集  (添加)
        List<String> reduce2 = newJoin.stream().filter(item -> !oldJoin.contains(item)).collect(Collectors.toList());
        if (reduce2 != null && reduce2.size() > 0) {
            logContent.append(TaskLogFunction.C.getName()).append(" ");
            for (String uId : reduce2) {
                UserEntity userEntity = userService.findById(uId);
                logContent.append(userEntity.getUserName()).append(" ");
            }
        }

        //如果没有参与者变动直接返回
        if ((reduce1 == null && reduce1.size() == 0) && (reduce2 == null && reduce2.size() == 0)) {
            return;
        } else {
            Schedule schedule = new Schedule();
            schedule.setScheduleId(scheduleId);
            schedule.setMemberIds(memberIds);
            schedule.setUpdateTime(System.currentTimeMillis());
            scheduleMapper.updateSchedule(schedule);
            log = logService.saveLog(scheduleId, logContent.toString(), 2);
        }
    }

    /**
     * 更新日程的开始时间或者结束时间
     *
     * @param scheduleId 日程id
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @return 操作的日志信息
     */
    @Override
    public Log updateScheduleStartAndEndTime(String scheduleId, String startTime, String endTime) {
        Schedule schedule = new Schedule();
        schedule.setScheduleId(scheduleId);
        StringBuilder content = new StringBuilder();
        //日期字符串转为毫秒数
        if (!StringUtils.isEmpty(startTime)) {
            schedule.setStartTime(DateUtils.strToLong(startTime));
            content.append(ScheduleLogFunction.A.getName()).append(" ").append(ScheduleLogFunction.B.getName());
        }
        if (!StringUtils.isEmpty(endTime)) {
            schedule.setEndTime(DateUtils.strToLong(endTime));
            content.append(ScheduleLogFunction.A.getName()).append(" ").append(ScheduleLogFunction.C.getName());
        }
        schedule.setUpdateTime(System.currentTimeMillis());
        scheduleMapper.updateSchedule(schedule);
        Log log = logService.saveLog(scheduleId, content.toString(), 3);
        return log;
    }

    /**
     * 清空日程的id
     *
     * @param scheduleId 日程的id
     */
    @Override
    public void clearScheduleTag(String scheduleId) {
        scheduleMapper.clearScheduleTag(scheduleId);
    }

    /**
     * 根据日程id 查询出该日程的名称
     *
     * @param publicId 日程id
     * @return
     */
    @Override
    public String findScheduleNameById(String publicId) {
        return scheduleMapper.findScheduleNameById(publicId);
    }

    /**
     * 查询出未来的日程
     * lable(1:未来 0:过去)
     *
     * @return
     */
    @Override
    public List<ScheduleVo> afterSchedule(int lable) {
        List<ScheduleVo> scheduleVos = scheduleMapper.afterSchedule(System.currentTimeMillis(), lable, ShiroAuthenticationManager.getUserId());
        for (ScheduleVo s : scheduleVos) {
            if (DateUtils.getDateStr().equals(s.getDate())) {
                s.setDate("今天");
                continue;
            }
            if (lable == 1) {
                if (DateUtils.getAfterDay(DateUtils.getDateStr(), 1, "yyyy-MM-dd", "yyyy-MM-dd").equals(s.getDate())) {
                    s.setDate("明天");
                }
            }
        }
        return scheduleVos;
    }

    /**
     * 查询出近三天的日程
     *
     * @param uId 当前用户id
     * @return
     */
    @Override
    public List<Schedule> findScheduleByUserIdAndThreeDay(String uId) {
        return scheduleMapper.findScheduleByUserIdAndThreeDay(uId);
    }

    /**
     * 查询出日历上的所有日程
     *
     * @return
     */
    @Override
    public List<Schedule> findCalendarSchedule() {
        return scheduleMapper.findCalendarSchedule(ShiroAuthenticationManager.getUserId());
    }

    /**
     * 查询出在该项目回收站中的日程
     *
     * @param projectId 项目id
     * @return
     */
    @Override
    public List<RecycleBinVO> findRecycleBin(String projectId) {
        return scheduleMapper.findRecycleBin(projectId);
    }

    /**
     * 将日程移入到回收站
     *
     * @param scheduleId 日程id
     */
    @Override
    public void moveToRecycleBin(String scheduleId) {
        scheduleMapper.moveToRecycleBin(scheduleId, System.currentTimeMillis());
    }

    /**
     * 恢复日程
     *
     * @param scheduleId 日程id
     */
    @Override
    public void recoverySchedule(String scheduleId) {
        scheduleMapper.recoverySchedule(scheduleId);
        Log log = logService.saveLog(scheduleId, TaskLogFunction.A29.getName(), 1);
    }

    /**
     * 根据日程id 查询出该日程的所有参与者信息
     *
     * @param scheduleId 日程id
     * @return
     */
    @Override
    public String findUidsByScheduleId(String scheduleId) {
        return scheduleMapper.findUidsByScheduleId(scheduleId);
    }

    /**
     * 查询日程的部分信息 (日程名称,开始时间,结束时间,项目名称)
     *
     * @param id 日程id
     * @return
     */
    @Override
    public ScheduleApiBean findScheduleApiBean(String id) {
        return scheduleMapper.selectScheduleApiBean(id);
    }

    /**
     * 根据月份分组查询日程
     * @param projectId 项目id
     * @return
     */
    @Override
    public List<Schedule> findScheduleGroup(String projectId) {
        List<Schedule> scheduleGroup = scheduleMapper.findScheduleGroup(System.currentTimeMillis(), projectId);


       /* for (Schedule s: scheduleGroup) {
            //查询日程的标签
            List<Tag> tags =new ArrayList<>();
            List<TagRelation>  tagRelations =tagRelationService.findTagRelationByScheduleId(s.getScheduleId());
            if (Stringer.isNotNullOrEmpty(tagRelations)){
                for (TagRelation tagr: tagRelations) {
                    tags.add(tagService.findById((int) tagr.getTagId()));
                }
            }
            s.setTagList(tags);
        }*/
        return scheduleGroup;
    }

    /**
     * 检测成员时间范围的合法性
     * @param userId 用户id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 是否合法
     */
    @Override
    public List<Schedule> testMemberTimeRange(String userId, Long startTime, Long endTime) {
        List<Schedule> conflictSchedules = new ArrayList<>();
        //查询出和该用户关联的日程id集合
        List<String> scheduleIds = scheduleMapper.selectList(new QueryWrapper<Schedule>().select("schedule_id").eq("member_id", userId).or().apply("FIND_IN_SET({0},member_ids)", userId)).stream().map(Schedule::getScheduleId).collect(Collectors.toList());
        //查询出和改用和关联的日程信息
        List<Schedule> schedules = scheduleMapper.selectList(new QueryWrapper<Schedule>().select("schedule_id","schedule_name","start_time","end_time").in("schedule_id",scheduleIds));
        schedules.forEach(item -> {
            if(!(startTime > item.getEndTime() || endTime < item.getStartTime())){
                conflictSchedules.add(item);
            }
        });
        return conflictSchedules;
    }

    /**
     * 获取日程信息 根据月份分组
     * @param projectId 项目id
     * @return 日程信息
     */
    @Override
    public List<ScheduleVo> getBeforeByMonth(String projectId) {
        return scheduleMapper.getBeforeByMonth(projectId);
    }

    /**
     * 获取日程信息 根据月份分组
     * @param projectId 项目id
     * @return 日程信息集合
     */
    @Override
    public List<Schedule> getAfterBind(String projectId) {
        return scheduleMapper.getAfterBind(projectId);
    }

    /**
     * 查询出关于用户的所有未来日程信息 按照日分组
     * @return 日程VO集合
     */
    @Override
    public List<ScheduleVo> findMe() {
        return scheduleMapper.selectMe(ShiroAuthenticationManager.getUserId(),System.currentTimeMillis());
    }

    /**
     * 查询出和当前登录用户有关日程的月份信息
     * @return 月份集合
     */
    @Override
    public List<String> findScheduleMonth() {
        return scheduleMapper.findScheduleMonth(ShiroAuthenticationManager.getUserId(),System.currentTimeMillis());
    }

    /**
     * 根据日程的月份信息获取日程
     * @param month 月份
     * @return 日程集合
     */
    @Override
    public List<Schedule> findByMonth(String month) {
        return scheduleMapper.findByMonth(month,ShiroAuthenticationManager.getUserId(),System.currentTimeMillis());
    }

    /**
     * 获取一个项目的日历日程信息 (version 2.0)
     * @param projectId 项目id
     * @return 日历日程信息
     */
    @Override
    public List<Schedule> getCalendarSchedule(String projectId) {
        return scheduleMapper.selectCalendarSchedule(projectId);
    }

    /**
     * 获取一个用户的日历日程信息
     * @param userId 用户id
     * @return 日程信息
     */
    @Override
    public List<Schedule> relevant(String userId) {
        return scheduleMapper.relevant(userId);
    }

    /**
     * 获取绑定该标签的所有日程信息
     * @param tagId 标签id
     * @return 日程集合
     */
    @Override
    public List<Schedule> getBindTagInfo(Long tagId) {
        return scheduleMapper.selectBindTagInfo(tagId);
    }

    /**
     * 检查该日程存不存在
     * @param scheduleId 日程id
     * @return 是否存在
     */
    @Override
    public Boolean checkIsExist(String scheduleId) {
        return scheduleMapper.selectCount(new QueryWrapper<Schedule>().lambda().eq(Schedule::getScheduleId, scheduleId)) > 0;
    }

    /**
     * 根据日程id查询出该日程的项目id
     * 如果没有项目id 则返回null
     * @param scheduleId 日程id
     * @return 项目id
     */
    @Override
    public String getProjectId(String scheduleId) {
        if(!this.checkIsExist(scheduleId)){
            throw new ServiceException("该日程不存在!");
        }
        //生成条件表达式对象
        LambdaQueryWrapper<Schedule> select = new QueryWrapper<Schedule>().lambda().eq(Schedule::getScheduleId, scheduleId).select(Schedule::getProjectId);
        String projectId = scheduleMapper.selectOne(select).getProjectId();
        return projectId;
    }

    @Override
    public String[] getJoinAndCreatorId(String publicId) {
        LambdaQueryWrapper<Schedule> select = new QueryWrapper<Schedule>().lambda()
                .select(Schedule::getMemberIds, Schedule::getMemberId)
                .eq(Schedule::getScheduleId, publicId);

        Schedule one = this.getOne(select);
        if(one != null){
            if(one.getMemberIds() != null){
                StringBuilder userIds = new StringBuilder(one.getMemberIds());
                if(StringUtils.isNotEmpty(one.getMemberId())){
                    userIds.append(",").append(one.getMemberId());
                }
                return userIds.toString().split(",");
            } else if(one.getMemberId() != null){
                return new String[]{one.getMemberId()};
            }
        }
        return new String[0];
    }

    /**
     * 获取日程列表
     * @param projectId
     * @param memberId
     * @return
     */
    @Override
    public List<ScheduleListVO> getScheduleList(String projectId, String memberId) {
        List<Schedule>scheduleList=scheduleMapper.getScheduleList(projectId,memberId);
        List<ScheduleListVO>scheduleListVOS= Lists.newArrayList();


        Optional.ofNullable(scheduleList).ifPresent(schedules->{
            List<ScheduleSimpleInfoVO>scheduleSimpleInfoVOS=Lists.newArrayList();
            ScheduleListVO scheduleListVO=new ScheduleListVO();
            schedules.forEach(schedule -> {
                ScheduleSimpleInfoVO scheduleSimpleInfoVO=ScheduleSimpleInfoVO.builder().build();
                //日期处理，Long转换成MM-dd类型
                DateTimeFormatter time = DateTimeFormatter.ofPattern("MM月dd日");
                DateTimeFormatter time2 = DateTimeFormatter.ofPattern("HH:mm");
                String createTime = time.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(schedule.getCreateTime()), ZoneId.systemDefault()));
                String startTime = time2.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(schedule.getStartTime()), ZoneId.systemDefault()));
                String endTime = time2.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(schedule.getEndTime()), ZoneId.systemDefault()));

                //赋值
                if (schedule.getIsAllday()==1) {
                    scheduleSimpleInfoVO.setStartTime("全天");
                }else{
                    scheduleSimpleInfoVO.setStartTime(startTime);
                    scheduleSimpleInfoVO.setEndTime(endTime);
                }
                scheduleSimpleInfoVO.setProjectId(schedule.getProjectId());
                scheduleSimpleInfoVO.setProjectName(schedule.getProjectName());
                scheduleSimpleInfoVO.setScheduleId(schedule.getScheduleId());
                scheduleSimpleInfoVO.setScheduleName(schedule.getScheduleName());
                scheduleSimpleInfoVOS.add(scheduleSimpleInfoVO);
                scheduleListVO.setCreateTime(createTime);
                scheduleListVO.setScheduleSimpleInfoVOS(scheduleSimpleInfoVOS);
                });
            scheduleListVOS.add(scheduleListVO);
        });
        return scheduleListVOS;
    }
}
