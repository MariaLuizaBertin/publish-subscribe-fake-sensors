package sensors;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import broker.RabbitMQConfig;

public class FacadeSensors {
    private Sensor[] sensores;

    public FacadeSensors() {
        this.sensores = new Sensor[] {
                new Cardiaco(),
                new Pressao(),
                new Saturacao(),
                new Temperatura()
        };
    }

    // Inicia o loop continuo de medicoes e envios ao broker
    public void checkup(String idPaciente) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RabbitMQConfig.HOST);

        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {

            channel.exchangeDeclare(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.EXCHANGE_TYPE);

            System.out.println("==================================================");
            System.out.println(" Monitor iniciado para: " + idPaciente.toUpperCase());
            System.out.println(" Exchange: " + RabbitMQConfig.EXCHANGE_NAME + " (topic)");
            System.out.println("==================================================\n");

            while (true) {
                for (Sensor sensor : sensores) {
                    publicarLeitura(channel, idPaciente, sensor);
                }
                System.out.println("--------------------------------------------------");
                Thread.sleep(5000);
            }

        } catch (InterruptedException e) {
            System.out.println("Monitor interrompido.");
        } catch (Exception e) {
            System.err.println("Erro na conexao com RabbitMQ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void publicarLeitura(Channel channel, String idPaciente, Sensor sensor) throws Exception {
        sensor.medir();
        String prioridade = sensor.classificar();
        
        // Routing key segue o padrao: paciente.sinal.prioridade
        String routingKey = idPaciente + "." + sensor.getTipoSinal() + "." + prioridade;
        String mensagem = sensor.getDescricao() + " | Prioridade: " + prioridade;

        channel.basicPublish(RabbitMQConfig.EXCHANGE_NAME, routingKey, null, mensagem.getBytes("UTF-8"));
        System.out.println(" [Pub] " + routingKey + " -> " + mensagem);
    }
}
