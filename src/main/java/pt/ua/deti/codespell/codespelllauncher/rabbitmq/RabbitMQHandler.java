package pt.ua.deti.codespell.codespelllauncher.rabbitmq;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Getter
public class RabbitMQHandler {

    private final String topicExchangeName = "code-spell-launcher-exchange";

    private final String codeReceiverQueue = "code-spell-launcher-receiver-queue";
    private final String codeExecutionResultsQueue = "code-spell-launcher-execution-results-queue";
    private final String codeAnalysisResultsQueue = "code-spell-launcher-analysis-results-queue";

    private final String routingKey = "code_spell.launcher.#";

    @Bean
    Queue receiverQueue() {
        return new Queue(codeReceiverQueue, false);
    }

    @Bean
    Queue executionResultsQueue() {
        return new Queue(codeExecutionResultsQueue, false);
    }

    @Bean
    Queue analysisResultsQueue() {
        return new Queue(codeAnalysisResultsQueue, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding bindingReceiverQueue(Queue receiverQueue, TopicExchange exchange) {
        return BindingBuilder.bind(receiverQueue).to(exchange).with(getRoutingKeyWithTopic("receiver"));
    }

    @Bean
    Binding bindingExecutionResultsQueue(Queue executionResultsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(executionResultsQueue).to(exchange).with(getRoutingKeyWithTopic("execution.#"));
    }

    @Bean
    Binding bindingAnalysisResultsQueue(Queue analysisResultsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(analysisResultsQueue).to(exchange).with(getRoutingKeyWithTopic("analysis.#"));
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(codeReceiverQueue);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RabbitMQReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    public String getRoutingKeyWithTopic(String topic) {
        return routingKey.replace("#", topic);
    }

}
