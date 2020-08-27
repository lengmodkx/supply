package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.file.FileEsService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName FileEsApi
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/8/26 17:08
 * @Discription 用于搜索es的fileapi
 */
@RequestMapping("/fileEs")
@RestController
public class FileEsApi {

    @Resource
    private FileEsService fileEsService;

    @GetMapping("/{fileName}/material_base_search")
    public JSONObject searchEsFile(@NotBlank(message = "搜索名称不能为空!") @PathVariable String fileName, Integer pageNumber,Integer pageSize) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", 1);
        try {
            List<File> fileList = fileEsService.searchEsFile(fileName, pageNumber, pageSize);
            jsonObject.put("totle", fileEsService.getSucaiTotle(fileName));
            jsonObject.put("data", fileList);
            jsonObject.put("msg",fileList.size());
            return jsonObject;
        } catch (IOException e) {
            throw new AjaxException(e);
        }
    }
}
