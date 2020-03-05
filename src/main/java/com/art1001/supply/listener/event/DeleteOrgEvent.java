package com.art1001.supply.listener.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author shaohua
 * @date 2020/2/28 10:15
 */
public class DeleteOrgEvent extends ApplicationEvent {

    public DeleteOrgEvent(Object source) {
        super(source);
    }


}
