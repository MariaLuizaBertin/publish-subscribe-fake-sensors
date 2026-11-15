package classificador;

public class RegraCardiaco implements RegraClassificacao {

    @Override
    public String tipo() {
        return "bpm";
    }

    @Override
    public Classificacao classificar(double bpm) {
        if (bpm > 140) {
            return Classificacao.critico("TAQUICARDIA SEVERA (Alerta Crítico)");
        } else if (bpm > 100) {
            return Classificacao.atencao("TAQUICARDIA (Batimento Elevado)");
        } else if (bpm < 60) {
            return Classificacao.critico("BRADICARDIA (Batimento Baixo)");
        } else {
            return Classificacao.rotina("Batimento NORMAL");
        }
    }
}
