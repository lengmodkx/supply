package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.api.request.RecycleBinParamDTO;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.recycle.RecycleBinService;
import com.art1001.supply.util.ValidatorUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author heshaohua
 **/
@Validated
@RestController
@RequestMapping("/recycle_bin")
public class RecycleBinApi {

    /**
     * 注入任务逻辑层Bean
     */
    @Resource
    private RecycleBinService recycleBinService;

    @Resource
    private FileService fileService;

    /**
     * 获取一个项目回收站中的信息
     * @param projectId 项目id
     * @param type 信息类型
     * @param fileType 文件类型(获取的是文件夹还是文件)
     * @return 回收站信息
     */
    @GetMapping("/{projectId}/{type}")
    public JSONObject getRecycleBinItem(@PathVariable String projectId, @PathVariable String type, @RequestParam(required = false) String fileType){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data",recycleBinService.getRecycleBinItem(projectId, type,fileType));
            jsonObject.put("result", 1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,数据获取失败!",e);
        }
    }

    /**
     * 移入/恢复回收站中的内容 -- 任务
     * @param recycleParamsDto 参数对象
     * @return 结果
     */
    @PutMapping("/recovery")
    @Push(type = 1,value = PushType.D14)
    public JSONObject recovery(RecycleBinParamDTO recycleParamsDto){
        JSONObject jsonObject = new JSONObject();
        ValidatorUtils.validateEntity(recycleParamsDto);
        recycleBinService.recovery(recycleParamsDto);
        jsonObject.put("result", 1);
        jsonObject.put("msgId",recycleParamsDto.getProjectId());
        jsonObject.fluentPut("data", fileService.findParentId(recycleParamsDto.getProjectId()));
        return jsonObject;
    }
}
