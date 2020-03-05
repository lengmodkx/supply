package com.art1001.supply.listener;

import com.art1001.supply.listener.event.DeleteOrgEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author shaohua
 * @date 2020/2/28 10:19
 */
@Slf4j
@Component
public class DeleteOrgListener {

    @EventListener
    public void onApplicationEvent(DeleteOrgEvent deleteOrgEvent) {
        log.info("收到通知");
    }
}
