package com.art1001.supply.service.file;

import com.art1001.supply.entity.file.MemberDownload;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface MemberDownloadService extends IService<MemberDownload> {
    /**
     * 获取用户已下载信息
     * @param memberId
     * @return
     */
    List<MemberDownload> getIsDownload(String memberId);
}
