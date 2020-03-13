package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Log;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.api.request.RecycleBinParamDTO;
import com.art1001.supply.common.Constants;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.file.FileService;
import com.art1001.supply.service.recycle.RecycleBinService;
import com.art1001.supply.util.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author heshaohua
 **/
@Slf4j
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
        recycleBinService.moveOrRecovery(recycleParamsDto);
        jsonObject.put("result", 1);
        jsonObject.put("msgId",recycleParamsDto.getProjectId());
        jsonObject.fluentPut("data", fileService.findParentId(recycleParamsDto.getProjectId()));
        return jsonObject;
    }

    /**
     * 移到回收站/恢复
     * @param taskId 任务id
     * @return 结果
     */
    @Push(value = PushType.A17,type = 3)
    @RequestMapping("/move_task_rb")
    public JSONObject moveTaskToRecycleBin(RecycleBinParamDTO recycleBinParamDTO
    ){
        JSONObject object = new JSONObject();
        try{
            if(!Constants.TASK_EN.equals(recycleBinParamDTO.getPublicType())){
                object.put("result", 0);
                object.put("msg", "publicType参数不正确！");
                return object;
            }
            recycleBinService.moveOrRecovery(recycleBinParamDTO);
            object.put("result",1);
            object.put("msgId", recycleBinParamDTO.getProjectId());
            object.put("data", recycleBinParamDTO.getPublicId());
            object.put("id", recycleBinParamDTO.getPublicId());
            object.put("publicType", Constants.TASK);
        }catch(Exception e){
            log.error("系统异常,移入回收站失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 将文件移至回收站/恢复
     * @param recycleBinParamDTO 参数
     */
    @Push(value = PushType.C13,type = 1)
    @RequestMapping("/move_file_rb")
    public JSONObject moveFileToRecycleBin(RecycleBinParamDTO recycleBinParamDTO) {
        JSONObject jsonObject = new JSONObject();
        try {
            recycleBinService.moveOrRecovery(recycleBinParamDTO);
            jsonObject.put("result", 1);
            jsonObject.put("msgId", recycleBinParamDTO.getProjectId());
            jsonObject.put("data", recycleBinParamDTO.getFileIdList());
        } catch (Exception e) {
            log.error("移入回收站异常:", e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 将分享移入回收站/恢复
     * @param recycleBinParamDTO 参数
     */
    @Log
    @Push(PushType.B6)
    @RequestMapping("/move_share_rb")
    public JSONObject moveShareToRecycleBin(RecycleBinParamDTO recycleBinParamDTO){
        JSONObject jsonObject = new JSONObject();
        try {
            recycleBinService.moveOrRecovery(recycleBinParamDTO);
            jsonObject.put("result",1);
            jsonObject.put("data", recycleBinParamDTO.getProjectId());
            jsonObject.put("msgId", recycleBinParamDTO.getProjectId());
        } catch (Exception e){
            log.error("系统异常,移入回收站失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 移动日程到回收站
     * @param recycleBinParamDTO 参数
     */
    @Push(value = PushType.D12,type = 1)
    @RequestMapping("/move_schedule_rb")
    public JSONObject moveToRecycleBin(RecycleBinParamDTO recycleBinParamDTO){
        JSONObject object = new JSONObject();
        try{

            recycleBinService.moveOrRecovery(recycleBinParamDTO);
            object.put("msgId", recycleBinParamDTO.getProjectId());
            object.put("data", recycleBinParamDTO.getProjectId());
            object.put("msg", "成功移至回收站!");
            object.put("result", 1);
        }catch(Exception e){
            log.error("移入回收站失败:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 移入回收站
     */
    @RequestMapping("/move_tag_rb")
    public JSONObject dropTag(RecycleBinParamDTO recycleBinParamDTO){
        JSONObject jsonObject = new JSONObject();
        try{
            recycleBinService.moveOrRecovery(recycleBinParamDTO);
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("系统异常,移入回收站失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

}
