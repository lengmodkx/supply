package com.art1001.supply.file;

import com.art1001.supply.ApplicationTests;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Date:2019/3/21 14:45
 * @Author heshaohua
 **/
public class FileTest extends ApplicationTests {


    @Resource
    private FileService fileService;

    @Test
    public void seachFolder(){
        List<File> files = new ArrayList<>();
        for (int i = 6;i <= 11;i++){
            File file = new File();
            file.setFileName("何少华的测试文件夹 " + (++i));
            file.setProjectId("e67de76e2a1540929574e7d6a4c08701");
            file.setParentId("86b7319190234eccadb72108fa4e6fe9");
            file.setMemberId("c0ef5cfb273a47d7b81394f9d00ceb1d");
            file.setCreateTime(System.currentTimeMillis());
            file.setCatalog(1);
            files.add(file);
        }
        fileService.saveBatch(files);
    }

    @Test
    public void orderMyFile(){
        fileService.created("size");
    }
}
