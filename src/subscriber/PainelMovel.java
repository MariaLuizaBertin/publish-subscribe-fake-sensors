package subscriber;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import model.LeituraPainel;

public class PainelMovel implements Subscriber {
	private static final String PRIORIDADE_CRITICA = "critico";
	private final Map<String, Map<String, LeituraPainel>> ultimosSinais = new ConcurrentHashMap<>();
	private final List<LeituraPainel> historico = new CopyOnWriteArrayList<>();

	@Override
	public void receber(String routingKey, String paciente, String tipo, String prioridade, String mensagem) {
		LeituraPainel leitura = new LeituraPainel(paciente, tipo, prioridade, mensagem);
		ultimosSinais
			.computeIfAbsent(paciente, k -> new ConcurrentHashMap<>())
			.put(tipo, leitura);
		historico.add(leitura);
	}

	@Override
	public void removerSinalAtual(String paciente, String tipo) {
		ultimosSinais.computeIfPresent(paciente, (p, sinais) -> {
			sinais.remove(tipo);
			return sinais.isEmpty() ? null : sinais;
		});
	}

	@Override
	public Map<String, Map<String, LeituraPainel>> getUltimosSinais() {
		return this.ultimosSinais;
	}

	@Override
	public List<LeituraPainel> getHistorico() {
		return List.copyOf(this.historico);
	}

	@Override
	public String getPrioridadeFiltro() {
		return PRIORIDADE_CRITICA;
	}
}
