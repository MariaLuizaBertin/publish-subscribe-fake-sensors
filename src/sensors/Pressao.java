package sensors;

public class Pressao implements Sensor {
    private double sistolica;
    private double diastolica;
    private String status;

    @Override
    public double medir() {
        int categoria = (int) Math.floor(Math.random() * 6) + 1;

        if (categoria == 1) {
            // Pressão baixa
            sistolica = Math.floor(Math.random() * 30) + 60; // Gera de 60 a 89
            diastolica = Math.floor(Math.random() * 20) + 40; // Gera de 40 a 59
        } else if (categoria == 2 || categoria == 3 || categoria == 4) {
            // Pressão normal
            sistolica = Math.floor(Math.random() * 31) + 90; // Gera de 90 a 120
            diastolica = Math.floor(Math.random() * 21) + 60; // Gera de 60 a 80
        } else if (categoria == 5) {
            // Pressão Elevada / Pré-Hipertensão
            sistolica = Math.floor(Math.random() * 19) + 121; // Gera de 121 a 139
            diastolica = Math.floor(Math.random() * 9) + 81; // Gera de 81 a 89
        } else {
            // Pressão Alta
            sistolica = Math.floor(Math.random() * 81) + 140; // Gera de 140 a 220
            diastolica = Math.floor(Math.random() * 61) + 90; // Gera de 90 a 150
        }
        return sistolica;
    }

    @Override
    public String getTipoSinal() {
        return "pressao";
    }

    @Override
    public String classificar() {
        if (sistolica >= 140 || diastolica >= 90) {
            this.status = "HIPERTENSÃO";
            return "critico";
        } else if (sistolica < 90 || diastolica < 60) {
            this.status = "Pressão BAIXA";
            return "critico";
        } else if (sistolica > 120 || diastolica > 80) {
            this.status = "ELEVADA / PRÉ-HIPERTENSÃO";
            return "atencao";
        } else {
            this.status = "Pressão NORMAL";
            return "rotina";
        }
    }

    public void analisePressao() {
        classificar();
        System.out.println(sistolica + " " + diastolica);
        System.out.println("Status: " + status);
    }

    @Override
    public String getDescricao() {
        return sistolica + "/" + diastolica + " mmHg - " + status;
    }

    public double getSistolica() {
        return this.sistolica;
    }

    public double getDiastolica() {
        return this.diastolica;
    }

    public String getStatus() {
        return this.status;
    }
}
