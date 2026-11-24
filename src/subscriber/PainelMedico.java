package subscriber;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import model.LeituraPainel;

public class PainelMedico implements Subscriber {
	private final String paciente;
	private final Map<String, Map<String, LeituraPainel>> ultimosSinais = new ConcurrentHashMap<>();
	private final List<LeituraPainel> historico = new CopyOnWriteArrayList<>();

	public PainelMedico(String paciente) {
		if (paciente == null || paciente.isBlank()) {
			throw new IllegalArgumentException("PainelMedico exige um paciente válido.");
		}
		this.paciente = paciente.trim();
	}

	@Override
	public void receber(String routingKey, String paciente, String tipo, String prioridade, String mensagem, String condicao) {
		LeituraPainel leitura = new LeituraPainel(paciente, tipo, prioridade, mensagem, condicao);
		ultimosSinais
			.computeIfAbsent(paciente, k -> new ConcurrentHashMap<>())
			.put(tipo, leitura);
		historico.add(leitura);
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
	public String getPacienteFiltro() {
		return this.paciente;
	}
}
