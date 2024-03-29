package com.insight.base.user.common.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 宣炳刚
 * @date 2019-09-03
 * @remark Topic交换机配置
 */
@Configuration
public class QueueConfig {
    /**
     * dlx exchange name
     */
    public final static String DELAY_EXCHANGE = "amq.direct";
    /**
     * delay queue
     */
    public final static String DELAY_USER_QUEUE = "dlx.insight.user";
    private final static int QUEUE_EXPIRATION = 1000 * 300;
    /**
     * exchange name
     */
    private final static String TOPIC_EXCHANGE = "amq.topic";
    /**
     * process queue
     */
    private final static String PROCESS_USER_QUEUE = "insight.user";

    /**
     * user dlx exchange
     *
     * @return DirectExchange
     */
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE);
    }

    /**
     * user exchange
     *
     * @return DirectExchange
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    /**
     * delay queue
     *
     * @return Queue
     */
    @Bean
    public Queue delayUserQueue() {
        return QueueBuilder.durable(DELAY_USER_QUEUE)
                .withArgument("x-dead-letter-exchange", TOPIC_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", PROCESS_USER_QUEUE)
                .withArgument("x-message-ttl", QUEUE_EXPIRATION)
                .build();
    }

    /**
     * process queue
     *
     * @return Queue
     */
    @Bean
    public Queue processUserQueue() {
        return QueueBuilder.durable(PROCESS_USER_QUEUE).build();
    }
}
