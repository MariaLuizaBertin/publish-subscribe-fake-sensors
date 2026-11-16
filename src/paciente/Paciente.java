package paciente;

import java.util.List;
import broker.RabbitMQConfig;
import classificador.ClassificadorSinais;
import publisher.Publicador;
import publisher.Sensor;
import publisher.SensorFactory;

public class Paciente {
    private final String nome;
    private final List<Sensor> sensores;
    private final Publicador publicador;

    public Paciente(String nome, Publicador publicador) {
        this.nome = nome;
        this.publicador = publicador;
        ClassificadorSinais classificador = new ClassificadorSinais();
        SensorFactory factory = new SensorFactory();
        this.sensores = factory.criarTodos(nome, classificador, publicador);
    }

    public String getNome() {
        return nome;
    }
}
