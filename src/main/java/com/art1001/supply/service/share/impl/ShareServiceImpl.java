package com.art1001.supply.service.share.impl;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.art1001.supply.base.Base;
import com.art1001.supply.common.Constants;
import com.art1001.supply.entity.ServerMessage;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.binding.BindingConstants;
import com.art1001.supply.entity.file.File;
import com.art1001.supply.entity.file.FilePushType;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.project.ProjectMember;
import com.art1001.supply.entity.task.TaskMember;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.mapper.share.ShareMapper;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.user.UserService;
import com.art1001.supply.shiro.ShiroAuthenticationManager;
import com.art1001.supply.util.IdGen;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.art1001.supply.entity.share.Share;

/**
 * shareServiceImpl
 */
@Service
public class ShareServiceImpl implements ShareService {

	/** shareMapper接口*/
	@Resource
	private ShareMapper shareMapper;

	@Resource
    private UserService userService;

	@Resource
    private LogService logService;

	@Resource
	private Base base;

    @Resource
    private SimpMessagingTemplate messagingTemplate;
	
	/**
	 * 查询分页share数据
	 */
	@Override
	public List<Share> findByProjectId(String projectId, Integer isDel){
		return shareMapper.findByProjectId(projectId, isDel);
	}

	/**
	 * 通过id获取单条share数据
	 * 
	 * @param id
	 * @return
	 */
	@Override 
	public Share findById(String id){
		return shareMapper.findById(id);
	}

	/**
	 * 通过id删除share数据
	 * 
	 * @param id
	 */
	@Override
	public void deleteById(String id){
		base.deleteItemOther(id,BindingConstants.BINDING_SHARE_NAME);
		shareMapper.deleteById(id);
	}

	@Override
	public void deleteTag(String shareId, String tagIds) {
		shareMapper.deleteTag(shareId, tagIds);
	}

	/**
	 * 修改share数据
	 * 
	 * @param share
	 */
	@Override
	public Share updateShare(Share share){
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();

        share.setMemberId(userEntity.getId());
        share.setMemberName(userEntity.getUserName());
        share.setMemberImg(userEntity.getUserInfo().getImage());

        share.setUpdateTime(System.currentTimeMillis());
		shareMapper.updateShare(share);
		return share;
	}
	/**
	 * 保存share数据
	 * 
	 * @param share
	 */
	@Override
	public Share saveShare(Share share){
        UserEntity userEntity = ShiroAuthenticationManager.getUserEntity();

	    share.setId(IdGen.uuid());
        share.setMemberId(userEntity.getId());
        share.setMemberName(userEntity.getUserName());
        share.setMemberImg(userEntity.getUserInfo().getImage());

        share.setCreateTime(System.currentTimeMillis());
        share.setUpdateTime(System.currentTimeMillis());

        shareMapper.saveShare(share);

        return share;
	}

	/**
	 * 根据项目id 查询出项目下的所有分享信息
	 * @param projectId 项目id
	 * @return 返回分享信息  集合
	 */
	@Override
	public List<Share> shareByProjectId(String projectId) {
		return shareMapper.shareByProjectId(projectId);
	}

	/**
	 * 查询出分享的参与人员
	 * @param shareId 分享的id
	 * @return 参与者的信息
	 */
	@Override
	public List<ProjectMember> shareJoinInfo(String shareId) {
		return shareMapper.shareJoinInfo(shareId);
	}

	/**
	 * 查询出项目的成员信息 排除 分享的参与者
	 * @param projectId 项目id
	 * @param shareId 分享id
	 * @return
	 */
	@Override
	public List<ProjectMember> findProjectMemberNotShareJoin(String projectId, String shareId) {
		return shareMapper.findProjectMemberShareJoin(projectId,shareId);
	}

	/**
	 * 添加或者移除掉 分享的参与者 然后将结果推送
	 * @param shareId 分享的id
	 * @param addUserEntity 要添加的成员id
	 */
	@Override
	public void addAndRemoveShareMember(String shareId, String addUserEntity) {
		//查询出当前文件中的 参与者id
        Share byId = shareMapper.findById(shareId);

        //log日志
		Log log = new Log();
		StringBuilder logContent = new StringBuilder();

		//将数组转换成集合
		List<String> oldJoin = Arrays.asList(byId.getUids().split(","));
		List<String> newJoin = Arrays.asList(addUserEntity.split(","));

		//比较 oldJoin 和 newJoin 两个集合的差集  (移除)
		List<String> reduce1 = oldJoin.stream().filter(item -> !newJoin.contains(item)).collect(Collectors.toList());
		if(reduce1 != null && reduce1.size() > 0){
			logContent.append(TaskLogFunction.B.getName()).append(" ");
			for (String uId : reduce1) {
				String userName = userService.findUserNameById(uId);
				logContent.append(userName).append(" ");
			}
		}

		//比较 newJoin  和 oldJoin 两个集合的差集  (添加)
		List<String> reduce2 = newJoin.stream().filter(item -> !oldJoin.contains(item)).collect(Collectors.toList());
		if(reduce2 != null && reduce2.size() > 0){
			logContent.append(TaskLogFunction.C.getName()).append(" ");
			for (String uId : reduce2) {
				String userName = userService.findUserNameById(uId);
				logContent.append(userName).append(" ");
			}
		}

		//如果没有参与者变动直接返回
		if((reduce1 == null && reduce1.size() == 0) && (reduce2 == null && reduce2.size() == 0)){
			return;
		} else{
		    Share share = new Share();
		    share.setId(shareId);
		    share.setUids(addUserEntity);
		    share.setUpdateTime(System.currentTimeMillis());
		    shareMapper.updateShare(share);
			log = logService.saveLog(shareId,logContent.toString(),4);

			//推送信息
			FilePushType filePushType = new FilePushType(TaskLogFunction.A19.getName());
			Map<String,Object> map = new HashMap<String,Object>();
			List<UserEntity> adduser = new ArrayList<UserEntity>();
			map.put("log",log);
			for (String id : reduce2) {
				adduser.add(userService.findById(id));
			}
			map.put("reduce2",reduce2);
            map.put("reduce1",reduce1);
			map.put("adduser",adduser);
			map.put("shareId",shareId);
			filePushType.setObject(map);
			//推送至分享的详情界面
			messagingTemplate.convertAndSend("/topic/"+byId.getProjectId(),new ServerMessage(JSON.toJSONString(filePushType)));
			//推送至分享的详情界面
			messagingTemplate.convertAndSend("/topic/"+shareId,new ServerMessage(JSON.toJSONString(filePushType)));
		}
	}

	/**
	 * 清空分享的标签
	 * @param shareId 分享的id
	 */
	@Override
	public void shareClearTag(String shareId) {
		shareMapper.shareClearTag(shareId);
	}

	/**
	 * 根据id 查询出该分享的标题
	 * @param publicId 分享id
	 * @return
	 */
	@Override
	public String findShareNameById(String publicId) {
		return shareMapper.findShareNameById(publicId);
	}

	/**
	 * 查询出在回收站中的分享
	 * @param projectId 项目id
	 * @return 该项目下所有在回收站的分享集合
	 */
	@Override
	public List<RecycleBinVO> findRecycleBin(String projectId) {
		return shareMapper.findRecycleBin(projectId);
	}

	/**
	 * 恢复分享内容
	 * @param shareId 分享的id
	 */
	@Override
	public void recoveryShare(String shareId) {
		shareMapper.recoveryShare(shareId);
		logService.saveLog(shareId,TaskLogFunction.A27.getName(),1);
	}

	/**
	 * 移入回收站
	 * @param shareId 分享id
	 */
	@Override
	public void moveToRecycleBin(String shareId) {
		shareMapper.moveToRecycleBin(shareId,System.currentTimeMillis());
	}
}