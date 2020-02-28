package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.CodeMsg;
import com.art1001.supply.entity.OssInfo;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.util.AliyunOss;
import com.art1001.supply.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Slf4j
@RestController
@RequestMapping("oss")
public class OssApi {

    @Resource
    private OssInfo oss;

    @Resource
    private FileService fileService;
    /**
     * 前端获取直传文件到阿里云oss的签名
     * @param dir
     * @return
     */

    @GetMapping("sign")
    public Result getSign(@RequestParam String dir){
        OSSClient client = new OSSClient(oss.getEndpoint(), oss.getAccessId(), oss.getAccessKey());
        try {
            long expireEndTime = System.currentTimeMillis() + 30 * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyCods = new PolicyConditions();
            policyCods.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyCods.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
            String postPolicy = client.generatePostPolicy(expiration, policyCods);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            oss.setPolicy(encodedPolicy);
            oss.setSignature(postSignature);
            oss.setDir(dir);
            oss.setExpire(String.valueOf(expireEndTime / 1000));

            JSONObject object = new JSONObject();
            object.put("callbackUrl", oss.getCallbackUrl());
            object.put("callbackBody","{\"filename\":${object},\"size\":${size},\"user_id\":${x:user_id},\"name\":${x:name},\"project_id\":${x:project_id},\"parent_id\":${x:parent_id},\"level\":${x:level},\"model_pic\":${x:model_pic}}");
            object.put("callbackBodyType", "application/json");
            String base64CallbackBody = BinaryUtil.toBase64String(object.toString().getBytes());
            oss.setCallback(base64CallbackBody);
            return Result.success(oss);
        }catch (Exception e){
            return Result.fail(CodeMsg.SERVER_ERROR);
        }
    }

    /**
     * 阿里云oss回调请求地址，推送未加
     */
    @PostMapping("/callback")
    public void dealCallBack(HttpServletRequest request, HttpServletResponse response){
        try {
            ServletInputStream inputStream = request.getInputStream();
            int contentLength = Integer.parseInt(request.getHeader("content-length"));
            String ossCallbackBody = AliyunOss.getRequestBody(inputStream,contentLength);
            System.out.println(ossCallbackBody);
            boolean ret = AliyunOss.verifyOSSCallbackRequest(request, ossCallbackBody);
            System.out.println(ret);
            if (ret) {
                JSONObject object = JSON.parseObject(ossCallbackBody);
                File file = new File();
                file.setMemberId(object.getString("user_id"));
                file.setUserId(object.getString("user_id"));
                file.setProjectId(object.getString("project_id"));
                file.setParentId(object.getString("parent_id"));
                file.setFileName(object.getString("name"));
                file.setFileUrl(object.getString("filename"));
                file.setExt(FileUtils.convertFileSize(object.getLong("size")));
                file.setFileUids(object.getString("user_id"));
                fileService.save(file);
                AliyunOss.response(request, response, "{\"Status\":\"OK\"}", HttpServletResponse.SC_OK);
            }
            else{
                AliyunOss.response(request, response, "{\"Status\":\"verdify not ok\"}", HttpServletResponse.SC_BAD_REQUEST);
            }
        }catch (Exception e){
            log.error("服务器错误，{}",e.getMessage());
        }
    }
}
