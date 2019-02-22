package com.art1001.supply;

import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.entity.role.ResourcesRole;
import com.art1001.supply.entity.tag.TagRelation;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.resource.ResourceService;
import com.art1001.supply.service.role.ResourcesRoleService;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.art1001.supply.service.task.TaskService;
import com.art1001.supply.util.IdGen;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.BatchOptions;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Resource
    private TaskService taskService;

    @Resource
    private TagRelationService tagRelationService;

    @Resource
    private ResourceService resourceService;

    @Resource
    private ResourcesRoleService resourcesRoleService;

    @Resource
    private FileService fileService;

    @Autowired
    private RedissonClient redissonClient;

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
        List<ResourceEntity> resource_type = resourceService.list(new QueryWrapper<ResourceEntity>().eq("parent_id", 5));
        resource_type.forEach(item -> {
            ResourcesRole resourcesRole = new ResourcesRole();
            resourcesRole.setRoleId(2);
            resourcesRole.setCreateTime(LocalDateTime.now());
            resourcesRole.setResourceId(item.getResourceId());
            resourcesRoleService.save(resourcesRole);
        });
    }

    @Test
    public void test2(){
        String name = "测试文件夹";
        int index = 0;
        for (int i = 0; i < 20; i++) {
            index++;
            File file = new File();
            file.setFileId(IdGen.uuid());
            file.setProjectId("4a5aea84a00a4d1db79f3b5434a37265");
            file.setCatalog(1);
            file.setCreateTime(System.currentTimeMillis());
            file.setMemberId("396d6cf3630245fb821d30be04ea7162");
            file.setLevel(1);
            file.setFileName(name+index);
            fileService.save(file);
        }

    }

    @Test
    public void test3(){
        RBucket<String> bucket = redissonClient.getBucket("1");
        bucket.set("shaohua");
    }
}
