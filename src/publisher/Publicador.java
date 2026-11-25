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
        return "{\"leitura\":\"" + escaparJson(sinal.getDescricaoLeitura())
                + "\",\"condicao\":\"" + escaparJson(sinal.getDescricaoClassificacao()) + "\"}";
    }

    private String escaparJson(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public int calcularPrioridadeAmqp(String prioridade) {
        if (prioridade == null) {
            return 1;
        }
        return switch (prioridade.toLowerCase()) {
            case "critico" -> 10;
            case "atencao" -> 5;
            default -> 1;
        };
    }

    public void publicar(SinalVital sinal) {
        String routingKey = montarRoutingKey(sinal);
        String mensagem = montarMensagem(sinal);
        int prioridadeAmqp = calcularPrioridadeAmqp(sinal.getPrioridade());
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, mensagem, msg -> {
                msg.getMessageProperties().setPriority(prioridadeAmqp);
                return msg;
            });
            System.out.println("Enviado '" + routingKey + "' (prio=" + prioridadeAmqp + "): " + mensagem);
        } catch (AmqpException e) {
            System.err.println("Falha ao publicar '" + routingKey + "': " + e.getMessage());
        }
    }
}
