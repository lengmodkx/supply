package com.art1001.supply.controller.file;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

/**
 * 文件相关controller
 */
@Controller
@RequestMapping("file")
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;

    @Resource
    private ProjectService projectService;
    /**
     * 文件列表
     *
     * @param projectId 项目id
     * @param parentId  上级目录id
     * @param isDel     删除标识
     */
    @GetMapping("/list.html")
    public String list(
            @RequestParam String projectId,
            @RequestParam(required = false, defaultValue = "0") String parentId,
            @RequestParam(required = false, defaultValue = "0") Integer isDel,
            Model model
    ) {
        List<File> fileList = fileService.findChildFile(projectId, parentId, isDel);
        model.addAttribute("fileList", fileList);
        model.addAttribute("parentId", parentId);
        model.addAttribute("project", projectService.findProjectByProjectId(projectId));
        model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        return "file";
    }

    /**
     * 文件目录
     * @param file 文件
     *             projectId 项目id
     *             parentId 上级id
     *
     */
    @GetMapping("/getFolder")
    @ResponseBody
    public JSONObject getFolder(File file) {
        JSONObject jsonObject = new JSONObject();

        try {
            // 设置 catalog 为1，只查询目录
            file.setCatalog(1);
            // 只查询未删除的目录
            file.setFileDel(0);
            if (StringUtils.isEmpty(file.getParentId())) {
                file.setParentId("0");
            }
            List<File> fileList = fileService.findFileList(file);
            jsonObject.put("result", 1);
            jsonObject.put("data", fileList);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("result", 0);
            jsonObject.put("msg", "获取失败");
        }
        return jsonObject;
    }

    /**
     * 文件目录
     * @param file 文件
     *             projectId 项目id
     *             parentId 上级id
     *
     */
    @GetMapping("/getFile")
    @ResponseBody
    public JSONObject getFile(File file) {
        JSONObject jsonObject = new JSONObject();

        try {
            // 设置 catalog 为1，只查询目录
            // 只查询未删除的目录
            file.setFileDel(0);
            if (StringUtils.isEmpty(file.getParentId())) {
                file.setParentId("0");
            }
            List<File> fileList = fileService.findFileList(file);
            jsonObject.put("result", 1);
            jsonObject.put("data", fileList);
            jsonObject.put("file", file);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("result", 0);
            jsonObject.put("msg", "获取失败");
        }
        return jsonObject;
    }

    /**
     * 创建文件夹
     *
     * @param projectId  项目id
     * @param parentId   上一级目录id  默认为0
     * @param folderName 文件夹名称
     */
    @PostMapping("/createFolder")
    @ResponseBody
    public JSONObject createFolder(
            @RequestParam String projectId,
            @RequestParam(required = false, defaultValue = "0") String parentId,
            @RequestParam String folderName
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.createFolder(projectId, parentId, folderName);

            jsonObject.put("result", 1);
            jsonObject.put("msg", "创建成功");
        } catch (Exception e) {
            log.error("创建文件夹异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "创建失败");
        }


        return jsonObject;
    }

    /**
     * 上传文件
     *
     * @param projectId 项目id
     * @param parentId  上级目录id
     * @param file      文件
     */
    @PostMapping("/uploadFile")
    @ResponseBody
    public JSONObject uploadFile(
            @RequestParam String projectId,
            @RequestParam(required = false, defaultValue = "0") String parentId,
            MultipartFile file
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            String imgDir = fileService.uploadFile(projectId, parentId, file);
            jsonObject.put("result", 1);
            jsonObject.put("data", imgDir);
            jsonObject.put("msg", "上传成功");

        } catch (Exception e) {
            log.error("上传文件异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "上传失败");
        }

        return jsonObject;
    }

    /**
     * 更新文件
     */
    @PostMapping("/updateUploadFile")
    @ResponseBody
    public JSONObject updateUploadFile(
            @RequestParam String fileId,
            MultipartFile multipartFile
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            // 得到原来的文件
            File file = fileService.findFileById(fileId);
            // 设置文件url
            String fileUrl = file.getFileUrl();
            // 上传oss，相同的objectName会覆盖
            AliyunOss.uploadInputStream(fileUrl, multipartFile.getInputStream());

            // 得到文件名
            String fileName = multipartFile.getOriginalFilename();
            // 设置修改后的文件名
            file.setFileName(fileName);

            // 更新数据库
            fileService.updateFile(file);

            // 设置返回数据
            jsonObject.put("result", 1);
            jsonObject.put("data", fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("result", 0);
            jsonObject.put("data", "更新失败");
        }

        return jsonObject;
    }

    /**
     * 修改文件
     *
     * @param file file
     */
    @PostMapping("/updateFile")
    @ResponseBody
    public JSONObject updateFile(File file) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.updateFile(file);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "修改成功");
        } catch (Exception e) {
            log.error("修改文件异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "修改失败");
        }

        return jsonObject;
    }

    /**
     * 删除
     *
     * @param fileId 文件id
     */
    @RequestMapping("/deleteFile")
    @ResponseBody
    public JSONObject deleteFile(@RequestParam String fileId) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.deleteFileById(fileId);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "删除成功");
        } catch (Exception e) {
            log.error("删除文件异常, {}", e);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "删除成功");
        }
        return jsonObject;
    }

    /**
     * 下载
     *
     * @param fileId 文件
     */
    @RequestMapping("/downloadFile")
    @ResponseBody
    public void downloadFile(
            @RequestParam String fileId,
            HttpServletResponse response
    ) throws IOException {
        // 获取文件
        File file = fileService.findFileById(fileId);
        String fileName = file.getFileName();
        // 如果下载的是目录
        if (file.getCatalog() == 1) {
            fileName += ".zip";
        }
        String path = Constants.OSS_URL + file.getFileUrl();
        InputStream inputStream = AliyunOss.downloadInputStream(path);
        // 设置响应类型
        response.setContentType("application/x-msdownload");
        // 设置头信息
        // 设置fileName的编码
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] bytes = new byte[1024];
        int n;
        assert inputStream != null;
        while ((n = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, n);
        }
        outputStream.close();
        inputStream.close();
    }

    /**
     * 移动文件
     * @param fileIds 文件id数组
     * @param folderId 目标文件夹id
     */
    @RequestMapping("/moveFile")
    public void moveFile(
            @RequestParam String[] fileIds,
            @RequestParam String folderId
    ) {
        fileService.moveFile(fileIds, folderId);
        // 获取目标文件夹
        File folder = fileService.findFileById(folderId);
    }

    @GetMapping("/downloadFolder")
    @ResponseBody
    public void downloadFolder(HttpServletResponse response) {

    }

    /**
     * 复制文件
     * @param fileId 文件id
     * @param folderId 目标文件夹id
     */
    @RequestMapping("/copyFile")
    public void copyFile(
            @RequestParam String fileId,
            @RequestParam String folderId
    ) {
        // 获取目标文件夹
        File folder = fileService.findFileById(folderId);
        // 获取数据库中的文件
        File file = fileService.findFileById(fileId);

    }

}
