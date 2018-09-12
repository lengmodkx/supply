package com.art1001.supply.api;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.binding.BindingVo;
import com.art1001.supply.entity.relation.GroupVO;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.service.binding.BindingService;
import com.art1001.supply.service.collect.PublicCollectService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.relation.RelationService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
     * @param currentGroup 当前分组id
     * @return
     */
    @GetMapping("/{projectId}")
    public JSONObject share(@PathVariable String projectId, String shareId, @RequestParam("currentGroup")String currentGroup){
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
            jsonObject.put("currentGroup",currentGroup);

            //查询出分享的关联信息
            for (Share s : shareList) {
                BindingVo bindingVo = bindingService.listBindingInfoByPublicId(s.getId());
                s.setBindingVo(bindingVo);
            }

            //加载该项目下所有分组的信息
            List<GroupVO> groups = relationService.loadGroupInfo(projectId);
            jsonObject.put("groups",groups);

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
     * 添加分享
     * @param projectId 项目id
     * @param shareId 分享id (选填)
     * @return
     */
    @PostMapping("/toAddShare")
    public JSONObject toAddShare(@RequestParam String projectId,
                             @RequestParam(required = false) String shareId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("projectId", projectId);
            jsonObject.put("share",shareService.findById(shareId));
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
    @GetMapping("SearchPeople")
    public String SearchPeople(
            @RequestParam String projectId,
            @RequestParam String shareId
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
        return "tk-share-people";
    }

    /**
     * 添加项目成员
     * @param shareId 分享id
     * @param memberId 成员id
     * @return
     */
    @PostMapping("/addShareMember")
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
            log.error("添加参与者异常", e.getMessage());
            jsonObject.put("result", 0);
            jsonObject.put("msg", "添加失败");
        }
        return jsonObject;
    }
}
