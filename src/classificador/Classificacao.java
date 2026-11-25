package classificador;

public record Classificacao(String prioridade, String descricao) {

    public static Classificacao rotina() {
        return new Classificacao("rotina", "Normal");
    }

    public static Classificacao rotina(String descricao) {
        return new Classificacao("rotina", descricao);
    }

    public static Classificacao atencao(String descricao) {
        return new Classificacao("atencao", descricao);
    }

    public static Classificacao critico(String descricao) {
        return new Classificacao("critico", descricao);
    }
}
