package com.art1001.supply.service.publics;

/**
 * @Description
 * @Date:2019/4/3 18:00
 * @Author ddm
 **/
public interface PublicService {

    /**
     * 点赞
     * @param publicId 信息id
     * @return 是否成功
     */
    int fabulous(String publicId);
}