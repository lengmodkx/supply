package com.art1001.supply.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.ApplicationTests;
import com.art1001.supply.api.TaskApi;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.service.task.TaskService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @Description
 * @Date:2019/3/22 19:09
 * @Author heshaohua
 **/
public class JsonTest extends ApplicationTests {

    @Resource
    private TaskService taskService;

    @Test
    public void testJson(){
        System.out.println(JSON.toJSONString(taskService.getOne(new QueryWrapper<Task>().eq("task_id", "f623124bc3de435da14a4cb57af22697"))));
    }
}
