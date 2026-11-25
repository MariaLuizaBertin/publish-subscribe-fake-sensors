package classificador;

public class RegraTemperatura implements RegraClassificacao {

    @Override
    public String tipo() {
        return "temperatura";
    }

    @Override
    public Classificacao classificar(double celsius) {
        if (celsius >= 37.8) {
            return Classificacao.critico("FEBRE / HIPERTERMIA (Alerta)");
        } else if (celsius >= 37.3) {
            return Classificacao.atencao("ESTADO FEBRIL (Atenção)");
        } else if (celsius < 35.0) {
            return Classificacao.critico("HIPOTERMIA (Alerta Crítico)");
        } else {
            return Classificacao.rotina("Temperatura NORMAL");
        }
    }
}
