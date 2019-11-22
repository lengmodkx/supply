package com.art1001.supply.entity.user;

import lombok.Data;
import lombok.ToString;

/**
 * @author heshaohua
 * @date 2019/11/22 10:50
 **/
@Data
@ToString
public class WeChatAppUserInfo {

    private String avatarUrl;

    private Integer gender;

    private String nickName;

    private String province;

}
