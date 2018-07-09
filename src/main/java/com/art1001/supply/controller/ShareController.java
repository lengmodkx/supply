package com.art1001.supply.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.exception.AjaxException;
import com.art1001.supply.service.project.ProjectMemberService;
import com.art1001.supply.service.project.ProjectService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.tag.TagService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.CommonUtils;
import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/share")
public class ShareController {
    @Resource
    private ProjectService projectService;

    @Resource
    private ShareService shareService;

    @Resource
    private TagService tagService;

    @Resource
    private ProjectMemberService projectMemberService;

    //导航到分享界面
    @RequestMapping("/share.html")
    public String share(@RequestParam String projectId, Model model){

        List<Share> shareList = shareService.findByProjectId(projectId, 0);
        shareList.forEach(share -> {
            String tagIdsStr = share.getTagIds();
            if (StringUtils.isNotEmpty(tagIdsStr)) {
                String[] tagIdArrStr = tagIdsStr.split(",");

                if (tagIdArrStr.length > 0) {
                    Integer[] tagIdArr = new Integer[tagIdArrStr.length];
                    for (int i = 0; i < tagIdArrStr.length; i++) {
                        tagIdArr[i] = Integer.valueOf(tagIdArrStr[i]);
                    }
                    List<Tag> tagList = tagService.findByIds(tagIdArr);
                    share.setTagList(tagList);
                }
            }
        });
        List<Tag> tagList = tagService.findByProjectId(projectId);
        model.addAttribute("shareList",shareList);
        model.addAttribute("tagList",tagList);
        model.addAttribute("user",ShiroAuthenticationManager.getUserEntity());
        model.addAttribute("project",projectService.findProjectByProjectId(projectId));
        return "share";
    }

    @RequestMapping("/toAddShare.html")
    public String toAddShare(@RequestParam String projectId, Model model) {
        model.addAttribute("projectId", projectId);
        return "share_edit";
    }

    @RequestMapping("toSearchPeople")
    public String toSearchPeople(
            @RequestParam String projectId,
            Model model
    ) {
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();
        List<ProjectMember> projectMemberList = projectMemberService.findByProjectIdAndMemberId(projectId, userEntity.getId());
        model.addAttribute("projectMemberList", projectMemberList);
        return "tk-search-people";
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
        try {
            Share share = new Share();
            share.setTitle(title);
            share.setContent(content);
            share.setProjectId(projectId);
            share.setIsPrivacy(isPrivacy);
            shareService.saveShare(share);
            jsonObject.put("result", 1);
            jsonObject.put("msg", "保存成功");

        }catch (Exception e){
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

}
