package com.art1001.supply.entity.user;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Date:2019/6/11 10:27
 * @Author heshaohua
 **/
@Data
public class WeChatUser {

    // 用户标识
      private String openId;
      // 用户昵称
      private String nickname;
      // 性别（1是男性，2是女性，0是未知）
      private int sex;
      // 国家
      private String country;
      // 省份
      private String province;
      // 城市
      private String city;
      // 用户头像链接
      private String headImgUrl;
      // 用户特权信息
      private List<String> privilegeList;

      private String unionid;
}
