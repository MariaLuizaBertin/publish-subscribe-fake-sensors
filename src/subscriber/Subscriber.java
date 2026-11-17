package subscriber;

import java.util.Map;
import model.LeituraPainel;

public interface Subscriber {
	public void receber(String routinKey, String paciente, String tipo, String prioridade, String mensagem);
	
	public Map<String, Map<String, LeituraPainel>> getUltimosSinais();
}
