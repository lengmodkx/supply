package com.art1001.supply.mapper.file;

import com.art1001.supply.entity.file.MemberDownload;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberDownloadMapper extends BaseMapper<MemberDownload> {
    List<MemberDownload> getIsDownload(@Param("memberId") String memberId);
}
