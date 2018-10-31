package com.art1001.supply.entity.tag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * Entity
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName(value = "prm_tag_relation")
public class TagRelation extends Model<TagRelation> {
	
	private static final long serialVersionUID = 1L;


	/**
	 * id
	 */
	@TableId(value = "id",type = IdType.UUID)
	private String id;


	/**
	 * 标签id
	 */
	private long tagId;


	/**
	 * 任务id
	 */
	private String taskId;


	/**
	 * 文件id
	 */
	private String fileId;


	/**
	 * 日程id
	 */
	private String scheduleId;


	/**
	 * 分享id
	 */
	private String shareId;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}