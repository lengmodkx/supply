package com.art1001.supply.listener;

import com.art1001.supply.common.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName RedisGetMsgListenerConfig
 * @Author 邓凯欣 lengmodkx@163.com
 * @Date 2020/12/25 15:50
 * @Discription
 */
@Component
public class RedisGetMsgListenerConfig {
    /**
     * 初始化监听器
     * @param connectionFactory
     * @param listenerAdapter
     * @return
     */
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // new PatternTopic("这里是监听的通道的名字") 通道要和发布者发布消息的通道一致
        container.addMessageListener(listenerAdapter, new PatternTopic(Constants.GET_MESSAGE_CHANNEL));
        return container;
    }

    /**
     * 绑定消息监听者和接收监听的方法
     * @param Redis2
     * @return
     */
    @Bean
    MessageListenerAdapter listenerAdapter(Redis2 redis2) {
        // redisReceiver 消息接收者
        // receiveMessage 消息接收后的方法
        return new MessageListenerAdapter(redis2, "receiveMessage");
    }


    @Bean
    StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    /**
     * 注册订阅者
     * @param latch
     * @return
     */
    @Bean
    RedisReceiver receiver(CountDownLatch latch) {
        return new RedisReceiver(latch);
    }

    /**
     * 计数器，用来控制线程
     * @return
     */
    @Bean
    CountDownLatch latch() {
        return new CountDownLatch(1); //指定了计数的次数 1
    }
}
