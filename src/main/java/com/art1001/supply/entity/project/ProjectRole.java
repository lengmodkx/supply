package com.art1001.supply.entity.project;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prm_project_role")
public class ProjectRole extends Model<ProjectRole>{

    /**
      * id
      */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
      * 项目角色名
      */
    private String projectRoleName;

    /**
      * 创建时间
      */
    private Date createTime;

    /**
      * 修改时间
      */
    private Date updateTime;

    /**
     * 项目id
     */
    private String projectId;

}
