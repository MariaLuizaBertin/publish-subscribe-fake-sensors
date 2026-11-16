package publisher;

import java.util.List;
import org.springframework.stereotype.Component;
import classificador.ClassificadorSinais;

@Component
public class SensorFactory {

    public Sensor criar(String tipo) {
        return switch (tipo.toUpperCase()) {
            case "CARDIACO" -> new Cardiaco();
            case "PRESSÃO", "PRESSAO" -> new Pressao();
            case "SATURAÇÃO", "SATURACAO" -> new Saturacao();
            case "TEMPERATURA" -> new Temperatura();
            default -> throw new IllegalArgumentException("Tipo de sensor desconhecido: " + tipo);
        };
    }

    public Sensor criar(String tipo, String pacienteId, ClassificadorSinais classificador, Publicador publicador) {
        Sensor sensor = criar(tipo);
        sensor.configurar(pacienteId, classificador, publicador);
        return sensor;
    }

    public List<Sensor> criarTodos(String pacienteId, ClassificadorSinais classificador, Publicador publicador) {
        return List.of(
                criar("CARDIACO", pacienteId, classificador, publicador),
                criar("PRESSÃO", pacienteId, classificador, publicador),
                criar("SATURAÇÃO", pacienteId, classificador, publicador),
                criar("TEMPERATURA", pacienteId, classificador, publicador)
        );
    }
}
