package sensors;

public class Saturacao implements Sensor {
    private double spo2;
    private String status;

    @Override
    public double medir() {
        int categoria = (int) Math.floor(Math.random() * 6) + 1;

        if (categoria == 1) {
            // Hipóxia Severa / Crítica
            spo2 = Math.floor(Math.random() * 21) + 60; // Gera de 60% a 80%
        } else if (categoria == 2 || categoria == 3 || categoria == 4) {
            // Saturação Normal / Ideal
            spo2 = Math.floor(Math.random() * 6) + 95; // Gera de 95% a 100%
        } else if (categoria == 5) {
            // Hipóxia Leve / Atenção
            spo2 = Math.floor(Math.random() * 4) + 91; // Gera de 91% a 94%
        } else {
            // Hipóxia Moderada
            spo2 = Math.floor(Math.random() * 10) + 81; // Gera de 81% a 90%
        }
        return spo2;
    }

    @Override
    public String getTipoSinal() {
        return "spo2";
    }

    @Override
    public String classificar() {
        if (spo2 >= 95) {
            this.status = "Saturação NORMAL";
            return "rotina";
        } 
        else if (spo2 >= 91) {
            this.status = "HIPÓXIA LEVE (Atenção)";
            return "atencao";
        }
        else if (spo2 >= 81) {
            this.status = "HIPÓXIA MODERADA (Alerta)";
            return "atencao";
        } 
        else {
            this.status = "HIPÓXIA SEVERA (Alerta Crítico)";
            return "critico";
        }
    }

    public void analiseSpo2() {
        classificar();
        System.out.println(spo2 + "% SpO2");
        System.out.println("Status: " + status);
    }

    @Override
    public String getDescricao() {
        return spo2 + "% SpO2 - " + status;
    }

    public double getSpo2() {
        return this.spo2;
    }

    public String getStatus() {
        return this.status;
    }
}
