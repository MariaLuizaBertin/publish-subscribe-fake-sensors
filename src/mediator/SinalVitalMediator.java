package mediator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import broker.RabbitMQConfig;
import subscriber.Subscriber;


// Observer e Mediator
@Service
public class SinalVitalMediator {
	@Value("${spring.rabbitmq.host:localhost}")
	private String host;

	@Value("${spring.rabbitmq.port:5672}")
	private int port;

	@Value("${spring.rabbitmq.username:guest}")
	private String username;

	@Value("${spring.rabbitmq.password:guest}")
	private String password;

	private static List<String> FILAS_SINAIS = List.of(
			RabbitMQConfig.FILA_CARDIACO,
			RabbitMQConfig.FILA_PRESSAO,
			RabbitMQConfig.FILA_SATURACAO,
			RabbitMQConfig.FILA_TEMPERATURA
	);

	private final List<Subscriber> subscribers = new CopyOnWriteArrayList<>();

	private Connection connection;
	private Channel channel;

	public SinalVitalMediator(List<Subscriber> subscribers) {
		this.subscribers.addAll(subscribers);
	}

	public void registrar(Subscriber subscriber) {
		subscribers.add(subscriber);
	}

	public void remover(Subscriber subscriber) {
		subscribers.remove(subscriber);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void iniciarConexao() {
		ConnectionFactory factory = new ConnectionFactory();
    	factory.setHost(host);
    	factory.setPort(port);
    	factory.setUsername(username);
    	factory.setPassword(password);

		int maxTentativas = 10;
		int tentativa = 0;
		int espera = 2000; // ms

		while (tentativa < maxTentativas) {
			tentativa++;
			try {
				connection = factory.newConnection();
				channel = connection.createChannel();

				DeliverCallback deliverCallback = (consumerTag, delivery) -> {
					String mensagem = new String(delivery.getBody(), StandardCharsets.UTF_8);
					String routingKey = delivery.getEnvelope().getRoutingKey();
					aoReceberMensagem(routingKey, mensagem);
				};

				for (String fila : FILAS_SINAIS) {
					channel.basicConsume(fila, true, deliverCallback, consumerTag -> { });
				}

				System.out.println("SinalVitalMediator conectado ao RabbitMQ (tentativa " + tentativa + ").");
				return; // sucesso, sai do loop

			} catch (IOException | TimeoutException e) {
				System.err.println("[" + tentativa + "/" + maxTentativas + "] RabbitMQ ainda não disponível: " + e.getMessage());
				if (tentativa < maxTentativas) {
					try {
						Thread.sleep(espera);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						return;
					}
					espera = Math.min(espera * 2, 15000); // backoff exponencial, máx 15s
				}
			}
		}

		System.err.println("Não foi possível conectar ao RabbitMQ após " + maxTentativas + " tentativas.");
	}

	public void aoReceberMensagem(String routingKey, String mensagem) {
		String[] partes = routingKey.split("\\.");
		if (partes.length < 3) {
			return;
		}

		String paciente = partes[0];
		String tipo = partes[1];
		String prioridade = partes[2];

		String leitura = extrairCampoJson(mensagem, "leitura");
		String condicao = extrairCampoJson(mensagem, "condicao");
		if (leitura == null) {
			// Compatibilidade com mensagens em texto puro
			leitura = mensagem;
			condicao = "";
		}

		for (Subscriber sub : subscribers) {
			String pacienteFiltro = sub.getPacienteFiltro();
			if (pacienteFiltro != null && !pacienteFiltro.equalsIgnoreCase(paciente)) {
				continue;
			}

			String prioridadeFiltro = sub.getPrioridadeFiltro();
			if (prioridadeFiltro != null && !prioridadeFiltro.equalsIgnoreCase(prioridade)) {
				sub.removerSinalAtual(paciente, tipo);
				continue;
			}

			sub.receber(routingKey, paciente, tipo, prioridade, leitura, condicao);
		}
	}

	private String extrairCampoJson(String json, String campo) {
		if (json == null || !json.trim().startsWith("{")) {
			return null;
		}
		String chave = "\"" + campo + "\"";
		int idxChave = json.indexOf(chave);
		if (idxChave < 0) {
			return null;
		}
		int idxDoisPontos = json.indexOf(':', idxChave + chave.length());
		if (idxDoisPontos < 0) {
			return null;
		}
		int idxInicio = json.indexOf('"', idxDoisPontos + 1);
		if (idxInicio < 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean escapando = false;
		for (int i = idxInicio + 1; i < json.length(); i++) {
			char c = json.charAt(i);
			if (escapando) {
				sb.append(c);
				escapando = false;
			} else if (c == '\\') {
				escapando = true;
			} else if (c == '"') {
				return sb.toString();
			} else {
				sb.append(c);
			}
		}
		return null;
	}
}
