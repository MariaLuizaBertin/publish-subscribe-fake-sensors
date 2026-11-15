package publisher;

import classificador.Classificacao;
import classificador.ClassificadorSinais;
import model.SinalVital;

// Template Method (ler -> classificar -> montar mensagem -> publicar)
public abstract class Sensor {
	private ClassificadorSinais classificador;
	private Publicador publicador;
	private String pacienteId;

	public Sensor() {
		this("paciente1", new ClassificadorSinais(), new Publicador());
	}

	public Sensor(String pacienteId, ClassificadorSinais classificador, Publicador publicador) {
		this.pacienteId = pacienteId;
		this.classificador = classificador;
		this.publicador = publicador;
	}

	public void configurar(String pacienteId, ClassificadorSinais classificador, Publicador publicador) {
		this.pacienteId = pacienteId;
		this.classificador = classificador;
		this.publicador = publicador;
	}

	public final void executarCiclo() {
		double leitura = medir();
		String tipoSinal = getTipoSinal();
		Classificacao c = classificador.classificar(tipoSinal, leitura);
		SinalVital sinal = new SinalVital(pacienteId, tipoSinal, leitura, getDescricao(), c.prioridade(),
				c.descricao());
		String mensagem = publicador.montarMensagem(sinal);
		publicador.publicar();
	}

	public abstract double medir();

	public abstract String getTipoSinal();

	public abstract String getDescricao();
}
