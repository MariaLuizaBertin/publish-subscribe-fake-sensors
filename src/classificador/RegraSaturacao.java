package classificador;

public class RegraSaturacao implements RegraClassificacao {

    @Override
    public String tipo() {
        return "spo2";
    }

    @Override
    public Classificacao classificar(double saturacao) {
        if (saturacao >= 95) {
            return Classificacao.rotina("SATURAÇÃO NORMAL");
        } else if (saturacao >= 90) {
            return Classificacao.atencao("HIPÓXIA LEVE (Atenção)");
        } else if (saturacao >= 81) {
            return Classificacao.critico("HIPÓXIA MODERADA (Alerta Crítico)");
        } else {
            return Classificacao.critico("HIPÓXIA SEVERA (Alerta Crítico)");
        }
    }
}
