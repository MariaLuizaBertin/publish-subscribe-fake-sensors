package subscriber;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import model.LeituraPainel;

/**
 * Painel Móvel — recebe apenas sinais CRÍTICOS e mantém histórico separado
 * por paciente com tamanho máximo configurável (escalabilidade).
 *
 * <p>Estrutura de histórico:
 * <pre>
 *   historicosPorPaciente → { pacienteId → Deque&lt;LeituraPainel&gt; (ordem cronológica, máx N) }
 * </pre>
 *
 * <p>O limite padrão é {@value #LIMITE_PADRAO} entradas por paciente e pode ser
 * sobrescrito via propriedade {@code monitoramento.movel.historico-limite} (injetada via {@link PainelFactory}).
 */
public class PainelMovel implements Subscriber {

	private static final String PRIORIDADE_CRITICA = "critico";
	private static final int LIMITE_PADRAO = 50;

	/** Último sinal crítico por paciente/tipo (para exibição do estado atual). */
	private final Map<String, Map<String, LeituraPainel>> ultimosSinais = new ConcurrentHashMap<>();

	/**
	 * Histórico de alertas críticos separado por paciente.
	 * Cada fila tem no máximo {@code limiteHistorico} entradas; ao atingir o
	 * limite a entrada mais antiga é descartada (política FIFO de expiração).
	 */
	private final Map<String, Deque<LeituraPainel>> historicosPorPaciente = new ConcurrentHashMap<>();

	private final int limiteHistorico;

	public PainelMovel() {
		this(LIMITE_PADRAO);
	}

	public PainelMovel(int limiteHistorico) {
		this.limiteHistorico = limiteHistorico > 0 ? limiteHistorico : LIMITE_PADRAO;
	}

	// -------------------------------------------------------------------------
	// Subscriber
	// -------------------------------------------------------------------------

	@Override
	public void receber(String routingKey, String paciente, String tipo, String prioridade, String mensagem, String condicao) {
		LeituraPainel leitura = new LeituraPainel(paciente, tipo, prioridade, mensagem, condicao);

		// Atualiza o sinal atual por paciente/tipo
		ultimosSinais
			.computeIfAbsent(paciente, k -> new ConcurrentHashMap<>())
			.put(tipo, leitura);

		// Adiciona ao histórico do paciente respeitando o limite
		adicionarAoHistorico(paciente, leitura);
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

	/** Retorna todo o histórico crítico agregado (em ordem cronológica por paciente). */
	@Override
	public List<LeituraPainel> getHistorico() {
		List<LeituraPainel> agregado = new ArrayList<>();
		historicosPorPaciente.values().forEach(fila -> {
			synchronized (fila) {
				agregado.addAll(fila);
			}
		});
		return Collections.unmodifiableList(agregado);
	}

	@Override
	public String getPrioridadeFiltro() {
		return PRIORIDADE_CRITICA;
	}

	// -------------------------------------------------------------------------
	// Acesso ao histórico separado por paciente
	// -------------------------------------------------------------------------

	/**
	 * Retorna o histórico crítico de um paciente específico
	 * (em ordem cronológica, do mais antigo ao mais recente).
	 */
	public List<LeituraPainel> getHistoricoPorPaciente(String pacienteId) {
		Deque<LeituraPainel> fila = historicosPorPaciente.get(pacienteId);
		if (fila == null) {
			return List.of();
		}
		synchronized (fila) {
			return List.copyOf(fila);
		}
	}

	/**
	 * Retorna um snapshot do histórico de todos os pacientes, agrupado por ID.
	 * A ordem dentro de cada lista é cronológica (do mais antigo ao mais recente).
	 */
	public Map<String, List<LeituraPainel>> getHistoricosPorPaciente() {
		Map<String, List<LeituraPainel>> snapshot = new LinkedHashMap<>();
		historicosPorPaciente.forEach((paciente, fila) -> {
			synchronized (fila) {
				snapshot.put(paciente, List.copyOf(fila));
			}
		});
		return Collections.unmodifiableMap(snapshot);
	}

	/** Retorna o limite máximo de entradas por paciente. */
	public int getLimiteHistorico() {
		return limiteHistorico;
	}

	// -------------------------------------------------------------------------
	// Interno
	// -------------------------------------------------------------------------

	private void adicionarAoHistorico(String paciente, LeituraPainel leitura) {
		Deque<LeituraPainel> fila = historicosPorPaciente.computeIfAbsent(
			paciente, k -> new ArrayDeque<>(limiteHistorico + 1)
		);
		synchronized (fila) {
			if (fila.size() >= limiteHistorico) {
				fila.pollFirst(); // descarta o mais antigo
			}
			fila.addLast(leitura); // insere o mais recente no final
		}
	}
}
