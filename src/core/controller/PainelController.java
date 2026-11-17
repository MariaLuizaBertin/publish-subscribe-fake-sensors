package core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import model.LeituraPainel;
import subscriber.PainelEnfermagem;

@RestController
@RequestMapping("/api")
public class PainelController {
	private final PainelEnfermagem enfermagem;

	public PainelController(PainelEnfermagem enfermagem) {
		this.enfermagem = enfermagem;
	}

	@GetMapping("/enfermagem")
	public Map<String, Map<String, LeituraPainel>> enfermagem(){
		return enfermagem.getUltimosSinais();
	}
	
	
}
