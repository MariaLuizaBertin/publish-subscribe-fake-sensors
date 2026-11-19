package subscriber;

import org.springframework.stereotype.Component;

@Component
public class PainelFactory {

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
            case "MOVEL", "MÓVEL" -> new PainelMovel();
            default -> throw new IllegalArgumentException("Tipo de painel desconhecido: " + tipo);
        };
    }
}
