package com.art1001.supply.entity.share;

import com.art1001.supply.entity.binding.Binding;
import com.art1001.supply.entity.log.Log;
import com.art1001.supply.entity.tag.Tag;
import com.art1001.supply.entity.user.UserEntity;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * shareEntity
 */
@Data
@ToString
public class Share extends Model<Share> {

	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	private String id;


	/**
	 * 分享标题
	 */
	private String title;


	/**
	 * 分享内容
	 */
	private String content;

	/**
	 * 项目id
	 */
	private String projectId;


	/**
	 * 标签id
	 */
	private String tagIds;


	/**
	 * 用户id
	 */
	private String memberId;


	/**
	 * 用户名
	 */
	private String memberName;


	/**
	 * 头像
	 */
	private String memberImg;

	/**
	 * 分享的创建者信息
	 */
	private UserEntity userEntity;

	/**
	 * 创建时间
	 */
	private Long createTime;

	/**
	 * 更新时间
	 */
	private Long updateTime;

	/**
	 * 私密
	 * 0：公开  1：私密
	 */
	private Integer isPrivacy;

	/**
	 * 该分享的关联内容
	 */
	private List<Binding> bindings;

	private List<Tag> tagList;

	/**
	 * 该任务的 聊天记录 和  log 日志
	 */
	private List<Log> logs;

	/**
	 * 分享的参与者信息
	 */
	private String uids;

	List<UserEntity> joinInfo;


	private String createTimeStr;

    public String getCreateTimeStr() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(createTime);
    }

    /**
	 * 是否被删除
	 * @return
	 */
	private int isDel;

	@Override
	protected Serializable pkVal() {
		return this.id;
	}
}
