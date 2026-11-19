package mediator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import broker.RabbitMQConfig;
import subscriber.Subscriber;

// Observer e Mediator
@Service
public class SinalVitalMediator {

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
		factory.setHost(RabbitMQConfig.HOST);

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

			System.out.println("SinalVitalMediator conectado manualmente ao RabbitMQ.");
		} catch (IOException | TimeoutException e) {
			System.err.println("Erro ao conectar SinalVitalMediator ao RabbitMQ: " + e.getMessage());
		}
	}

	public void aoReceberMensagem(String routingKey, String mensagem) {
		String[] partes = routingKey.split("\\.");
		if (partes.length < 3) {
			return;
		}

		String paciente = partes[0];
		String tipo = partes[1];
		String prioridade = partes[2];

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

			sub.receber(routingKey, paciente, tipo, prioridade, mensagem);
		}
	}
}
