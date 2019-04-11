package com.art1001.supply.project;

import com.art1001.supply.ApplicationTests;
import com.art1001.supply.service.project.ProjectMemberService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @Description
 * @Date:2019/3/28 19:43
 * @Author heshaohua
 **/
public class ProjectTest extends ApplicationTests {

    @Resource
    private ProjectMemberService projectMemberService;

    @Test
    public void getStarProjectId(){
        projectMemberService.getStarProject("c0ef5cfb273a47d7b81394f9d00ceb1d").forEach(item -> {
            System.out.println(item.getProjectId());
        });
    }
}
