package subscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PainelFactory {

    private final int limiteHistoricoMovel;

    public PainelFactory() {
        this(50);
    }

    @Autowired
    public PainelFactory(@Value("${monitoramento.movel.historico-limite:50}") int limiteHistoricoMovel) {
        this.limiteHistoricoMovel = limiteHistoricoMovel;
    }

    public Subscriber criarPainel(String tipo) {
        return criarPainel(tipo, null);
    }

    public Subscriber criarPainel(String tipo, String paciente) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de painel não pode ser nulo.");
        }

        return switch (tipo.toUpperCase()) {
            case "ENFERMAGEM" -> new PainelEnfermagem();
            case "MEDICO", "MÉDICO" -> new PainelMedico(paciente);
            case "MOVEL", "MÓVEL" -> new PainelMovel(limiteHistoricoMovel);
            default -> throw new IllegalArgumentException("Tipo de painel desconhecido: " + tipo);
        };
    }
}
