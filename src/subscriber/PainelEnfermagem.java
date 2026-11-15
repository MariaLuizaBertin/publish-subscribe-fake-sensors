package subscriber;

public class PainelEnfermagem extends PainelHospitalar {

    @Override
    protected String getTopicoAssinatura() {
        // Recebe todas as medicoes de todos os leitos e sinais
        return "#";
    }

    @Override
    protected void processarMensagem(String routingKey, String mensagem) {
        System.out.println(" [Enfermagem] " + routingKey + " -> " + mensagem);
    }

    @Override
    protected String getNomePainel() {
        return "PAINEL DE ENFERMAGEM (Monitoramento Geral)";
    }
}
