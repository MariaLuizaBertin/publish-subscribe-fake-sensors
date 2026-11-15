package sensors;

public class Temperatura implements Sensor {
    private double temp;
    private String status;

    @Override
    public double medir() {
        int categoria = (int) Math.floor(Math.random() * 6) + 1;

        if (categoria == 1) { 
            // Hipotermia (Temperatura Baixa / Crítica)
            temp = (Math.random() * 5.0) + 30.0;
        } 
        else if (categoria == 2 || categoria == 3 || categoria == 4) { 
            // Temperatura Normal
            temp = (Math.random() * 2.3) + 35.0;
        } 
        else if (categoria == 5) {
            // Estado Febril / Febrícula
            temp = (Math.random() * 0.5) + 37.3;
        }
        else { 
            // Febre / Febre Alta (Crítica)
            temp = (Math.random() * 4.3) + 37.8;
        }
        
        temp = Math.round(temp * 10.0) / 10.0;
        return temp;
    }

    @Override
    public String getTipoSinal() {
        return "temperatura";
    }

    @Override
    public String classificar() {
        if (temp >= 37.8) {
            this.status = "FEBRE / HIPERTERMIA (Alerta)";
            return "critico";
        } 
        else if (temp >= 37.3) {
            this.status = "ESTADO FEBRIL (Atenção)";
            return "atencao";
        }
        else if (temp < 35.0) {
            this.status = "HIPOTERMIA (Alerta Crítico)";
            return "critico";
        } 
        else {
            this.status = "Temperatura NORMAL";
            return "rotina";
        }
    }

    public void analiseTemperatura() {
        classificar();
        System.out.println(temp + " °C");
        System.out.println("Status: " + status);
    }

    @Override
    public String getDescricao() {
        return temp + " °C - " + status;
    }

    public double getTemperatura() {
        return this.temp;
    }

    public String getStatus() {
        return this.status;
    }
}
