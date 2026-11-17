package publisher;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import broker.RabbitMQConfig;
import model.SinalVital;

@Component
public class Publicador {
    private final RabbitTemplate rabbitTemplate;

    public Publicador(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public String montarRoutingKey(SinalVital sinal) {
        return sinal.getPacienteId() + "." + sinal.getTipo() + "." + sinal.getPrioridade();
    }

    public String montarMensagem(SinalVital sinal) {
        return sinal.getDescricaoLeitura();
    }

    public void publicar(SinalVital sinal) {
        String routingKey = montarRoutingKey(sinal);
        String mensagem = montarMensagem(sinal);
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, mensagem);
            System.out.println("Enviado '" + routingKey + "': " + mensagem);
        } catch (AmqpException e) {
            System.err.println("Falha ao publicar '" + routingKey + "': " + e.getMessage());
        }
    }
}
