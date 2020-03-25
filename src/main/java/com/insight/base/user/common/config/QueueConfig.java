package com.insight.base.user.common.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 宣炳刚
 * @date 2019-09-03
 * @remark Topic交换机配置
 */
@Configuration
public class QueueConfig {
    private final static int QUEUE_EXPIRATION = 1000 * 300;

    /**
     * exchange name
     */
    private final static String TOPIC_EXCHANGE_NAME = "amq.topic";

    /**
     * process queue
     */
    private final static String PROCESS_QUEUE_NAME = "insight.user";

    /**
     * dlx exchange name
     */
    public final static String DELAY_EXCHANGE_NAME = "amq.direct";

    /**
     * delay queue
     */
    public final static String DELAY_QUEUE_NAME = "dlx.insight.user";

    /**
     * dlx exchange
     *
     * @return DirectExchange
     */
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE_NAME);
    }

    /**
     * exchange
     *
     * @return DirectExchange
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    /**
     * delay queue
     *
     * @return Queue
     */
    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable(DELAY_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", TOPIC_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", PROCESS_QUEUE_NAME)
                .withArgument("x-message-ttl", QUEUE_EXPIRATION)
                .build();
    }

    /**
     * process queue
     *
     * @return Queue
     */
    @Bean
    public Queue processQueue() {
        return QueueBuilder.durable(PROCESS_QUEUE_NAME).build();
    }

    /**
     * 将延时交换机绑定到延时队列
     *
     * @param delayQueue    Queue
     * @param delayExchange DirectExchange
     * @return Binding
     */
    @Bean
    public Binding dlxBinding(Queue delayQueue, DirectExchange delayExchange) {
        return BindingBuilder.bind(delayQueue).to(delayExchange).with(DELAY_QUEUE_NAME);
    }

    /**
     * 将交换机绑定到实际消费队列
     *
     * @param processQueue  Queue
     * @param topicExchange TopicExchange
     * @return Binding
     */
    @Bean
    public Binding defaultBinding(Queue processQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(processQueue).to(topicExchange).with(PROCESS_QUEUE_NAME);
    }
}
