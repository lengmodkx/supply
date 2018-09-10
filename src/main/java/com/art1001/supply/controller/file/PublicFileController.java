package com.art1001.supply.controller.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.PublicFile;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.file.PublicFileService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heshaohua
 * @Title: PublicFile
 * @Description: TODO
 * @date 2018/9/8 15:21
 **/
@Log4j
@Controller
@RequestMapping("publicfile")
public class PublicFileController {

    @Resource
    PublicFileService publicFileService;

    @Resource
    private FileService fileService;

    /**
     * 查询出公共文件库的所有文件
     *
     * @return
     */
    @PostMapping("findAllPublicFile")
    @ResponseBody
    public JSONObject findAllPublicFile() {
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> publicFile = fileService.findPublicFile();
            jsonObject.put("data", publicFile);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return jsonObject;
    }

    /**
     * 返回公共该文件夹的数据
     *
     * @return
     */
    @GetMapping("findPublicFolder")
    @ResponseBody
    public JSONObject findPublicFolder(@RequestParam("folderName") String folderName) {
        JSONObject jsonObject = new JSONObject();
        try {
            PublicFile publicFolder = publicFileService.findPublicFolder(folderName);
            jsonObject.put("result", 1);
            jsonObject.put("data", publicFolder);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            jsonObject.put("msg", "系统异常");
        }
        return jsonObject;
    }

    /**
     * 上传公共文件数据
     */
    @PostMapping("uploadFileToPublicFile")
    @ResponseBody
    public JSONObject uploadFileToPublicFile(PublicFile file) {
        JSONObject jsonObject = new JSONObject();
        try {
            publicFileService.savePublicFile(file);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error(e.getMessage());
            jsonObject.put("result", 0);
            jsonObject.put("msg", "系统异常");
        }
        return jsonObject;
    }

    /**
     * 加载公共文件的子文件及文件夹
     *
     * @param parentId 父文件夹id
     * @return
     */
    @GetMapping("loadChildFile")
    @ResponseBody
    public JSONObject loadChildFile(@RequestParam(value = "fileId") String parentId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<PublicFile> files = publicFileService.findChildFile(parentId);
            jsonObject.put("data", files);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error(e.getMessage());
            jsonObject.put("msg", "系统异常!");
            jsonObject.put("result", 0);
        }
        return jsonObject;
    }

    /**
     * 在公共模型库下创建文件夹
     * @param folderName 文件夹名称
     * @param parentId 父文件夹id
     * @return
     */
    @PostMapping("createPublicFolder")
    @ResponseBody
    public JSONObject createPublicFolder(@RequestParam("folderName") String folderName, @RequestParam(value = "parentId",defaultValue = "0",required = false) String parentId){
        JSONObject jsonObject = new JSONObject();
        try {
            PublicFile publicFile = new PublicFile();
            publicFileService.createPublicFolder(folderName,parentId);
            jsonObject.put("result",1);
        } catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            jsonObject.put("msg", "系统异常!");
            jsonObject.put("result", 0);
        }
        return jsonObject;
    }
}
