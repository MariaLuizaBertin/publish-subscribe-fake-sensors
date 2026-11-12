package sensors;

public class Cardiaco implements Sensor {
    private double bpm;
    private String status;

    @Override
    public double medir() {
        int categoria = (int) Math.floor(Math.random() * 6) + 1;

        if (categoria == 1) { 
            // Bradicardia (Batimento Baixo / Crítico)
            bpm = Math.floor(Math.random() * 20) + 40;   // Gera de 40 a 59 BPM
        } 
        else if (categoria == 2 || categoria == 3 || categoria == 4) { 
            // Batimento Normal
            bpm = Math.floor(Math.random() * 41) + 60;   // Gera de 60 a 100 BPM
        } 
        else if (categoria == 5) {
            // Taquicardia Leve / Ritmo Acelerado
            bpm = Math.floor(Math.random() * 40) + 101;  // Gera de 101 a 140 BPM
        }
        else { 
            // Taquicardia Severa (Batimento Muito Alto / Crítico)
            bpm = Math.floor(Math.random() * 61) + 141;  // Gera de 141 a 201 BPM
        }
        return bpm;
    }

    @Override
    public String getTipoSinal() {
        return "bpm";
    }

    @Override
    public String classificar() {
        if (bpm > 140) {
            this.status = "TAQUICARDIA SEVERA (Alerta Crítico)";
            return "critico";
        } 
        else if (bpm > 100) {
            this.status = "TAQUICARDIA (Batimento Elevado)";
            return "atencao";
        }
        else if (bpm < 60) {
            this.status = "BRADICARDIA (Batimento Baixo)";
            return "critico";
        } 
        else {
            this.status = "Batimento NORMAL";
            return "rotina";
        }
    }

    public void analiseBpm() {
        classificar();
        System.out.println(bpm + " BPM");
        System.out.println("Status: " + status);
    }

    @Override
    public String getDescricao() {
        return bpm + " BPM - " + status;
    }

    public double getBpm() {
        return this.bpm;
    }

    public String getStatus() {
        return this.status;
    }
}
