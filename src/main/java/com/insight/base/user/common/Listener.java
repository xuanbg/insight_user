package com.insight.base.user.common;

import com.insight.util.pojo.User;
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
    public void receiveUser(User user) {
        core.addUser(user, null);
    }
}