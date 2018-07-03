package com.art1001.supply.controller.file;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
     * @param file projectId 项目id
     *             parentId  上级目录id
     *             isDel     删除标识 默认为0
     */
    @GetMapping("/list.html")
    public String list(
            File file,
            Model model
    ) {
        // 项目id
        String projectId = file.getProjectId();
        // 上级id
        String parentId = file.getParentId();
        // 删除标识
        Integer fileDel = file.getFileDel();
        List<File> fileList = fileService.findChildFile(projectId, parentId, fileDel);
        model.addAttribute("fileList", fileList);
        model.addAttribute("parentId", parentId);
        model.addAttribute("projectId", projectId);
        model.addAttribute("project", projectService.findProjectByProjectId(projectId));
        model.addAttribute("user", ShiroAuthenticationManager.getUserEntity());
        return "file";
    }

    /**
     * 打开下拉框
     */
    @GetMapping("/optionFile")
    public String optionFile(@RequestParam String fileId, Model model) {
        model.addAttribute("fileId", fileId);
        return "tk-filemenu";
    }

    @RequestMapping("/openDownloadFile")
    public String openDownloadFile(@RequestParam String fileId, Model model) {
        model.addAttribute("file", fileService.findFileById(fileId));
        return "tk-file-download";
    }

    /**
     * 文件目录
     *
     * @param file 文件
     *             projectId 项目id
     *             parentId 上级id
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
     *
     * @param file 文件
     *             projectId 项目id
     *             parentId 上级id
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
     * 获取子目录
     */
    @RequestMapping("/getChildFolder")
    @ResponseBody
    public JSONObject getChildFolder(@RequestParam String fileId) {
        JSONObject jsonObject = new JSONObject();
        try {
            File file = new File();
            file.setParentId(fileId);
            file.setCatalog(1);
            List<File> fileList = fileService.findFileList(file);
            jsonObject.put("result", 1);
            jsonObject.put("data", fileList);
            jsonObject.put("fileId", fileId);
            jsonObject.put("msg", "获取成功");
        } catch (Exception e) {
            log.error("获取子目录失败， {}", e);
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
            if (StringUtils.isEmpty(parentId)) {
                parentId = "0";
            }
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
            if (StringUtils.isEmpty(parentId)) {
                parentId = "0";
            }
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
            MultipartFile file
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            // 得到原来的文件
            File f = fileService.findFileById(fileId);
            // 设置文件url
            String fileUrl = f.getFileUrl();
            // 上传oss，相同的objectName会覆盖
            AliyunOss.uploadInputStream(fileUrl, file.getInputStream());

            // 得到文件名
            String fileName = file.getOriginalFilename();
            // 设置修改后的文件名
            f.setFileName(fileName);

            // 更新数据库
            fileService.updateFile(f);

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
        InputStream inputStream = null;
        // 获取文件
        File file = fileService.findFileById(fileId);
        String fileName = file.getFileName();

        String deleteUrl = "";
        // 如果下载的是目录，则打包成zip
        if (file.getCatalog() == 1) {
            // 得到临时下载文件目录
            String tempPath = FileUtils.getTempPath();
            // 创建文件夹，加时间戳，区分
            String path = tempPath + "\\" + System.currentTimeMillis() + "\\" + fileName;
            java.io.File folder = new java.io.File(path);
            folder.mkdirs();
            // 设置查询条件
            List<File> childFile = fileService.findChildFile(file.getProjectId(), file.getFileId(), 0);
            if (childFile.size() > 0) {
                // 下载到临时文件
                this.downloadZip(childFile, path);
            }

            // 把临时文件打包成zip下载
            String downloadPath = path + ".zip";
            FileOutputStream fos1 = new FileOutputStream(new java.io.File(downloadPath));
            FileUtils.toZip(path, fos1, true);

            // 开始下载

            // 以流的形式下载文件。
            inputStream = new BufferedInputStream(new FileInputStream(downloadPath));
            fileName += ".zip";
            // 删除临时文件
            deleteUrl = downloadPath.substring(0, downloadPath.lastIndexOf("\\"));
        } else {
            // 文件  在oss上得到流
            inputStream = AliyunOss.downloadInputStream(file.getFileUrl());
        }

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

        if (file.getCatalog() == 1) {
            FileUtils.delFolder(deleteUrl);
        }
    }

    @GetMapping("/copyFile.html")
    public String copyFilePage(@RequestParam String[] fileIds, Model model) {
        List<File> fileList = fileService.findByIds(fileIds);
        String projectId = fileList.get(0).getProjectId();
        // 文件数量
        AtomicInteger fileNum = new AtomicInteger();
        // 文件夹数量
        AtomicInteger folderNum = new AtomicInteger();
        fileList.forEach(file -> {
            if (file.getCatalog() == 1) {
                folderNum.addAndGet(1);
            } else {
                fileNum.addAndGet(1);
            }
        });

        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        // 获取所有参与的项目
        List<Project> projectList = projectService.findProjectByMemberId(userEntity.getId());

        model.addAttribute("fileNum", fileNum);
        model.addAttribute("folderNum", folderNum);
        model.addAttribute("projectId", projectId);
        model.addAttribute("projectList", projectList);
        return "copyFile";
    }

    /**
     * 移动文件
     *
     * @param fileIds ids
     */
    @GetMapping("/moveFile.html")
    public String moveFilePage(@RequestParam String[] fileIds, Model model) {
        List<File> selectFileList = fileService.findByIds(fileIds);
        String projectId = selectFileList.get(0).getProjectId();
        // 文件数量
        AtomicInteger fileNum = new AtomicInteger();
        // 文件夹数量
        AtomicInteger folderNum = new AtomicInteger();
        selectFileList.forEach(file -> {
            if (file.getCatalog() == 1) {
                folderNum.addAndGet(1);
            } else {
                fileNum.addAndGet(1);
            }
        });

        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        // 获取所有参与的项目
        List<Project> projectList = projectService.findProjectByMemberId(userEntity.getId());

        // 获取项目顶级的文件夹
        File file = new File();
        file.setProjectId(projectId);
        file.setParentId("0");
        file.setCatalog(1);
        List<File> fileList = fileService.findFileList(file);

        model.addAttribute("fileMsg", fileNum + "个文件");
        model.addAttribute("folderMsg", folderNum + "文件夹");
        model.addAttribute("projectId", projectId);
        model.addAttribute("projectList", projectList);
        model.addAttribute("fileList", fileList);
        model.addAttribute("fileIds", StringUtils.join(fileIds, ","));
        return "moveFile";
    }

    /**
     * 移动文件
     *
     * @param fileIds  文件id数组
     * @param folderId 目标文件夹id
     */
    @RequestMapping("/moveFile")
    @ResponseBody
    public JSONObject moveFile(
            @RequestParam String[] fileIds,
            @RequestParam String folderId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.moveFile(fileIds, folderId);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "移动成功");
        } catch (Exception e) {
            log.error("移动文件异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "移动失败");
        }
        return jsonObject;
    }

    /**
     * 复制文件
     *
     * @param fileIds   多个文件id
     * @param folderId 目标文件夹id
     */
    @RequestMapping("/copyFile")
    @ResponseBody
    public void copyFile(
            @RequestParam String[] fileIds,
            @RequestParam String folderId
    ) {
        for (String fileId : fileIds) {
            // 获取目标文件夹
            File folder = fileService.findFileById(folderId);
            // 获取源文件
            File file = fileService.findFileById(fileId);
            // 父级id
            String parentId = "";
            // 设置父级id
            if (folder.getParentId().equals("1")) {
                // 如果是项目的根目录则设置父级id为0
                parentId = "0";
            } else {
                parentId = folder.getFileId();
            }

            if (file.getCatalog() == 1) { // 文件夹
                file.setParentId(parentId);
                String fId = file.getFileId();
                String projectId = file.getProjectId();
                fileService.saveFile(file);
                List<File> childFile = fileService.findChildFile(projectId, fId, 0);
                if (childFile.size() > 0) {
                    Map<String, List<File>> map = new HashMap<>();
                    map.put(file.getFileId(), childFile);
                    this.copyFolder(map);
                }

            } else { // 文件
                this.copyFileSave(file, parentId);
            }
        }

    }

    /**
     * 回收站
     *
     * @param fileIds ids
     */
    @RequestMapping("/recoveryFile")
    @ResponseBody
    public JSONObject recoveryFile(
            @RequestParam String[] fileIds
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            fileService.recoveryFile(fileIds);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "移入回收站成功");
        } catch (Exception e) {
            log.error("移入回收站异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "移入回收站失败");
        }
        return jsonObject;
    }

    public static void main(String[] args) {
        System.out.println(FileUtils.getTempPath());
        String tempPath = FileUtils.getTempPath();
        String fileName = tempPath + "\\" + "test";
        java.io.File folder = new java.io.File(fileName);
        folder.mkdirs();
    }

    /**
     * 下载zip
     *
     * @param fileList 下载条件
     * @param path     url
     */
    private void downloadZip(List<File> fileList, String path) {
        List<File> fileUrl = new ArrayList<>();
        for (File file : fileList) {
            if (file.getCatalog() == 1) { // 目录
                String url = path + "\\" + file.getFileName();
                java.io.File folder = new java.io.File(url);
                folder.mkdirs();
                fileUrl.add(file);
            } else { // 文件
                int byteSum = 0;
                int byteRead = 0;
                InputStream inStream = null;
                FileOutputStream fs = null;
                try {
                    inStream = AliyunOss.downloadInputStream(file.getFileUrl());
                    fs = new FileOutputStream(path + "\\" + file.getFileName());

                    byte[] buffer = new byte[1204];
                    assert inStream != null;
                    while ((byteRead = inStream.read(buffer)) != -1) {
                        byteSum += byteRead;
                        fs.write(buffer, 0, byteRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        assert inStream != null;
                        inStream.close();
                        assert fs != null;
                        fs.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        if (fileUrl.size() > 0) {
            for (File file : fileUrl) {
                List<File> childFile = fileService.findChildFile(file.getProjectId(), file.getFileId(), 0);
                this.downloadZip(childFile, path + "\\" + file.getFileName());
            }
        }
    }

    /**
     * 复制文件夹
     */
    private void copyFolder(Map<String, List<File>> map) {
        Map<String, List<File>> fileMap = new HashMap<>();
        for (Map.Entry<String, List<File>> entry: map.entrySet()) {
            // 得到键，即parentId
            String parentId = entry.getKey();
            // 得到值，即FileList
            List<File> fileList = entry.getValue();
            for (File file : fileList) {
                if (file.getCatalog() == 1) { // 文件夹
                    // 设置父级id
                    file.setParentId(parentId);
                    String fId = file.getFileId();
                    String projectId = file.getProjectId();
                    // 存库
                    fileService.saveFile(file);
                    // 得到此文件夹下一层的子集
                    List<File> childFile = fileService.findChildFile(projectId, fId, 0);
                    fileMap.put(file.getFileId(), childFile);
                } else { //文件
                    this.copyFileSave(file, parentId);
                }
            }

            if (fileMap.size() > 0) {
                this.copyFolder(fileMap);
            }
        }
    }

    /**
     * 复制文件保存
     */
    private void copyFileSave(File file, String parentId) {
        // 得到源文件objectName
        String fileUrl = file.getFileUrl();
        // 源文件名
        String fileName = file.getFileName();
        // 得到后缀名
        String ext = fileName.substring(fileName.lastIndexOf("."), fileName.length() - 1);
        // 设置现在文件名
        String newFileName = System.currentTimeMillis() + "." + ext;
        // 设置现在文件objectName
        String newFileUrl = fileUrl.substring(0, fileUrl.lastIndexOf("/"));

        // 在oss上复制
        AliyunOss.copyFile(fileUrl, newFileName);

        // 设置文件名
        file.setFileName(newFileName);
        // 设置url
        file.setFileUrl(newFileUrl);
        // 设置父级id
        file.setParentId(parentId);

        file.setFileUrl(fileUrl);
        fileService.saveFile(file);
    }
}
