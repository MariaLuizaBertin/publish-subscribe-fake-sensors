package paciente;

import sensors.FacadeSensors;

public class Paciente {
    private String nome;
    private FacadeSensors monitorSensores;

    public Paciente(String nome) {
        this.nome = nome;
        this.monitorSensores = new FacadeSensors();
    }

    public void analiseGeral() {
        // Dispara o monitoramento continuo dos sensores via RabbitMQ
        this.monitorSensores.checkup(this.nome);
    }

    public String getNome() {
        return nome;
    }
}
