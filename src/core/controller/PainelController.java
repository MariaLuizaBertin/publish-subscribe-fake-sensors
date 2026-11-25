package core.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mediator.SinalVitalMediator;
import model.LeituraPainel;
import subscriber.PainelFactory;
import subscriber.Subscriber;

@RestController
@RequestMapping("/api")
public class PainelController {
	private final PainelFactory painelFactory;
	private final SinalVitalMediator mediator;
	private final List<String> pacientes;

	private final Subscriber enfermagem;
	private final Subscriber movel;
	private final Map<String, Subscriber> paineisMedicos = new ConcurrentHashMap<>();

	public PainelController(PainelFactory painelFactory, SinalVitalMediator mediator, @Value("${monitoramento.pacientes:paciente1}") List<String> pacientes) {
		this.painelFactory = painelFactory;
		this.mediator = mediator;
		this.pacientes = pacientes.stream().map(String::trim).filter(p -> !p.isEmpty()).toList();

		this.enfermagem = painelFactory.criarPainel("ENFERMAGEM");
		this.movel = painelFactory.criarPainel("MOVEL");
		this.mediator.registrar(this.enfermagem);
		this.mediator.registrar(this.movel);

		for (String pacienteId : this.pacientes) {
			Subscriber painelMedico = painelFactory.criarPainel("MEDICO", pacienteId);
			this.paineisMedicos.put(pacienteId, painelMedico);
			this.mediator.registrar(painelMedico);
		}
	}

	@GetMapping("/pacientes")
	public List<String> listarPacientes() {
		return pacientes;
	}

	@GetMapping("/enfermagem")
	public Map<String, Map<String, LeituraPainel>> enfermagem() {
		return enfermagem.getUltimosSinais();
	}

	@GetMapping("/medico")
	public Map<String, Object> medico(@RequestParam String paciente) {
		String pacienteId = paciente.trim();
		Subscriber painelMedico = paineisMedicos.computeIfAbsent(pacienteId, id -> {
			Subscriber novoPainel = painelFactory.criarPainel("MEDICO", id);
			mediator.registrar(novoPainel);
			return novoPainel;
		});
		return Map.of(
			"paciente", pacienteId,
			"ultimosSinais", painelMedico.getUltimosSinais().getOrDefault(pacienteId, Map.of()),
			"historico", painelMedico.getHistorico()
		);
	}

	@GetMapping("/movel")
	public Map<String, Object> movel() {
		return Map.of(
			"atuais", movel.getUltimosSinais(),
			"historico", movel.getHistorico()
		);
	}
}
