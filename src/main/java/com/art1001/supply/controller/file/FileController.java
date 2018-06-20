package com.art1001.supply.controller.file;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.util.AliyunOss;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStreamReader;
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

    /**
     * 文件列表
     *
     * @param projectId 项目id
     * @param parentId 上级目录id
     * @param isDel 删除标识
     */
    @GetMapping("/list.html")
    public String list(
            @RequestParam String projectId,
            @RequestParam (required = false, defaultValue = "0") String parentId,
            @RequestParam (required = false, defaultValue = "0") Integer isDel,
            Model model
    ) {
        if (parentId.equals("0")) {
            List<File> childFile = fileService.findChildFile(projectId, parentId, isDel);
            if (childFile.size() > 0) {
                parentId = childFile.get(0).getFileId();
            }

        }
        List<File> fileList = fileService.findChildFile(projectId, parentId, isDel);
        model.addAttribute("fileList", fileList);
        return "file";
    }

    /**
     * 创建文件夹
     *
     * @param projectId 项目id
     * @param parentId  上一级目录id  默认为0
     * @param folderName 文件夹名称
     */
    @PostMapping("/createFolder")
    @ResponseBody
    public JSONObject createFolder(
            @RequestParam String projectId,
            @RequestParam (required = false, defaultValue = "0") String parentId,
            @RequestParam String folderName
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (parentId.equals("0")) {
                List<File> childFile = fileService.findChildFile(projectId, parentId, 0);
                if (childFile.size() > 0) {
                    parentId = childFile.get(0).getFileId();
                }

            }

            fileService.createFolder(parentId, folderName);

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
     * @param file 文件
     */
    @PostMapping("/uploadFile")
    @ResponseBody
    public JSONObject uploadFile(
            @RequestParam String projectId,
            @RequestParam (required = false, defaultValue = "0") String parentId,
            MultipartFile file,
            HttpServletRequest request
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (parentId.equals("0")) {
                List<File> childFile = fileService.findChildFile(projectId, parentId, 0);
                if (childFile.size() > 0) {
                    parentId = childFile.get(0).getFileId();
                }

            }
            String imgDir = fileService.uploadFile(projectId, parentId, file, request);
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
     * 修改文件
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
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        File file = fileService.findFileById(fileId);
        InputStreamReader inputStreamReader = AliyunOss.downloadInputStream(file.getFileUrl());


    }
}
