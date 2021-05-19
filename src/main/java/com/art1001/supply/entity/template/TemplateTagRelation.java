package com.art1001.supply.entity.template;

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
@TableName(value = "template_tag_relation")
public class TemplateTagRelation extends Model<TemplateTagRelation> {
	
	/**
	 * id
	 */
	@TableId(value = "id",type = IdType.ASSIGN_UUID)
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