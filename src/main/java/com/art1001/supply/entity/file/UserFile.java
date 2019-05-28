package com.art1001.supply.entity.file;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@TableName("prm_user_file")
public class UserFile extends Model<File> {

    @TableId(value = "file_id",type = IdType.AUTO)
    private String id;

    //用户id
    private String userId;

    //文件id
    private String fileId;

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
