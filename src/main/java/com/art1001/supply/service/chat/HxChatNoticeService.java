package com.art1001.supply.service.chat;

import com.alibaba.fastjson.JSONObject;
import com.art1001.supply.entity.chat.HxChatNotice;
import com.baomidou.mybatisplus.extension.service.IService;

public interface HxChatNoticeService extends IService<HxChatNotice> {
    /**
     * 保存未接收环信消息消息数量
     * @param memberId
     * @param contentFrom
     * @param hxGroupId
     * @param groupId
     * @return
     */
    Integer saveChatCount(String memberId, Integer contentFrom, String hxGroupId, String groupId);

    JSONObject getChatCount();

}
