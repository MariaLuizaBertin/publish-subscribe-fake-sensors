package subscriber;

import java.util.List;
import java.util.Map;
import model.LeituraPainel;

public interface Subscriber {
	default void receber(String routingKey, String paciente, String tipo, String prioridade, String mensagem) {
		receber(routingKey, paciente, tipo, prioridade, mensagem, "");
	}

	public void receber(String routingKey, String paciente, String tipo, String prioridade, String mensagem, String condicao);
	
	public Map<String, Map<String, LeituraPainel>> getUltimosSinais();

	default void removerSinalAtual(String paciente, String tipo) {
	}

	default List<LeituraPainel> getHistorico() {
		return List.of();
	}

	default String getPacienteFiltro() {
		return null;
	}

	default String getPrioridadeFiltro() {
		return null;
	}
}
