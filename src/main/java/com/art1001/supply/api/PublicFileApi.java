package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.PublicFile;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.ServiceException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.file.PublicFileService;
import com.art1001.supply.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.search.aggregator.Count;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heshaohua
 * @Title: PublicFileApi
 * @Description: TODO
 * @date 2018/9/19 9:55
 **/
@Slf4j
@RequestMapping("publicFiles")
@RestController
public class PublicFileApi {

    @Resource
    PublicFileService publicFileService;

    @Resource
    private FileService fileService;


    /**
     * 查询出公共文件库的文件
     *
     * @return
     */
    @GetMapping("/{parentId}")
    public JSONObject findAllPublicFile(@PathVariable(value = "parentId",required = false) String parentId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<File> publicFile = fileService.findPublicFile(parentId);
            if(CommonUtils.listIsEmpty(publicFile)){
                jsonObject.put("msg","无数据");
                jsonObject.put("result",1);
                return jsonObject;
            }
            jsonObject.put("data", publicFile);
            jsonObject.put("result",1);
        } catch (Exception e) {
            log.error("系统异常:",e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

    /**
     * 返回公共该文件夹的数据
     * @return
     */
    @GetMapping("public_folder")
    public JSONObject findPublicFolder(@RequestParam("folderName") String folderName) {
        JSONObject jsonObject = new JSONObject();
        try {
            PublicFile publicFolder = publicFileService.findPublicFolder(folderName);
            jsonObject.put("result", 1);
            jsonObject.put("data", publicFolder);
        } catch (Exception e) {
            log.error("系统异常:",e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

    /**
     * 上传公共文件数据
     */
    @PostMapping("/upload")
    public JSONObject uploadFileToPublicFile(PublicFile file) {
        JSONObject jsonObject = new JSONObject();
        try {
            publicFileService.savePublicFile(file);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 加载公共文件的子文件及文件夹
     * @param parentId 父文件夹id
     * @return
     */
    @GetMapping("/{fileId}/child_file")
    public JSONObject loadChildFile(@PathVariable(value = "fileId") String parentId) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<PublicFile> files = publicFileService.findChildFile(parentId);
            if(CommonUtils.listIsEmpty(files)){
                jsonObject.put("result",1);
                jsonObject.put("msg", "无数据");
            }
            jsonObject.put("data", files);
            jsonObject.put("result", 1);
        } catch (Exception e) {
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 在公共模型库下创建文件夹
     * @param folderName 文件夹名称
     * @param parentId 父文件夹id
     * @return
     */
    @PostMapping("/folder")
    public JSONObject createPublicFolder(@RequestParam(value = "folderName") String folderName, @RequestParam(value = "parentId",defaultValue = "0",required = false) String parentId){
        JSONObject jsonObject = new JSONObject();
        try {
            PublicFile publicFile = new PublicFile();
            publicFileService.createPublicFolder(folderName,parentId);
            jsonObject.put("result",1);
        } catch (ServiceException e){
            log.error("文件夹已存在!",e);
            throw new AjaxException(e);
        } catch (Exception e){
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }
}
