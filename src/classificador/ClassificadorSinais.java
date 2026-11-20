package classificador;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

// Strategy
public class ClassificadorSinais {
	private final Map<String, RegraClassificacao> regras = new HashMap<>();

	public ClassificadorSinais() {
		this(List.of(new RegraCardiaco(), new RegraPressao(), new RegraSaturacao(), new RegraTemperatura()));
	}

	public ClassificadorSinais(List<RegraClassificacao> regrasDisponiveis) {
		regrasDisponiveis.forEach(r -> regras.put(r.tipo(), r));
	}

	public Classificacao classificar(String tipo, double valor) {
		RegraClassificacao regra = regras.get(tipo);
		if (regra == null) {
			throw new IllegalArgumentException("Nenhuma regra de classificação para " + tipo);
		}
		return regra.classificar(valor);
	}
}
