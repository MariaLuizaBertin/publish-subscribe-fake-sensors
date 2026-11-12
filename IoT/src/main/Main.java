package main;

import sensors.FacadeSensors;

public class Main {

    public static void main(String[] args) {
        // Paciente padrao se nao for passado argumento
        String idPaciente = "paciente1";

        if (args.length >= 1) {
            idPaciente = args[0];
        }

        System.out.println("Iniciando monitoramento para: " + idPaciente);
        FacadeSensors monitor = new FacadeSensors();
        monitor.checkup(idPaciente);
    }
}
