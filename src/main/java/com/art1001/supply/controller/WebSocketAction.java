package com.art1001.supply.controller;

import com.art1001.supply.entity.ClientMessage;
import com.art1001.supply.entity.ServerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
public class WebSocketAction {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private SimpMessagingTemplate messagingTemplate;


    @RequestMapping("/test.html")
    public String test() {
        return "test2";
    }

    @MessageMapping("/sendTest/{id}")
    @SendTo("/topic/{id}")
    public ServerMessage sendDemo(ClientMessage message,@PathVariable String id) {
        logger.info("接收到了信息" + message.getName());
        return new ServerMessage(message.getName());
    }

    /**
     * 项目的订阅
     */
    @SubscribeMapping("/subscribe")
    public void project() {
        logger.info("用户订阅了我。。。");
    }

    //客户端只要订阅了/topic/subscribeTest主题，调用这个方法即可
    @RequestMapping("test")
    public void templateTest() {
        messagingTemplate.convertAndSend("/topic/subscribeTest", new ServerMessage("服务器主动推的数据"));
    }
}
