package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Push;
import com.art1001.supply.annotation.PushName;
import com.art1001.supply.annotation.PushType;
import com.art1001.supply.annotation.Log;
import com.art1001.supply.entity.Result;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.entity.project.Project;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.exception.SystemException;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.AliyunOss;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author heshaohua
 * @Title: ShareApi
 * @Description: TODO 分享api
 * @date 2018/9/11 18:09
 * [POST]   // 新增
 * [GET]    // 查询
 * [PATCH]  // 更新
 * [PUT]    // 覆盖，全部更新
 * [DELETE] // 删除
 **/
@Slf4j
@RestController
@RequestMapping("shares")
public class ShareApi {

    /**
     * 注入分享逻辑层实例
     */
    @Resource
    private ShareService shareService;

    @Resource
    private PublicCollectService collectService;

    @Resource
    UserService userService;

    @Resource
    ProjectService projectService;

    @Resource
    LogService logService;
    /**
     * 加载分享页面
     * @param projectId 项目id
     * @return
     */
    @GetMapping
    public JSONObject share(@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            String userId = ShiroAuthenticationManager.getUserId();
            List<Share> shares = shareService.findByProjectId(projectId, 0);
            List<PublicCollect> collectList = collectService.list(new QueryWrapper<PublicCollect>().eq("collect_type", "分享").eq("project_id", projectId).eq("member_id", userId));
            jsonObject.put("data",shares);
            jsonObject.put("collects",collectList);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,分享数据加载失败:",e);
            throw new SystemException(e);
        }
        return jsonObject;
    }

    /**
     * 获取一个分享的信息
     * @param shareId 分享id
     * @return
     */
    @GetMapping("/{shareId}")
    public JSONObject getShare(@PathVariable(value = "shareId")String shareId){
        JSONObject jsonObject = new JSONObject();
        try {
            Share share = shareService.findByIdAllInfo(shareId);
            jsonObject.put("data",share);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,分享信息获取失败:",e.getMessage());
            throw new SystemException(e);
        }
        return jsonObject;
    }

    /**
     * 添加/移除 分享成员
     * @param shareId 分享id
     * @param memberIds 多个成员id的字符串
     * @return
     */
    @Push(value = PushType.B10,name = PushName.SHARE,type = 1)
    @PutMapping("/{shareId}/members")
    public JSONObject updateMembers(@PathVariable String shareId,@RequestParam(value = "memberIds") String memberIds){
        JSONObject jsonObject = new JSONObject();
        try {
            if(shareService.updateMembers(shareId,memberIds)){
                jsonObject.put("result",1);
                jsonObject.put("msgId", shareService.getProjectIdByShareId(shareId));
                jsonObject.put("data", shareId);
            }
        } catch (Exception e){
            log.error("系统异常,成员更新失败:",e.getMessage());
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 添加分享
     * @param projectId 项目id
     * @param title 分享标题
     * @param content 分享内容
     * @param isPrivacy 是否是隐私模式
     * @return
     */
    @Log(PushType.B1)
    @Push(PushType.B1)
    @PostMapping
    public JSONObject saveShare(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "isPrivacy",required = false) Integer isPrivacy,
            @RequestParam(value = "joinIds") String joinIds
    ){
        JSONObject object = new JSONObject();
        String  userId = ShiroAuthenticationManager.getUserId();
        try {
            Share share = new Share();
            share.setTitle(title);
            share.setContent(content);
            share.setProjectId(projectId);
            share.setIsPrivacy(isPrivacy);
            share.setMemberId(userId);
            share.setUids(userId);
            share.setCreateTime(System.currentTimeMillis());
            share.setUpdateTime(System.currentTimeMillis());
            share.setUids(joinIds);
            shareService.save(share);
            object.put("result",1);
            object.put("data",projectId);
            object.put("msgId",projectId);
        } catch (Exception e){
            log.error("保存分享异常:",e);
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 删除分享
     * @param shareId 分享id
     * @return
     */
    @Log(PushType.B9)
    @Push(PushType.B9)
    @DeleteMapping("/{shareId}")
    public Result shareDelete(@PathVariable("shareId") String shareId
         // 在回收站彻底删除分享时调用的接口，没有项目id这个参数
         // ,  @RequestParam(value = "projectId") String projectId
                               ){
        try{
            shareService.removeById(shareId);
            return Result.success();
        }catch (Exception e){
            log.error("分享删除失败:",e);
            throw new AjaxException(e);
        }
    }

    /**
     * 将分享移入回收站
     * @param shareId 分享id
     * @return
     */
    @Log
    @Push(PushType.B6)
    @PutMapping("/{shareId}/recyclebin")
    public JSONObject recyclebin(@PathVariable(value = "shareId") String shareId,@RequestParam(value = "projectId")String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            shareService.moveToRecycleBin(shareId);
            jsonObject.put("result",1);
            jsonObject.put("data",projectId);
            jsonObject.put("msgId",projectId);
        } catch (Exception e){
            log.error("系统异常,移入回收站失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 恢复分享
     * @param shareId 分享id
     * @return
     */
    @Push(PushType.B7)
    @PutMapping("/{shareId}/recovery")
    public JSONObject recovery(@PathVariable("shareId") String shareId,@RequestParam(value = "projectId")String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            shareService.recoveryShare(shareId);
            jsonObject.put("result",1);
            jsonObject.put("data",projectId);
            jsonObject.put("msgId",projectId);
        } catch (Exception e){
            log.error("系统异常,恢复失败:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 复制分享
     * @param shareId 分享id
     * @param projectId 项目id
     * @return
     */
    @Log(PushType.B4)
    @Push(PushType.B4)
    @PostMapping("/{shareId}/copy")
    public JSONObject copy(@PathVariable(value = "shareId")String shareId,
                               @RequestParam(value = "projectId")String projectId){
        JSONObject jsonObject = new JSONObject();
        try{
            shareService.copyShare(shareId,projectId);
            UserEntity entity = userService.getById(ShiroAuthenticationManager.getUserId());
            Project project = projectService.getById(projectId);
            logService.saveLog(shareId,entity + "将分享复制到了" + project.getProjectName() ,4);
            jsonObject.put("result",1);
            jsonObject.put("data",projectId);
            jsonObject.put("msgId",projectId);
        }catch(Exception e){
            log.error("系统异常,复制失败:",e);
        }
        return jsonObject;
    }

    /**
     * 移动分享
     * @param shareId 分享id
     * @param projectId 项目id
     * @return
     */
    @Push(value = PushType.B5,type = 2)
    @PostMapping("/{shareId}/move")
    public JSONObject move(@PathVariable(value = "shareId")String shareId,
                            @RequestParam(value = "projectId")String projectId,
                           @RequestParam(value = "curProjectId")String curProjectId){
        JSONObject jsonObject = new JSONObject();
        try{
            shareService.moveShare(shareId,curProjectId);
            UserEntity entity = userService.getById(ShiroAuthenticationManager.getUserId());
            Project project = projectService.getById(curProjectId);
            logService.saveLog(shareId,entity + "将分享移动到了" + project.getProjectName() ,4);

            Map<String, Object> maps = new HashMap<String, Object>(2);
            if (projectId.equals(curProjectId)) {
                maps.put(projectId, projectId);
            } else {
                maps.put(projectId, projectId);
                maps.put(curProjectId, curProjectId);
            }
            jsonObject.put("data", maps);
            jsonObject.put("result",1);
        }catch(Exception e){
            log.error("系统异常,移动失败:",e);
        }
        return jsonObject;
    }

    /**
     * 从分享页面上传 图片
     * @param file 文件对象
     * @return
     */
    @PostMapping("/{projectId}/upload")
    public JSONObject uploadImage(@PathVariable(value = "projectId") String projectId, MultipartFile file){
        JSONObject jsonObject = new JSONObject();
        try {
            // 得到文件名
            String originalFilename = file.getOriginalFilename();
            // 重置文件名
            String fileName = System.currentTimeMillis() + originalFilename.substring(originalFilename.lastIndexOf("."));
            //设置url
            String parentUrl = projectId.concat("/upload_share/");
            // 设置文件url
            String fileUrl = parentUrl + fileName;
            // 上传oss，相同的objectName会覆盖
            AliyunOss.uploadInputStream(fileUrl, file.getInputStream());
            jsonObject.put("file_path",fileUrl);
            jsonObject.put("success",1);
            jsonObject.put("msg","文件上传成功!");
        } catch (Exception e){
            throw new AjaxException("文件上传失败!",e);
        }
        return jsonObject;
    }

    /**
     * 更换分享的隐私模式
     * @param shareId 分享id
     * @return
     */
    @Log
    @Push(PushType.B8)
    @PutMapping("/{shareId}/privacy")
    public JSONObject privacy(@PathVariable(value = "shareId")String shareId,@RequestParam(value = "projectId")String projectId){
        JSONObject jsonObject = new JSONObject();
        try{
            shareService.updatePrivacy(shareId);
            jsonObject.put("result",1);
            jsonObject.put("data",shareId);
            jsonObject.put("msgId",projectId);
        }catch(Exception e){
            log.error("系统异常:",e);
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 获取项目下的分享基本信息(用于绑定处)
     * @param projectId 项目id
     * @return 分享集合
     */
    @GetMapping("/{projectId}/bind")
    public JSONObject getBindInfo(@PathVariable String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<Share> bindInfo = shareService.getBindInfo(projectId);
            jsonObject.put("data",bindInfo);
            jsonObject.put("result",1);
            return jsonObject;
        } catch (Exception e){
            throw new AjaxException("系统异常,获取绑定信息失败!",e);
        }
    }

    /**
     * 置顶分享
     */
    @Log(PushType.B3)
    @Push(PushType.B3)
    @PostMapping("/{shareId}/top")
    public JSONObject setTop(@PathVariable String shareId,
                             @RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try{
            Share share = new Share();
            share.setId(shareId);
            share.setCreateTime(System.currentTimeMillis());
            shareService.updateById(share);
            jsonObject.put("result",1);
            jsonObject.put("data",projectId);
            jsonObject.put("msgId",projectId);
            return jsonObject;
        }catch (Exception e){
            throw new AjaxException("置顶失败");
        }
    }

    /**
     * 编辑分享
     * @param shareId 分享id
     * @param title 分享标题
     * @param content 分享内容
     * @param isPrivacy 是否是隐私模式
     * @return
     */
    @Log(PushType.B2)
    @Push(PushType.B2)
    @PostMapping("/{shareId}")
    public JSONObject editShare(
            @RequestParam(value = "projectId") String projectId,
            @PathVariable(value = "shareId") String shareId,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "isPrivacy",required = false) Integer isPrivacy,
            @RequestParam(value = "joinIds") String joinIds
    ){
        JSONObject object = new JSONObject();
        String  userId = ShiroAuthenticationManager.getUserId();
        try {
            Share share = new Share();
            share.setId(shareId);
            share.setTitle(title);
            share.setContent(content);
            share.setIsPrivacy(isPrivacy);
            share.setMemberId(userId);
            share.setUids(userId);
            share.setProjectId(projectId);
            share.setUids(joinIds);
            share.setUpdateTime(System.currentTimeMillis());
            shareService.updateById(share);
            object.put("result",1);
            object.put("data",share);
            object.put("msgId",projectId);
        } catch (Exception e){
            log.error("保存分享异常:",e);
            throw new AjaxException(e);
        }
        return object;
    }


}
