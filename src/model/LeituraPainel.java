package model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public record LeituraPainel(String paciente, String tipo, String prioridade, String mensagem, String horario) {
	private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

	public LeituraPainel(String paciente, String tipo, String prioridade, String mensagem) {
		this(paciente, tipo, prioridade, mensagem, LocalTime.now().format(FORMATO_HORA));
	}
}
