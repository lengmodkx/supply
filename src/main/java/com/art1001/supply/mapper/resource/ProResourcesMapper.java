package com.art1001.supply.mapper.resource;

import com.art1001.supply.entity.resource.ProResources;
import com.art1001.supply.entity.resource.ResourceShowVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 资源表 Mapper 接口
 * </p>
 *
 * @author heshaohua
 * @since 2019-06-18
 */
public interface ProResourcesMapper extends BaseMapper<ProResources> {

    List<ResourceShowVO> selectAll();

    List<ProResources> selectRoleHaveResources(@Param("ids") List<String> ids);
}
