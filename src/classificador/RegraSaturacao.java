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
        } else if (saturacao >= 91) {
            return Classificacao.atencao("HIPÓXIA LEVE (Atenção)");
        } else if (saturacao >= 81) {
            return Classificacao.atencao("HIPÓXIA MODERADA (Alerta)");
        } else {
            return Classificacao.critico("HIPÓXIA SEVERA (Alerta Crítico)");
        }
    }
}
