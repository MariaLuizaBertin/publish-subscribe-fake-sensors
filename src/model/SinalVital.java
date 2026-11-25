package model;

public class SinalVital {
	private final String pacienteId;
	private final String tipo;
	private final double valor;
	private final String descricaoLeitura;
	private final String prioridade;
	private final String descricaoClassificacao;

	public SinalVital(String pacienteId, String tipo, double valor, String prioridade) {
		this(pacienteId, tipo, valor, String.valueOf(valor), prioridade, "");
	}

	public SinalVital(String pacienteId, String tipo, double valor, String descricaoLeitura, String prioridade, String descricaoClassificacao) {
		this.pacienteId = pacienteId;
		this.tipo = tipo;
		this.valor = valor;
		this.descricaoLeitura = descricaoLeitura;
		this.prioridade = prioridade;
		this.descricaoClassificacao = descricaoClassificacao;
	}

	public String getPacienteId() {
		return pacienteId;
	}

	public String getTipo() {
		return tipo;
	}

	public double getValor() {
		return valor;
	}

	public String getDescricaoLeitura() {
		return descricaoLeitura;
	}

	public String getPrioridade() {
		return prioridade;
	}

	public String getDescricaoClassificacao() {
		return descricaoClassificacao;
	}
}
