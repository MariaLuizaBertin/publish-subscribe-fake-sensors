package subscriber;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import broker.RabbitMQConfig;

public abstract class PainelHospitalar {

    protected abstract String getTopicoAssinatura();
    protected abstract void processarMensagem(String routingKey, String mensagem);
    protected abstract String getNomePainel();

    // Template method: conecta ao RabbitMQ, cria fila temporaria e consome
    public final void iniciar() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RabbitMQConfig.HOST);

        try {
            final Connection connection = factory.newConnection();
            final Channel channel = connection.createChannel();

            // Declara a exchange de topicos e cria a fila temporaria exclusiva deste painel
            channel.exchangeDeclare(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.EXCHANGE_TYPE);
            String queueName = channel.queueDeclare().getQueue();
            
            // Faz o bind no topico especifico que cada subclasse definiu
            channel.queueBind(queueName, RabbitMQConfig.EXCHANGE_NAME, getTopicoAssinatura());

            System.out.println("==================================================");
            System.out.println(" " + getNomePainel());
            System.out.println(" Binding: " + getTopicoAssinatura());
            System.out.println(" Aguardando mensagens... (CTRL+C para sair)");
            System.out.println("==================================================\n");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String mensagem = new String(delivery.getBody(), "UTF-8");
                String routingKey = delivery.getEnvelope().getRoutingKey();
                processarMensagem(routingKey, mensagem);
            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });

        } catch (Exception e) {
            System.err.println("Erro na conexao com RabbitMQ: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
