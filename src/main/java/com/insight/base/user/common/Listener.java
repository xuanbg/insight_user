package com.insight.base.user.common;

import com.insight.util.pojo.User;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author 宣炳刚
 * @date 2019-09-03
 * @remark
 */
@Component
public class Listener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Core core;

    /**
     * 构造方法
     *
     * @param core Core
     */
    public Listener(Core core) {
        this.core = core;
    }

    /**
     * 从队列订阅新增用户消息
     *
     * @param user 队列消息
     */
    @RabbitHandler
    @RabbitListener(queues = "insight.user")
    public void receiveUser(User user, Channel channel, Message message) {
        try {
            core.addUser(user, null);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception ex) {
            logger.error("发生异常: {}", ex.getMessage());
        }
    }
}