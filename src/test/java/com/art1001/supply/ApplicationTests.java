package com.art1001.supply;

import com.art1001.supply.entity.tag.TagRelation;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.art1001.supply.service.task.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Resource
    private TaskService taskService;

    @Resource
    private TagRelationService tagRelationService;
    @Test
    public void testSql(){
        String tagIds=null;
        Arrays.stream(tagIds.split(",")).forEach(tagId->{
            TagRelation tagRelation = new TagRelation();
            tagRelation.setTagId(Long.valueOf(tagId));
            tagRelation.setTaskId("xxxxxx");
            tagRelationService.save(tagRelation);
        });

    }

    @Test
    public void test1(){
        SimpleDateFormat format = new SimpleDateFormat("ss mm HH dd MM ? yyyy");
        Date date = new Date();
        System.out.println(format.format(date));
    }
}
