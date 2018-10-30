package com.art1001.supply;

import com.art1001.supply.entity.resource.ResourceEntity;
import com.art1001.supply.entity.role.ResourcesRole;
import com.art1001.supply.entity.tag.TagRelation;
import com.art1001.supply.service.resource.ResourceService;
import com.art1001.supply.service.role.ResourcesRoleService;
import com.art1001.supply.service.tagrelation.TagRelationService;
import com.art1001.supply.service.task.TaskService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    public void test3(){
        taskService.completeTask("16a13489a79246949e91ac429012f384");
    }
}
