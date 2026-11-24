package websocket;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
 *   /topic/criticos        → apenas leituras críticas e aviso pontual de resolução de alerta ativo
 *   /topic/paciente/{id}   → leituras filtradas por paciente
 */
@Component
public class WebSocketBroadcaster implements Subscriber {

    private final SimpMessagingTemplate messagingTemplate;
    private final Set<String> criticosAtivos = ConcurrentHashMap.newKeySet();

    public WebSocketBroadcaster(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void receber(String routingKey, String paciente, String tipo, String prioridade, String mensagem, String condicao) {
        LeituraPainel leitura = new LeituraPainel(paciente, tipo, prioridade, mensagem, condicao);

        // broadcast geral
        messagingTemplate.convertAndSend("/topic/sinais", leitura);

        // broadcast por paciente
        messagingTemplate.convertAndSend("/topic/paciente/" + paciente, leitura);

        // broadcast só de críticos (ou resolução pontual quando um alerta ativo estabiliza)
        String chave = paciente + ":" + tipo;
        if ("critico".equalsIgnoreCase(prioridade)) {
            criticosAtivos.add(chave);
            messagingTemplate.convertAndSend("/topic/criticos", leitura);
        } else if (criticosAtivos.remove(chave)) {
            messagingTemplate.convertAndSend("/topic/criticos", new AlertaResolvido(true, paciente, tipo));
        }
    }

    public record AlertaResolvido(boolean resolvido, String paciente, String tipo) {
    }

    @Override
    public Map<String, Map<String, LeituraPainel>> getUltimosSinais() {
        return Map.of(); // broadcaster não armazena estado
    }
}
