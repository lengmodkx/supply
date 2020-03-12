package com.art1001.supply.service.recycle;

import com.art1001.supply.api.request.RecycleBinParamDTO;
import com.art1001.supply.entity.base.RecycleBinVO;

import java.util.List;

/**
 * @author heshaohua
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

    /**
     * 恢复项目回收站中的信息
     * @author heShaoHua
     * @param recycleParams 需要恢复的信息参数
     * @date 2019/6/5 11:03
     * @return 结果
     */
    void recovery(RecycleBinParamDTO recycleParams);

}