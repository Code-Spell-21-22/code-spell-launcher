package pt.ua.deti.codespell.codespelllauncher.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSender {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQHandler rabbitMQHandler;

    @Autowired
    public RabbitMQSender(RabbitTemplate rabbitTemplate, RabbitMQHandler rabbitMQHandler) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQHandler = rabbitMQHandler;
    }

    public void sendMessage(String topic, String message) {
        rabbitTemplate.convertAndSend(rabbitMQHandler.getTopicExchangeName(), rabbitMQHandler.getRoutingKeyWithTopic(topic), message);
    }

}
