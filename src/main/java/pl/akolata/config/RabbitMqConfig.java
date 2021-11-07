package pl.akolata.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMqConfig {
    // infinite loop
    public static final String EXCHANGE = "x.error-handling-demo";
    public static final String QUEUE_INFINITE_LOOP = "q.error-handling-demo.infinite-loop";
    public static final String ROUTING_KEY_INFINITE_LOOP = "infinite-loop";
    // DLQ and DLX
    public static final String QUEUE_DLQ_EXAMPLE = "q.error-handling-demo.dlx-dlq-example";
    public static final String DL_QUEUE_DLQ_EXAMPLE = "q.error-handling-demo.dlx-dlq-example.dlq";
    public static final String EXCHANGE_DLX_EXAMPLE = "x.error-handling-demo.dlx-dlq-example";
    public static final String DL_EXCHANGE_DLX_EXAMPLE = "x.error-handling-demo.dlx-dlq-example.dlx";
    public static final String DL_ROUTING_KEY_ORIGINAL = "dlx-before";
    public static final String DL_ROUTING_KEY_DLQ_OVERRIDDEN = "dlx-after";

    @Bean
    public Declarables infiniteLoopConfiguration() {
        Queue queueInfiniteLoopDemo = new Queue(QUEUE_INFINITE_LOOP, false);
        DirectExchange exchange = new DirectExchange(EXCHANGE, false, false);
        return new Declarables(
                exchange,
                queueInfiniteLoopDemo,
                BindingBuilder.bind(queueInfiniteLoopDemo).to(exchange).with(ROUTING_KEY_INFINITE_LOOP)
        );
    }

    @Bean
    public Declarables dlxAndDlqConfiguration() {
        Queue queueForDlqDemo = new Queue(QUEUE_DLQ_EXAMPLE, false, false, false, Map.of(
                "x-dead-letter-exchange", DL_EXCHANGE_DLX_EXAMPLE,
                "x-dead-letter-routing-key", DL_ROUTING_KEY_DLQ_OVERRIDDEN
        ));
        DirectExchange exchangeForDlxDemo = new DirectExchange(EXCHANGE_DLX_EXAMPLE, false, false);
        Queue deadLetterQueueForDqlDemo = new Queue(DL_QUEUE_DLQ_EXAMPLE, false);
        DirectExchange deadLetterExchangeForDlxDemo = new DirectExchange(DL_EXCHANGE_DLX_EXAMPLE, false, false);
        return new Declarables(
                exchangeForDlxDemo,
                queueForDlqDemo,
                BindingBuilder.bind(queueForDlqDemo).to(exchangeForDlxDemo).with(DL_ROUTING_KEY_ORIGINAL),
                deadLetterExchangeForDlxDemo,
                deadLetterQueueForDqlDemo,
                BindingBuilder.bind(deadLetterQueueForDqlDemo).to(deadLetterExchangeForDlxDemo).with(DL_ROUTING_KEY_DLQ_OVERRIDDEN)
        );
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper om) {
        return new Jackson2JsonMessageConverter(om);
    }

}
