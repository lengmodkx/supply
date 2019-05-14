package com.art1001.supply.service.recycle;

import com.art1001.supply.entity.base.RecycleBinVO;

import java.util.List;

/**
 * @Description 回收站接口
 * @Date:2019/5/14 10:51
 * @Author ddm
 **/
public interface RecycleBinService {

    /**
     * 获取项目中回收站的指定类型的信息
     * @param projectId 项目id
     * @param type 类型
     * @param fileType 文件类型(文件夹还是文件)
     * @return 信息
     */
    List<RecycleBinVO> getRecycleBinItem(String projectId, String type, String fileType);
}