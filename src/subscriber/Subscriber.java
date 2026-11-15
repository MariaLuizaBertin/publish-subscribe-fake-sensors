package subscriber;

public class Subscriber {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Uso: Subscriber <tipo> [idPaciente]");
            System.err.println("  Tipos: enfermagem, medico, emergencia");
            System.err.println("  Exemplos:");
            System.err.println("    Subscriber enfermagem");
            System.err.println("    Subscriber medico paciente1");
            System.err.println("    Subscriber emergencia");
            System.err.println("    Subscriber emergencia paciente2");
            System.exit(1);
        }

        String tipo = args[0];
        // Segundo argumento opcional: ID do paciente
        String paciente = (args.length >= 2) ? args[1] : null;

        // Factory instancia o painel solicitado
        PainelHospitalar painel = PainelFactory.criarPainel(tipo, paciente);

        if (painel != null) {
            painel.iniciar();
        }
    }
}
