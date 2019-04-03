package com.art1001.supply.service.share.impl;
import com.art1001.supply.entity.base.RecycleBinVO;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.share.Share;
import com.art1001.supply.entity.share.ShareApiBean;
import com.art1001.supply.entity.user.UserEntity;
import com.art1001.supply.enums.TaskLogFunction;
import com.art1001.supply.mapper.share.ShareMapper;
import com.art1001.supply.service.log.LogService;
import com.art1001.supply.service.share.ShareService;
import com.art1001.supply.service.user.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * shareServiceImpl
 */
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper,Share> implements ShareService {

	/** shareMapper接口*/
	@Resource
	private ShareMapper shareMapper;

	@Resource
    private UserService userService;

	@Resource
    private LogService logService;

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
	 * 添加或者移除掉 分享的参与者 然后将结果推送
	 * @param shareId 分享的id
	 * @param addUserEntity 要添加的成员id
	 */
	@Override
	public void updateMembers(String shareId, String addUserEntity) {
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
				UserEntity userEntity = userService.findById(uId);
				logContent.append(userEntity.getUserName()).append(" ");
			}
		}

		//比较 newJoin  和 oldJoin 两个集合的差集  (添加)
		List<String> reduce2 = newJoin.stream().filter(item -> !oldJoin.contains(item)).collect(Collectors.toList());
		if(reduce2 != null && reduce2.size() > 0){
			logContent.append(TaskLogFunction.C.getName()).append(" ");
			for (String uId : reduce2) {
				UserEntity userEntity = userService.findById(uId);
				logContent.append(userEntity.getUserName()).append(" ");
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
		    updateById(share);
			log = logService.saveLog(shareId,logContent.toString(),4);
		}
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

	/**
	 * 复制分享
	 * @param shareId 分享id
	 * @param projectId 项目id
	 */
	@Override
	public void copyShare(String shareId, String projectId) {

	}

	@Override
	public void moveShare(String shareId, String projectId) {

	}

	@Override
	public void updatePrivacy(String shareId) {

	}

	/**
	 * 根据id 查询出分享信息
	 * @param shareId 分享id
	 * @return
	 */
	@Override
	public Share findByIdAllInfo(String shareId) {
		Share byId = shareMapper.findById(shareId);
		return byId;
	}

	/**
	 * 查询分享部分信息 (项目名称,分享名称,执行者头像,标题,内容)
	 * @param id 分享id
	 * @return
	 */
	@Override
	public ShareApiBean findShareApiBean(String id) {
		return shareMapper.selectShareApiBean(id);
	}

	/**
	 * 获取分享信息
	 * @param projectId  项目id
	 * @return 分享信息
	 */
	@Override
	public List<Share> getBindInfo(String projectId) {
		return shareMapper.getBindInfo(projectId);
	}
}