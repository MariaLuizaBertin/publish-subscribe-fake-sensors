package classificador;

public class RegraPressao implements RegraClassificacao {

    @Override
    public String tipo() {
        return "pressao";
    }

    @Override
    public Classificacao classificar(double sistolica) {
        if (sistolica >= 140) {
            return Classificacao.critico("HIPERTENSÃO");
        } else if (sistolica < 90) {
            return Classificacao.critico("Pressão BAIXA");
        } else if (sistolica > 120) {
            return Classificacao.atencao("ELEVADA / PRÉ-HIPERTENSÃO");
        } else {
            return Classificacao.rotina("Pressão NORMAL");
        }
    }
}
