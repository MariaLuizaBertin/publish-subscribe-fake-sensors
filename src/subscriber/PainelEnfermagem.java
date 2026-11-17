package subscriber;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import model.LeituraPainel;

@Component
public class PainelEnfermagem implements Subscriber {
	private final Map<String, Map<String, LeituraPainel>> ultimosSinais = new ConcurrentHashMap<>();
	
	@Override
    public void receber(String routingKey, String paciente, String tipo, String prioridade, String mensagem) {
        ultimosSinais
            .computeIfAbsent(paciente, k -> new ConcurrentHashMap<>())
            .put(tipo, new LeituraPainel(tipo, prioridade, mensagem));
    }

	@Override
	public Map<String, Map<String, LeituraPainel>> getUltimosSinais() {
		return this.ultimosSinais;
	}
}
