package classificador;

public interface RegraClassificacao {
    String tipo();

    Classificacao classificar(double valor);
}
