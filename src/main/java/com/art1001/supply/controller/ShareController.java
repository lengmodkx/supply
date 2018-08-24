package com.art1001.supply.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.controller.base.BaseController;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.binding.BindingVo;
import com.art1001.supply.entity.collect.PublicCollect;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.task.PushType;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.CommonUtils;
import com.art1001.supply.util.IdGen;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/share")
public class ShareController extends BaseController {
    @Resource
    private ProjectService projectService;

    @Resource
    private ShareService shareService;

    @Resource
    private TagService tagService;

    @Resource
    private ProjectMemberService projectMemberService;

    @Resource
    private PublicCollectService publicCollectService;

    @Resource
    private BindingService bindingService;

    @Resource
    private LogService logService;

    @Resource
    private UserService userService;

    @Resource
    private SimpMessagingTemplate messagingTemplate;


    //导航到分享界面
    @RequestMapping("/share.html")
    public String share(@RequestParam String projectId,String shareId, Model model){

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
        model.addAttribute("shareList",shareList);

        //查询出分享的关联信息
        for (Share s : shareList) {
            BindingVo bindingVo = bindingService.listBindingInfoByPublicId(s.getId());
            s.setBindingVo(bindingVo);
        }

        if(!StringUtils.isEmpty(shareId)){
            model.addAttribute("shareId",shareId);
        }
        String userId = ShiroAuthenticationManager.getUserId();
        UserEntity userEntity = userService.findById(userId);
        model.addAttribute("user",userEntity);
        model.addAttribute("project",projectService.findProjectByProjectId(projectId));
        return "share";
    }

    @RequestMapping("/toAddShare.html")
    public String toAddShare(@RequestParam String projectId,
                             @RequestParam(required = false) String shareId,
                             Model model) {

        model.addAttribute("projectId", projectId);
        model.addAttribute("share",shareService.findById(shareId));

        return "share_edit";
    }

    /**
     * 打开项目成员弹窗
     */
    @RequestMapping("toSearchPeople")
    public String toSearchPeople(
            @RequestParam String projectId,
            @RequestParam String shareId,
            Model model
    ) {
        Share byId = shareService.findById(shareId);
        List<UserEntity> projectAllMember = userService.findProjectAllMember(projectId);
        List<UserEntity> manyUserById = userService.findManyUserById(byId.getUids());
        List<UserEntity> reduce1 = projectAllMember.stream().filter(item -> !manyUserById.contains(item)).collect(Collectors.toList());
        model.addAttribute("projectMemberList", reduce1);
        model.addAttribute("shareJoins",manyUserById);
        model.addAttribute("shareId", shareId);
        return "tk-share-people";
    }

    /**
     * 添加项目成员
     */
    @RequestMapping("/addShareMember")
    @ResponseBody
    public JSONObject addShareMember(
            @RequestParam String shareId,
            @RequestParam String memberId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", 1);
            jsonObject.put("msg", "添加成功");
        } catch (Exception e) {
            log.error("添加参与者异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "添加失败");
        }
        return jsonObject;
    }

    //添加一个分享
    @RequestMapping("/saveShare")
    @ResponseBody
    public JSONObject saveShare(
            @RequestParam String projectId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Integer isPrivacy
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
            jsonObject.put("msg", "保存成功");
        } catch (Exception e){
            log.error("保存分享异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "保存失败");
        }
        return jsonObject;
    }

    @RequestMapping("/addTag")
    @ResponseBody
    public JSONObject addTag(
            @RequestParam String shareId,
            @RequestParam String tagId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            Share share = shareService.findById(shareId);
            if (StringUtils.isNotEmpty(share.getTagIds())) {
                String[] tagIdArr = share.getTagIds().split(",");
                if (CommonUtils.useList(tagIdArr, tagId)) { // 已经存在，移除
                    StringBuilder tagIds = new StringBuilder();
                    for (String tId : tagIdArr) {
                        if (!tagId.equals(tId)) {
                            tagIds.append(tId).append(",");
                        }
                    }
                    if (StringUtils.isNotEmpty(tagIds)) {
                        tagIds.deleteCharAt(tagIds.length() - 1);
                    }
                    shareService.deleteTag(shareId, tagIds.toString());
                    jsonObject.put("result", 2);
                    jsonObject.put("msg", "移除成功");
                } else { // 不存在添加
                    String tagIds = share.getTagIds();
                    if (StringUtils.isNotEmpty(share.getTagIds())) {
                        tagIds += "," + tagId;
                    } else {
                        tagIds = tagId;
                    }

                    shareService.deleteTag(shareId, tagIds);
                    Tag tag = tagService.findById(Integer.valueOf(tagId));
                    jsonObject.put("result", 1);
                    jsonObject.put("data", tag);
                    jsonObject.put("msg", "添加成功");
                }
            } else {
                String tagIds = share.getTagIds();
                if (StringUtils.isNotEmpty(share.getTagIds())) {
                    tagIds += "," + tagId;
                } else {
                    tagIds = tagId;
                }

                shareService.deleteTag(shareId, tagIds);
                Tag tag = tagService.findById(Integer.valueOf(tagId));
                jsonObject.put("result", 1);
                jsonObject.put("data", tag);
                jsonObject.put("msg", "添加成功");
            }


        } catch (Exception e) {
            log.error("添加标签异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "添加失败");
        }
        return jsonObject;
    }


    /**
     * 移除分享的标签
     *
     * @param shareId 分享id
     * @param tagId 标签id
     */
    @RequestMapping("/deleteTag")
    @ResponseBody
    public JSONObject deleteTag(
            @RequestParam String shareId,
            @RequestParam String tagId
    ) {
        JSONObject jsonObject = new JSONObject();
        try {
            Share share = shareService.findById(shareId);
            String[] tagIdArr = share.getTagIds().split(",");
            StringBuilder tagIds = new StringBuilder();
            for (String tId : tagIdArr) {
                if (!tagId.equals(tId)) {
                    tagIds.append(tId).append(",");
                }
            }
            if (StringUtils.isNotEmpty(tagIds)) {
                tagIds.deleteCharAt(tagIds.length() - 1);
            }

            shareService.deleteTag(shareId, tagIds.toString());
            jsonObject.put("result", 1);
            jsonObject.put("msg", "移除成功");
        } catch (Exception e) {
            log.error("移除标签异常, {}", e);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "移除失败");
        }
        return jsonObject;
    }

    @PostMapping("shareByProjectId")
    @ResponseBody
    public JSONObject shareByProjectId(String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<Share> list = shareService.shareByProjectId(projectId);
            jsonObject.put("data",list);
            jsonObject.put("result",1);
        } catch (Exception e){
            e.printStackTrace();
            jsonObject.put("result",0);
            log.error("系统异常,数据拉取失败!");
        }
        return jsonObject;
    }


    //收藏分享
    @PostMapping("shareCollect")
    @ResponseBody
    public JSONObject shareCollect(String shareId,String projectId){
        JSONObject jsonObject = new JSONObject();
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        try{

            int judge = publicCollectService.judgeCollectPublic(userEntity.getId(), shareId, "分享");
            if(judge==1){
                jsonObject.put("result",1);
                jsonObject.put("msg","已经收藏");
            }else{
                PublicCollect publicCollect = new PublicCollect();
                publicCollect.setId(IdGen.uuid());
                publicCollect.setPublicId(shareId);
                publicCollect.setProjectId(projectId);
                publicCollect.setMemberId(userEntity.getId());
                publicCollect.setCollectType("分享");
                publicCollectService.savePublicCollect(publicCollect);
                jsonObject.put("result",1);
                jsonObject.put("msg","收藏成功");
            }

        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    //删除分享
    @PostMapping("shareDelete")
    @ResponseBody
    public JSONObject shareDelate(String shareId){
        JSONObject jsonObject = new JSONObject();
        try{
            shareService.deleteById(shareId);
            jsonObject.put("result",1);
            jsonObject.put("msg","移除成功");

        }catch (Exception e){
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 移入回收站
     * @param shareId 分享id
     * @param projectId 项目id
     * @return
     */
    @PostMapping("moveToRecycleBin")
    @ResponseBody
    public JSONObject moveToRecycleBin(String shareId, String projectId){
        JSONObject jsonObject = new JSONObject();
        JSONObject pushData = new JSONObject();
        try {
            shareService.moveToRecycleBin(shareId);
            jsonObject.put("result",1);

            //封装推送数据
            pushData.put("shareId",shareId);
            pushData.put("type","将分享移入了回收站");
            messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(pushData)));
        } catch (Exception e){
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,操作失败!");
            e.printStackTrace();
            log.error("系统异常,操作失败!");
        }
        return jsonObject;
    }

    /**
     * 获取项目下的所有标签
     * @param projectId 项目id
     * @return
     */
    @PostMapping("findAllTags")
    @ResponseBody
    public JSONObject findAllTags(@RequestParam String projectId){
        JSONObject jsonObject = new JSONObject();
        try {
            List<Tag> tagList = tagService.findByProjectId(projectId);
            jsonObject.put("data",tagList);
            jsonObject.put("result",1);

        } catch (Exception e){
            log.error("系统异常,标签获取失败!");
            throw new AjaxException(e);
        }
        return jsonObject;
    }

    /**
     * 分享的聊天室
     * @return
     */
    @PostMapping("chat")
    @ResponseBody
    public JSONObject chat(String projectId,String shareId,String content){
        JSONObject jsonObject = new JSONObject();
        try {
            Log log = new Log();
            log.setId(IdGen.uuid());
            log.setContent(ShiroAuthenticationManager.getUserEntity().getUserName()+" 说: "+ content);
            log.setLogType(1);
            log.setMemberId(ShiroAuthenticationManager.getUserId());
            log.setPublicId(shareId);
            log.setLogFlag(4);
            log.setCreateTime(System.currentTimeMillis());
            Log log1 = logService.saveLog(log);
            jsonObject.put("result",1);
            //推送数据
            PushType taskPushType = new PushType(TaskLogFunction.A14.getName());
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("shareLog",log1);
            map.put("shareId",shareId);
            taskPushType.setObject(map);
            //推送至分享的详情界面
            messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(taskPushType)));
            //推送至分享的详情界面
            messagingTemplate.convertAndSend("/topic/"+shareId,new ServerMessage(JSON.toJSONString(taskPushType)));
        } catch (Exception e){
            jsonObject.put("result",0);
            log.error("系统异常,发送失败,{}",e);
        }
        return jsonObject;
    }

    @PostMapping("addAndRemoveShareMember")
    @ResponseBody
    public JSONObject addAndRemoveShareMember(String shareId,String addUserEntity){
        JSONObject jsonObject = new JSONObject();
        try {
            shareService.addAndRemoveShareMember(shareId,addUserEntity);
            shareService.addAndRemoveShareMember(shareId,addUserEntity);
            jsonObject.put("result",1);
        } catch (Exception e){
            log.error("系统异常,操作失败!");
            jsonObject.put("msg","系统异常,操作失败!");
            jsonObject.put("result",0);
        }
        return jsonObject;
    }

    /**
     * 点击被关联的分享的时候要跳转到关联分享的详情页面
     * @param shareId 分享id
     * @param model
     * @return
     */
    @RequestMapping("shareInfo.html")
    public String shareInfoById(String shareId, Model model){
        Share byId = shareService.findById(shareId);
        byId.setIsCollect(publicCollectService.isCollItem(shareId));
        byId.setBindingVo(bindingService.listBindingInfoByPublicId(shareId));
        model.addAttribute("share",byId);
        return "revisetShare";
    }

    /**
     * 恢复分享
     * @param shareId 分享id
     * @param projectId 项目id
     * @return
     */
    @PostMapping("recoveryShare")
    @ResponseBody
    public JSONObject recoveryShare(String shareId, String projectId){
        JSONObject jsonObject = new JSONObject();
        JSONObject pushData = new JSONObject();
        try {
            shareService.recoveryShare(shareId);
            jsonObject.put("result",1);

            //包装推送数据
            pushData.put("share",shareService.findById(shareId));
            pushData.put("type","恢复了信息");
            messagingTemplate.convertAndSend("/topic/"+projectId,new ServerMessage(JSON.toJSONString(pushData)));

            pushData.remove("share");
            pushData.put("id",shareId);
            //推送至回收站界面
            messagingTemplate.convertAndSend("/topic/"+projectId+"recycleBin",new ServerMessage(JSON.toJSONString(pushData)));

        } catch (Exception e){
            log.error("系统异常,操作失败!");
            jsonObject.put("result",0);
            jsonObject.put("msg","系统异常,操作失败!");
        }
        return jsonObject;
    }
}
