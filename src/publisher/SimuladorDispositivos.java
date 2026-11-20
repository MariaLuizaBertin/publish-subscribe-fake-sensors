package publisher;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import classificador.ClassificadorSinais;

@Component
public class SimuladorDispositivos {
	private final List<Sensor> sensores = new ArrayList<>();
	
	// @Value preenche automaticamente a lista de pacientes
	public SimuladorDispositivos(SensorFactory factory, Publicador publicador, @Value("${monitoramento.pacientes:paciente1}") List<String> pacientes) {
		ClassificadorSinais classificador = new ClassificadorSinais();
		for (String pacienteId : pacientes) {
			String id = pacienteId.trim();
			if (!id.isEmpty()) {
				sensores.addAll(factory.criarTodos(id, classificador, publicador));
			}
		}
		System.out.println(sensores.size() + " sensores ativos para: " + pacientes);
	}
	
	
	@Scheduled(initialDelay = 2000, fixedRateString = "${monitoramento.intervalo-ms:3000}")
	public void executarCiclo() {
		for (Sensor s : sensores) {
			try {
				s.executarCiclo();
			} catch (RuntimeException e) {
				System.out.println("Erro no sensor " + s.getTipoSinal() + ": " + e.getMessage());
			}
		}
	}
}
