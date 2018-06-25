package com.art1001.supply.service;

import com.art1001.supply.ApplicationTests;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.service.file.FileService;
import org.junit.Test;

import javax.annotation.Resource;

public class FileServiceTest extends ApplicationTests {

    @Resource
    private FileService fileService;

    /**
     * 项目初始化测试
     */
    @Test
    public void initFileTest() {
        Project project = new Project();
        project.setProjectId("1");
        project.setProjectName("第一个项目");
        fileService.initProjectFolder(project);
    }

    /**
     * 创建文件夹
     */
    @Test
    public void createFolder() {
        fileService.createFolder("1", "0", "小片片");
    }
}
