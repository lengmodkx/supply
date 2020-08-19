package com.art1001.supply.service.chat;

import com.art1001.supply.entity.chat.HxChatNotice;
import com.baomidou.mybatisplus.extension.service.IService;

public interface HxChatNoticeService extends IService<HxChatNotice> {
    Integer saveChatCount(String memberId, Integer contentFrom, String hxGroupId, String groupId);

}
