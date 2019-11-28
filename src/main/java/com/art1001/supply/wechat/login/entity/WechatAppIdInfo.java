package com.art1001.supply.wechat.login.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author heshaohua
 * @since 2019-11-26
 */
@Data
public class WechatAppIdInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String userId;

    private String openId;

    private Long updateTime;

    private Long createTime;

    private Integer type;


}
