# 🏥 Hospital IoT — Simulador de Sensores com Publish/Subscribe

Sistema de monitoramento hospitalar simulado que utiliza o padrão **Publish/Subscribe** com **RabbitMQ** para transmitir sinais vitais de pacientes em tempo real, combinando múltiplos padrões de projeto.

---

## 📋 Visão Geral

Sensores simulados publicam leituras de sinais vitais (batimentos cardíacos, pressão arterial, saturação de oxigênio e temperatura) em um **Topic Exchange** do RabbitMQ. Diferentes tipos de painéis se inscrevem nas filas e recebem os dados de acordo com seus filtros de interesse (paciente, tipo de sinal, prioridade).

```
[Sensores Simulados]
  Cardiaco / Pressao / Saturacao / Temperatura
        │
        │  routing key: <paciente>.<tipo>.<prioridade>
        ▼
 [RabbitMQ - Topic Exchange: hospital_topic]
        │
   ┌────┴────────────────────────┐
   │                             │
[fila.cardiaco]           [fila.pressao]
[fila.saturacao]          [fila.temperatura]
        │
        ▼
 [SinalVitalMediator]  ◄── Observer / Mediator
        │
   ┌────┴───────────────────────────┐
   │              │                 │
[PainelMedico] [PainelEnfermagem] [PainelMovel]
 (por paciente)  (todos os sinais) (só críticos)
```

---

## 🏗️ Padrões de Projeto Utilizados

| Padrão | Onde é aplicado |
|---|---|
| **Publish/Subscribe** | RabbitMQ Topic Exchange — sensores publicam, painéis consomem |
| **Observer** | `SinalVitalMediator` notifica todos os `Subscriber` registrados |
| **Mediator** | `SinalVitalMediator` centraliza o roteamento das mensagens recebidas |
| **Template Method** | Classe abstrata `Sensor` define o ciclo `medir → classificar → publicar` |
| **Strategy** | `ClassificadorSinais` delega a classificação para uma `RegraClassificacao` por tipo |
| **Factory** | `SensorFactory` e `PainelFactory` criam os objetos concretos |

---

## 📡 Tipos de Sensores e Classificação

| Sensor | Routing Key | Fila | Classificação |
|---|---|---|---|
| Cardíaco | `<paciente>.bpm.<prioridade>` | `fila.cardiaco` | `< 60` ou `> 140` → **crítico** / `> 100` → **atenção** |
| Pressão | `<paciente>.pressao.<prioridade>` | `fila.pressao` | Sistólica `> 180` → **crítico** / `> 140` → **atenção** |
| Saturação (SpO₂) | `<paciente>.spo2.<prioridade>` | `fila.saturacao` | `< 90%` → **crítico** / `< 95%` → **atenção** |
| Temperatura | `<paciente>.temperatura.<prioridade>` | `fila.temperatura` | `> 39.5°C` → **crítico** / `> 37.5°C` → **atenção** |

---

## 👁️ Tipos de Painéis (Subscribers)

| Painel | Filtro | Histórico |
|---|---|---|
| **PainelMedico** | Apenas um paciente específico | ✅ Mantém histórico completo |
| **PainelEnfermagem** | Todos os pacientes, todos os sinais | ❌ Apenas último sinal por paciente |
| **PainelMovel** | Apenas sinais **críticos** | ✅ Mantém histórico completo |

---

## 🛠️ Tecnologias

- **Java 21**
- **Spring Boot 4.1.2**
- **Spring AMQP** (RabbitMQ)
- **Spring Web** (REST API)
- **RabbitMQ 3** (via Docker)
- **Maven**

---

## 🚀 Como Executar

### Pré-requisitos
- Java 21+
- Docker e Docker Compose

### 1. Subir o RabbitMQ

```bash
docker compose up -d
```

Aguarde o RabbitMQ estar completamente pronto (cerca de 10 segundos). Você pode verificar:

```bash
docker logs -f rabbitmq | grep "Server startup complete"
```

> O painel de gerenciamento fica disponível em [http://localhost:15672](http://localhost:15672)  
> Login: `guest` / Senha: `guest`

### 2. Executar a aplicação

```bash
./mvnw spring-boot:run
```

> **Windows:** use `mvnw.cmd spring-boot:run`

A aplicação tentará se conectar ao RabbitMQ automaticamente com **retry** (até 10 tentativas com backoff exponencial), então não é necessário aguardar o RabbitMQ estar 100% pronto antes de rodar.

---

## 🌐 API REST

Após iniciar a aplicação, os seguintes endpoints ficam disponíveis em `http://localhost:8080`:

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/pacientes` | Lista os pacientes monitorados |
| `GET` | `/api/enfermagem` | Últimos sinais de todos os pacientes |
| `GET` | `/api/medico?paciente={id}` | Sinais e histórico de um paciente específico |
| `GET` | `/api/movel` | Apenas sinais críticos (todos os pacientes) |

### Exemplos

```bash
# Listar pacientes
curl http://localhost:8080/api/pacientes

# Painel de enfermagem (todos os sinais atuais)
curl http://localhost:8080/api/enfermagem

# Painel médico do paciente1
curl "http://localhost:8080/api/medico?paciente=paciente1"

# Painel móvel (apenas críticos)
curl http://localhost:8080/api/movel
```

---

## ⚙️ Configuração

Edite o arquivo [`resources/application.properties`](resources/application.properties):

```properties
# Conexão com o RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# Lista de pacientes monitorados (separados por vírgula)
monitoramento.pacientes=paciente1,paciente2,paciente3,paciente4,paciente5

# Intervalo entre ciclos de leitura (ms)
monitoramento.intervalo-ms=3000
```

---

## 📁 Estrutura do Projeto

```
src/
├── broker/
│   └── RabbitMQConfig.java       # Configuração do Exchange e Filas
├── classificador/
│   ├── ClassificadorSinais.java  # Strategy: seleciona a regra por tipo
│   ├── RegraCardiaco.java
│   ├── RegraPressao.java
│   ├── RegraSaturacao.java
│   └── RegraTemperatura.java
├── core/
│   ├── Main.java                 # Entry point Spring Boot
│   └── controller/
│       └── PainelController.java # REST API
├── mediator/
│   └── SinalVitalMediator.java   # Observer + Mediator
├── model/
│   ├── SinalVital.java
│   └── LeituraPainel.java
├── publisher/
│   ├── Sensor.java               # Template Method (abstrato)
│   ├── Cardiaco.java
│   ├── Pressao.java
│   ├── Saturacao.java
│   ├── Temperatura.java
│   ├── Publicador.java           # Publica no RabbitMQ via RabbitTemplate
│   ├── SensorFactory.java
│   └── SimuladorDispositivos.java
└── subscriber/
    ├── Subscriber.java           # Interface Observer
    ├── PainelMedico.java
    ├── PainelEnfermagem.java
    ├── PainelMovel.java
    └── PainelFactory.java
```

---

## 🐳 Docker Compose

O arquivo [`docker-compose.yml`](docker-compose.yml) sobe o RabbitMQ com:
- Reinício automático (`restart: unless-stopped`)
- Usuário/senha padrão `guest`/`guest`
- Healthcheck interno para garantir que o broker está pronto antes de aceitar conexões
