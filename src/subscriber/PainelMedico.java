package subscriber;

public class PainelMedico extends PainelHospitalar {

    private String idPaciente;

    public PainelMedico(String idPaciente) {
        this.idPaciente = idPaciente;
    }

    @Override
    protected String getTopicoAssinatura() {
        // Recebe todos os sinais vitais apenas deste paciente especifico
        return idPaciente + ".#";
    }

    @Override
    protected void processarMensagem(String routingKey, String mensagem) {
        System.out.println(" [Medico] " + routingKey + " -> " + mensagem);
    }

    @Override
    protected String getNomePainel() {
        return "PAINEL MEDICO (Paciente: " + idPaciente + ")";
    }
}
