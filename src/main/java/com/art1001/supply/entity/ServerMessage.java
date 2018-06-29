package com.art1001.supply.entity;

import lombok.Data;

/**
 *
 * @ClassName: ServerMessage
 * @Description: 服务端发送消息实体
 * @author cheng
 * @date 2017年9月27日 下午4:25:26
 */
@Data
public class ServerMessage {

    private String responseMessage;

    public ServerMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
