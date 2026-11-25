package subscriber;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import model.LeituraPainel;

public class PainelEnfermagem implements Subscriber {
	private final Map<String, Map<String, LeituraPainel>> ultimosSinais = new ConcurrentHashMap<>();
	
	@Override
    public void receber(String routingKey, String paciente, String tipo, String prioridade, String mensagem, String condicao) {
        ultimosSinais
            .computeIfAbsent(paciente, k -> new ConcurrentHashMap<>())
            .put(tipo, new LeituraPainel(paciente, tipo, prioridade, mensagem, condicao));
    }

	@Override
	public Map<String, Map<String, LeituraPainel>> getUltimosSinais() {
		return this.ultimosSinais;
	}
}
