package subscriber;

public class PainelFactory {

    public static PainelHospitalar criarPainel(String tipo, String paciente) {
        switch (tipo.toLowerCase()) {
            case "enfermagem":
                return new PainelEnfermagem();

            case "medico":
                // Medico acompanha um paciente especifico (padrao: paciente1)
                if (paciente == null || paciente.isEmpty()) {
                    paciente = "paciente1";
                }
                return new PainelMedico(paciente);

            case "emergencia":
                // Se paciente for null, monitora alertas criticos de todos os leitos
                return new AppEmergencia(paciente);

            default:
                System.err.println("Tipo de painel desconhecido: " + tipo);
                System.err.println("Tipos validos: enfermagem, medico, emergencia");
                return null;
        }
    }
}
