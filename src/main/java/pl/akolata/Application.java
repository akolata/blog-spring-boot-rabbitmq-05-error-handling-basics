package pl.akolata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.akolata.model.SimpleMessage;

import static pl.akolata.config.RabbitMqConfig.*;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class Application implements CommandLineRunner {
    private final RabbitTemplate rabbitTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public void runInfiniteLoopProducer() {
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_INFINITE_LOOP, new SimpleMessage());
    }

    @RabbitListener(queues = QUEUE_INFINITE_LOOP)
    public void infiniteLoopListener(SimpleMessage msg) {
        log.info("Queue [{}] received message: [{}]", QUEUE_INFINITE_LOOP, msg);
        throw new RuntimeException("Ops, this is an infinite loop!");
    }

    public void runDlxDlqProducer() {
        rabbitTemplate.convertAndSend(EXCHANGE_DLX_EXAMPLE, DL_ROUTING_KEY_ORIGINAL, new SimpleMessage());
    }

    @RabbitListener(queues = QUEUE_DLQ_EXAMPLE)
    public void dlxDlqQueueListenerOriginal(SimpleMessage msg) {
        log.info("Queue [{}] received message: [{}]", QUEUE_DLQ_EXAMPLE, msg);
        throw new AmqpRejectAndDontRequeueException("Ops, an error! Message should go to DLX and DLQ");
    }

    @RabbitListener(queues = DL_QUEUE_DLQ_EXAMPLE)
    public void dlxDlqQueueListenerDL(SimpleMessage msg) {
        log.info("Queue [{}] received message: [{}]", DL_QUEUE_DLQ_EXAMPLE, msg);
    }

    @Override
    public void run(String... args) {
//        runInfiniteLoopProducer();
        runDlxDlqProducer();
    }

}
