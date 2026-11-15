package subscriber;

public class AppEmergencia extends PainelHospitalar {

    private String idPaciente;

    public AppEmergencia(String idPaciente) {
        this.idPaciente = idPaciente;
    }

    @Override
    protected String getTopicoAssinatura() {
        if (idPaciente != null && !idPaciente.isEmpty()) {
            // Apenas alertas criticos de um paciente especifico
            return idPaciente + ".*.critico";
        }
        // Se nao passar paciente, monitora os criticos de TODOS os leitos
        return "*.*.critico";
    }

    @Override
    protected void processarMensagem(String routingKey, String mensagem) {
        System.out.println(" [EMERGENCIA] " + routingKey + " -> " + mensagem);
    }

    @Override
    protected String getNomePainel() {
        if (idPaciente != null && !idPaciente.isEmpty()) {
            return "APP EMERGENCIA (Paciente: " + idPaciente + " | Somente CRITICO)";
        }
        return "APP EMERGENCIA (Todos os pacientes | Somente CRITICO)";
    }
}
