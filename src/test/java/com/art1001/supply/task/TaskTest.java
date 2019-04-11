package com.art1001.supply.task;

import com.art1001.supply.ApplicationTests;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.task.Task;
import com.art1001.supply.mapper.task.TaskMapper;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description
 * @Date:2019/4/11 13:23
 * @Author heshaohua
 **/
public class TaskTest extends ApplicationTests {

    @Resource
    private TaskMapper taskmapper;

    /**
     * 测试我执行的任务
     */
    @Test
    public void testMeExecuteTask(){
        List<Task> tasks = taskmapper.selectExecuteAndOrder(false, "dueDate", "c0ef5cfb273a47d7b81394f9d00ceb1d");
        tasks.forEach(item -> {
            System.out.println(item.toString());
        });
    }

    /**
     * 测试我参与的任务数据
     */
    @Test
    public void testMeJoinTask(){
        List<Task> tasks = taskmapper.selectJoinAndOrder(false, "dueDate", "c0ef5cfb273a47d7b81394f9d00ceb1d");
        tasks.forEach(item -> {
            System.out.println(item.toString());
        });
    }

    /**
     * 测试我创建的任务数据
     */
    @Test
    public void testMeCreatedTask(){
        List<Task> tasks = taskmapper.selectJoinAndOrder(false, "dueDate", "c0ef5cfb273a47d7b81394f9d00ceb1d");
        tasks.forEach(item -> {
            System.out.println(item.toString());
        });
    }

    @Test
    public void testMeExecuteByProject(){
        List<Project> a = taskmapper.selectExecuteOrderProject(false, "c0ef5cfb273a47d7b81394f9d00ceb1d");
        a.forEach(item -> {
            System.out.println(item.toString());
        });
    }

    @Test
    public void testRecentThing(){
        List<Task> c0ef5cfb273a47d7b81394f9d00ceb1d = taskmapper.findByUserIdAndByTreeDay("c0ef5cfb273a47d7b81394f9d00ceb1d");
        c0ef5cfb273a47d7b81394f9d00ceb1d.forEach(item -> {
            System.out.println(item.toString());
        });
    }
}
