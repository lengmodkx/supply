package com.art1001.supply.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.annotation.Todo;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.BindingVo;
import com.art1001.supply.entity.relation.GroupVO;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 注入收藏逻辑层实例
     */
    @Resource
    private PublicCollectService publicCollectService;

    /**
     * 注入关联逻辑层实例
     */
    @Resource
    private BindingService bindingService;

    /**
     * 注入分组/菜单逻辑层实例
     */
    @Resource
    private RelationService relationService;

    /**
     * 注入用户信息逻辑层实例
     */
    @Resource
    private UserService userService;

    /**
     * 注入项目逻辑层实例
     */
    @Resource
    private ProjectService projectService;

    /**
     * 加载分享页面
     * @param projectId 项目id
     * @param shareId 分享id (可选)
     * @return
     */
    @GetMapping("/{projectId}")
    public JSONObject share(@PathVariable String projectId, String shareId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<Share> shareList = shareService.findByProjectId(projectId, 0);
            for (Share s : shareList) {
                for (int i = 0;i < s.getJoinInfo().size();i++){
                    if(s.getMemberId().equals(s.getJoinInfo().get(i).getId())){
                        s.getJoinInfo().remove(s.getJoinInfo().get(i));
                    }
                }
                Collections.reverse(s.getLogs());
            }
            //判断当前用户有没有收藏该任务
            for (Share share : shareList) {
                share.setIsCollect(publicCollectService.isCollItem(share.getId()));
            }
            jsonObject.put("shareList",shareList);

            //查询出分享的关联信息
            for (Share s : shareList) {
                BindingVo bindingVo = bindingService.listBindingInfoByPublicId(s.getId());
                s.setBindingVo(bindingVo);
            }

            if(!StringUtils.isEmpty(shareId)){
                jsonObject.put("shareId",shareId);
            }
            String userId = ShiroAuthenticationManager.getUserId();
            UserEntity userEntity = userService.findById(userId);
            jsonObject.put("user",userEntity);
            jsonObject.put("project",projectService.findProjectByProjectId(projectId));
            jsonObject.put("result",1);
        } catch (Exception e){
            jsonObject.put("msg","系统异常");
            jsonObject.put("result",0);
        }
        return jsonObject;
    }

    /**
     * 加载成员数据
     * @param projectId 项目id
     * @param shareId 分享id
     * @return
     */
    @GetMapping("/{shareId}/searchPeople")
    public JSONObject searchPeople(
            @PathVariable String shareId,
            @RequestParam String projectId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            Share byId = shareService.findById(shareId);
            List<UserEntity> projectAllMember = userService.findProjectAllMember(projectId);
            List<UserEntity> manyUserById = userService.findManyUserById(byId.getUids());
            List<UserEntity> reduce1 = projectAllMember.stream().filter(item -> !manyUserById.contains(item)).collect(Collectors.toList());
            jsonObject.put("projectMemberList", reduce1);
            jsonObject.put("shareJoins",manyUserById);
            jsonObject.put("shareId", shareId);
            jsonObject.put("result",1);
        } catch (Exception e){
            jsonObject.put("msg","系统异常!");
            jsonObject.put("result",0);
        }
        return jsonObject;
    }

    /**
     * 添加/移除 分享成员
     * @param shareId 分享id
     * @param memberIds 多个成员id的字符串
     * @return
     */
    @Todo
    @PostMapping("/{shareId}/updateMembers")
    public JSONObject updateMembers(@PathVariable String shareId,@RequestParam(value = "memberIds") String memberIds){
        JSONObject jsonObject = new JSONObject();
        try {
            shareService.updateMembers(shareId,memberIds);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,操作失败!",e.getMessage());
            jsonObject.put("msg","系统异常,操作失败!");
            jsonObject.put("result",0);
        }
        return jsonObject;
    }

    /**
     * 添加分享
     * @param projectId
     * @param title
     * @param content
     * @param isPrivacy
     * @return
     */
    @Todo
    @PostMapping
    public JSONObject saveShare(
            @RequestParam(value = "projectId") String projectId,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "isPrivacy",required = false) Integer isPrivacy
    ){
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        try {
            Share share = new Share();
            share.setTitle(title);
            share.setContent(content);
            share.setProjectId(projectId);
            share.setIsPrivacy(isPrivacy);
            share.setMemberId(userEntity.getId());
            share.setMemberImg(userEntity.getUserInfo().getImage());
            share.setMemberName(userEntity.getUserName());
            share.setUids(userEntity.getId());
            shareService.saveShare(share);
            jsonObject.put("result", 1);
        } catch (Exception e){
            e.printStackTrace();
            log.error("保存分享异常", e.getMessage());
            jsonObject.put("result", 0);
            jsonObject.put("msg", "保存失败");
        }
        return jsonObject;
    }

    /**
     * 删除分享
     * @param shareId 分享id
     * @return
     */
    @DeleteMapping("/{shareId}")
    public JSONObject shareDelate(@PathVariable("shareId") String shareId){
        JSONObject jsonObject = new JSONObject();
        try{
            shareService.deleteById(shareId);
            jsonObject.put("result",1);
        }catch (Exception e){
            log.error("系统异常",e.getMessage());
            e.printStackTrace();
            jsonObject.put("msg","系统异常");
            jsonObject.put("result",0);
        }
        return jsonObject;
    }

    /**
     * 将分享移入回收站
     * @param shareId 分享id
     * @return
     */
    @Todo
    @PutMapping("/{shareId}/moveToRecycleBin")
    public JSONObject moveToRecycleBin(@PathVariable(value = "shareId") String shareId){
        JSONObject jsonObject = new JSONObject();
        try {
            shareService.moveToRecycleBin(shareId);
            jsonObject.put("result",1);
        } catch (Exception e){
            e.printStackTrace();
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,操作失败!");
            log.error("系统异常,操作失败!",e.getMessage());
        }
        return jsonObject;
    }

    /**
     * 恢复分享
     * @param shareId 分享id
     * @return
     */
    @PutMapping("/{shareId}/recoveryShare")
    public JSONObject recoveryShare(@PathVariable("shareId") String shareId){
        JSONObject jsonObject = new JSONObject();
        try {
            shareService.recoveryShare(shareId);
            jsonObject.put("result",1);
        } catch (Exception e){
            e.printStackTrace();
            log.error("系统异常,操作失败!");
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,操作失败!");
        }
        return jsonObject;
    }

    /**
     * 复制分享
     * @param shareId 分享id
     * @param projectId 项目id
     * @return
     */
    @PostMapping("/{shareId}/copy")
    public JSONObject copyTask(@PathVariable(value = "shareId")String shareId,
                               @RequestParam(value = "projectId")String projectId){
        JSONObject object = new JSONObject();
        try{
            shareService.copyShare(shareId,projectId);
            object.put("result",1);
            object.put("msg","复制成功");
        }catch(Exception e){
            throw new AjaxException(e);
        }
        return object;
    }

    /**
     * 移动分享
     * @param shareId 分享id
     * @param projectId 项目id
     * @return
     */
    @PutMapping("/{shareId}/move")
    public JSONObject move(@PathVariable(value = "shareId")String shareId,
                            @RequestParam(value = "projectId")String projectId){
        JSONObject jsonObject = new JSONObject();
        try{
            shareService.moveShare(shareId,projectId);
            jsonObject.put("result",1);
        }catch(Exception e){
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常!");
        }
        return jsonObject;
    }

    /**
     * 更换分享的隐私模式
     * @param shareId 分享id
     * @return
     */
    @Todo
    @PutMapping("/{shareId}/privacy")
    public JSONObject move(@PathVariable(value = "shareId")String shareId){
        JSONObject jsonObject = new JSONObject();
        try{
            shareService.updatePrivacy(shareId);
            jsonObject.put("result",1);
        }catch(Exception e){
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常!");
        }
        return jsonObject;
    }

}
