package broker;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String HOST = "localhost";
    public static final String EXCHANGE_NAME = "hospital_topic";
    public static final String EXCHANGE_TYPE = "topic";

    public static final String FILA_CARDIACO    = "fila.cardiaco";
    public static final String FILA_PRESSAO     = "fila.pressao";
    public static final String FILA_SATURACAO   = "fila.saturacao";
    public static final String FILA_TEMPERATURA = "fila.temperatura";
    public static final int MAX_PRIORIDADE      = 10;

    @Bean
    public TopicExchange hospitalExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Declarables filasSinaisVitais(TopicExchange hospitalExchange) {
        Queue cardiaco    = QueueBuilder.durable(FILA_CARDIACO).maxPriority(MAX_PRIORIDADE).build();
        Queue pressao     = QueueBuilder.durable(FILA_PRESSAO).maxPriority(MAX_PRIORIDADE).build();
        Queue saturacao   = QueueBuilder.durable(FILA_SATURACAO).maxPriority(MAX_PRIORIDADE).build();
        Queue temperatura = QueueBuilder.durable(FILA_TEMPERATURA).maxPriority(MAX_PRIORIDADE).build();

        // Routing key esperada: <paciente>.<tipo>.<prioridade>
        return new Declarables(
            cardiaco, pressao, saturacao, temperatura,
            BindingBuilder.bind(cardiaco).to(hospitalExchange).with("*.bpm.#"),
            BindingBuilder.bind(pressao).to(hospitalExchange).with("*.pressao.#"),
            BindingBuilder.bind(saturacao).to(hospitalExchange).with("*.spo2.#"),
            BindingBuilder.bind(temperatura).to(hospitalExchange).with("*.temperatura.#")
        );
    }
}
