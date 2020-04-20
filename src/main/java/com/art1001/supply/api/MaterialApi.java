package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.Material;
import com.art1001.supply.service.file.MaterialService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("materials")
public class MaterialApi {

    @Resource
    private MaterialService materialService;

    @RequestMapping("upload")
    public Result upload(@RequestParam("userId")String userId,
                         @RequestParam("parentId")String parentId,
                         @RequestParam("files") String files){
        JSONArray objects = JSON.parseArray(files);
        for (int i=0;i<objects.size();i++) {
            JSONObject jsonObject = objects.getJSONObject(i);
            Material material = new Material();
            material.setMemberId(userId);
            material.setFileUrl(jsonObject.getString("url"));
            material.setFileName(jsonObject.getString("name"));
            material.setSize(jsonObject.getString("size"));
            material.setCatalog(0);
            material.setCreateTime(System.currentTimeMillis());
            material.setIsModel(0);
            //设置文件层级
            int parentLevel = materialService.getOne(new QueryWrapper<Material>().select("level").eq("file_id", parentId)).getLevel();
            material.setLevel(parentLevel + 1);
            material.setParentId(parentId);
            materialService.save(material);
        }


        return Result.success();
    }


}
