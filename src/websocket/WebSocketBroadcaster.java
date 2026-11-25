package websocket;

import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import model.LeituraPainel;
import subscriber.Subscriber;

/**
 * Subscriber do padrão Observer que, ao receber um sinal vital do mediator,
 * faz broadcast via WebSocket/STOMP para todos os clientes conectados.
 *
 * Tópicos publicados:
 *   /topic/sinais          → toda leitura (qualquer paciente, qualquer prioridade)
 *   /topic/criticos        → apenas leituras com prioridade "critico"
 *   /topic/paciente/{id}   → leituras filtradas por paciente
 */
@Component
public class WebSocketBroadcaster implements Subscriber {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void receber(String routingKey, String paciente, String tipo, String prioridade, String mensagem) {
        LeituraPainel leitura = new LeituraPainel(paciente, tipo, prioridade, mensagem);

        // broadcast geral
        messagingTemplate.convertAndSend("/topic/sinais", leitura);

        // broadcast por paciente
        messagingTemplate.convertAndSend("/topic/paciente/" + paciente, leitura);

        // broadcast só de críticos
        if ("critico".equalsIgnoreCase(prioridade)) {
            messagingTemplate.convertAndSend("/topic/criticos", leitura);
        }
    }

    @Override
    public Map<String, Map<String, LeituraPainel>> getUltimosSinais() {
        return Map.of(); // broadcaster não armazena estado
    }
}
