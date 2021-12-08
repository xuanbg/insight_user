package com.insight.base.user.common;

import com.insight.base.user.common.config.QueueConfig;
import com.insight.base.user.common.dto.UserDto;
import com.insight.utils.Json;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
     * @param channel Channel
     * @param message Message
     * @throws IOException IOException
     */
    @RabbitHandler
    @RabbitListener(queues = "insight.user")
    public void receiveUser(Channel channel, Message message) throws IOException {
        try {
            String body = new String(message.getBody());
            core.addUser(Json.toBean(body, UserDto.class));
        } catch (Exception ex) {
            logger.error("发生异常: {}", ex.getMessage());
            channel.basicPublish(QueueConfig.DELAY_EXCHANGE_NAME, QueueConfig.DELAY_QUEUE_NAME, null, message.getBody());
        }finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}