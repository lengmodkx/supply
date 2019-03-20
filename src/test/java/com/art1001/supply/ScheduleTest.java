package com.art1001.supply;

import com.art1001.supply.entity.schedule.Schedule;
import com.art1001.supply.service.schedule.ScheduleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Date:2019/3/20 14:15
 * @Author heshaohua
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduleTest {

    @Resource
    private ScheduleService scheduleService;

    /**
     * 测试用户日程时间合法性
     */
    @Test
    public void testMemberTimeRange(){
//        List<Schedule> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Schedule schedule = new Schedule();
//            schedule.setScheduleName("测试日程" + i);
//            schedule.setMemberId("c0ef5cfb273a47d7b81394f9d00ceb1d");
//            schedule.setStartTime(System.currentTimeMillis());
//            schedule.setEndTime(System.currentTimeMillis() + 60*1000*60*24L);
//            schedule.setProjectId("4a5aea84a00a4d1db79f3b5434a37265");
//            list.add(schedule);
//        }
//        scheduleService.saveBatch(list);
        scheduleService.testMemberTimeRange("c0ef5cfb273a47d7b81394f9d00ceb1d",System.currentTimeMillis(),System.currentTimeMillis());
    }
}
