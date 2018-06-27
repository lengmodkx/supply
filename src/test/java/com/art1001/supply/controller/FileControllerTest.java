package com.art1001.supply.controller;

import com.art1001.supply.ApplicationTests;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.util.AliyunOss;
import org.junit.Test;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

public class FileControllerTest extends ApplicationTests {

    @Resource
    private FileService fileService;

    @Test
    public void fileListTest() {
        Project project = new Project();
        project.setProjectId("1");
        project.setProjectName("第一个项目");
        fileService.initProjectFolder(project);
    }

    @Test
    public void fileChildListTest() {
        String projectId = "1";
        String parentId = "6440d5be06ae4650bd527b00909e5352";
        List<File> fileList = fileService.findChildFile(projectId, parentId, 0);

        fileList.forEach(System.out::println);
    }

    @Test
    public void createFolderTest() {
        String parentId = "6440d5be06ae4650bd527b00909e5352";
        String fileName = "小片片";
        // 查询文件目录是否存在
        int count = fileService.findByParentIdAndFileName(parentId, fileName);
        if (count > 0) {
            System.out.println("已经存在");
        } else {
            fileService.createFolder("", parentId, fileName);
        }
    }

    @Test
    public void deleteFileTest() {
        String fileId = "13eafb39da24406bafd4c05308df6448";
        File file = new File();
        file.setFileId(fileId);
        file.setFileDel(1);
//        fileService.updateFile(file);

        String projectId = "1";
        String parentId = "6440d5be06ae4650bd527b00909e5352";
        List<File> fileList = fileService.findChildFile(projectId, parentId, 0);
        fileList.forEach(f -> {
            System.out.println(f.getFileName());
        });
    }

    @Test
    public void uploadFileTest() {
        String fileId = "6440d5be06ae4650bd527b00909e5352";
        String fileName = "fileNamex";
        File file = fileService.findFileById(fileId);
        if (file != null) {
            String fileUrl = file.getFileUrl() + fileName;
            AliyunOss.uploadString(fileUrl, "测试字符串   test String");
            file.setFileName(fileName);
            file.setFileUrl(fileUrl);
            file.setParentId(file.getFileId());
        }

    }

    @Test
    public void findAllChildTest() {
        String fileId = "c281fd72f08e42eb9370a027a3cef889";
//        List<File> fileList = fileService.findAllChild(fileId);
    }

    public static void main(String[] args) {
        AliyunOss.rangeDownload("1529984862029/1529984862219");

    }

    private static String test() {
        return "1231";
    }


}
