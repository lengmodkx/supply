package com.art1001.supply.entity.wechat;

import lombok.Data;

/**
 * @Description
 * @Date:2019/8/31 11:10
 * @Author heshaohua
 **/
@Data
public class WechatParam {

    /**
     * 订单金额【备注：以分为单位】
     */
    private String totalFee;

    /**
     * 商品名称
     */
    private String body;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 附加参数
     */
    private String attach;

    /**
     * 会员ID
     */
    private String memberid;

    /**
     * 商品id
     */
    private Integer productId;


}
