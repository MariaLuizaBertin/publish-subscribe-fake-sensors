# 🏥 Hospital IoT — Simulador de Sensores com Publish/Subscribe

Sistema de monitoramento hospitalar em tempo real que utiliza o padrão arquitetural **Publish/Subscribe** com **RabbitMQ** (*Topic Exchange* + *Priority Queues*) e **WebSocket/STOMP** para transmitir sinais vitais e alertas clínicos de pacientes simulados, combinando múltiplos padrões de projeto.

---

## 📋 Visão Geral

Sensores simulados coletam medições de sinais vitais (frequência cardíaca, pressão arterial, saturação de oxigênio e temperatura corporal), classificam a gravidade clínica (`rotina`, `atencao` ou `critico`) e publicam mensagens em JSON no **Topic Exchange** durável `hospital_topic` do RabbitMQ, com prioridade AMQP diferenciada.

O `SinalVitalMediator` consome as quatro *Priority Queues* com `basicQos(1)` e confirmação manual (`basicAck`/`basicNack`), aplicando os filtros de cada painel (`PainelEnfermagem`, `PainelMedico`, `PainelMovel`) e repassando os eventos ao `WebSocketBroadcaster` para atualização em tempo real no navegador.

```
[Sensores Simulados]
  Cardiaco / Pressao / Saturacao / Temperatura
        │
        │  routing key: <paciente>.<tipo>.<prioridade>
        │  payload JSON: {"leitura":"...","condicao":"..."}
        │  prioridade AMQP: critico=10 | atencao=5 | rotina=1
        ▼
 [RabbitMQ — Topic Exchange durável: hospital_topic]
        │
   ┌────┴────────────────────────┐
   │  Priority Queues (max=10)   │
[fila.cardiaco]           [fila.pressao]
[fila.saturacao]          [fila.temperatura]
        │  basicQos(1) + manual ack
        ▼
 [SinalVitalMediator]  ◄── Mediator / Observer
        │
   ┌────┴──────────────┬──────────────────┬───────────────────────┐
   ▼                   ▼                  ▼                       ▼
[PainelEnfermagem] [PainelMedico]   [PainelMovel]      [WebSocketBroadcaster]
 (todos os sinais) (por paciente)   (só críticos)          │  STOMP (/ws)
                                                           ├─► /topic/sinais
                                                           ├─► /topic/paciente/{id}
                                                           └─► /topic/criticos
```

---

## 🏗️ Padrões de Software Utilizados

| Padrão | Categoria | Onde é aplicado |
|---|---|---|
| **Publish/Subscribe** | Arquitetural | RabbitMQ (`hospital_topic` + *Priority Queues*) e WebSocket/STOMP (`/topic/*`) |
| **Template Method** | Comportamental | Classe abstrata `Sensor` define o ciclo fixo `medir → classificar → publicar` |
| **Strategy** | Comportamental | `ClassificadorSinais` delega a classificação clínica para `RegraClassificacao` |
| **Mediator** | Comportamental | `SinalVitalMediator` centraliza consumo AMQP, parsing JSON e filtragem por painel |
| **Observer** | Comportamental | `SinalVitalMediator` notifica todos os `Subscriber` registrados (painéis e `WebSocketBroadcaster`) |
| **Factory** | Criacional | `SensorFactory` e `PainelFactory` centralizam a criação e configuração dos objetos |
| **Builder** | Criacional (API) | `QueueBuilder` do Spring AMQP em `RabbitMQConfig` para declarar filas duráveis com `x-max-priority` |

---

## 📡 Tipos de Sensores, Classificação e Prioridades

| Sensor | Routing Key | Fila (`x-max-priority=10`) | Classificação |
|---|---|---|---|
| Cardíaco | `<paciente>.bpm.<prioridade>` | `fila.cardiaco` (`*.bpm.#`) | `< 60` ou `> 140` → **crítico** / `101–140` → **atenção** |
| Pressão | `<paciente>.pressao.<prioridade>` | `fila.pressao` (`*.pressao.#`) | Sistólica `< 90` ou `≥ 140` → **crítico** / `121–139` → **atenção** |
| Saturação (SpO₂) | `<paciente>.spo2.<prioridade>` | `fila.saturacao` (`*.spo2.#`) | `< 90%` → **crítico** / `90%–94%` → **atenção** |
| Temperatura | `<paciente>.temperatura.<prioridade>` | `fila.temperatura` (`*.temperatura.#`) | `< 35.0°C` ou `≥ 37.8°C` → **crítico** / `37.3°C–37.7°C` → **atenção** |

**Mapeamento de prioridade AMQP:**
- `critico` → prioridade **10**
- `atencao` → prioridade **5**
- `rotina` → prioridade **1**

**Exemplo de payload publicado no RabbitMQ:**
```json
{"leitura":"165.0 BPM","condicao":"TAQUICARDIA SEVERA (Alerta Crítico)"}
```

---

## 👁️ Painéis (Assinantes) e Interfaces Web

| Painel | Interface Web | Tópico WebSocket | Filtro | Histórico |
|---|---|---|---|---|
| **PainelEnfermagem** | `/index.html` | `/topic/sinais` | Todos os pacientes e sinais | ❌ Apenas último sinal por paciente |
| **PainelMedico** | `/medico.html` | `/topic/paciente/{id}` | Apenas o paciente selecionado | ✅ Histórico detalhado com condição clínica |
| **PainelMovel** | `/movel.html` | `/topic/criticos` | Apenas sinais **críticos** (+ aviso pontual `AlertaResolvido`) | ✅ Histórico separado por paciente (limite configurável FIFO) |

---

## 🛠️ Tecnologias

- **Java 21**
- **Spring Boot 4.1.2**
- **Spring AMQP** (RabbitMQ)
- **Spring Web** (API REST e arquivos estáticos)
- **Spring WebSocket** (STOMP + SockJS)
- **RabbitMQ 3** (`rabbitmq:3-management` via Docker Compose)
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

> Caso tenha executado uma versão anterior do projeto com filas sem `x-max-priority` ou exchange não durável, recrie o contêiner com:
> ```bash
> docker compose down && docker compose up -d
> ```

O painel de gerenciamento do RabbitMQ fica disponível em [http://localhost:15672](http://localhost:15672) (login `guest` / senha `guest`).

### 2. Executar a aplicação

```bash
./mvnw spring-boot:run
```

> **Windows:** você também pode usar `mvnw.cmd spring-boot:run` ou os scripts auxiliares [`compilar.bat`](compilar.bat) e [`rodar.bat`](rodar.bat).

A aplicação tenta se conectar ao RabbitMQ automaticamente com **retry** (até 10 tentativas com *backoff* exponencial).

### 3. Acessar os painéis web

- **Painel Enfermagem:** [http://localhost:8080/index.html](http://localhost:8080/index.html)
- **Painel Médico:** [http://localhost:8080/medico.html](http://localhost:8080/medico.html)
- **Painel Móvel:** [http://localhost:8080/movel.html](http://localhost:8080/movel.html)

---

## 🌐 API REST e WebSocket

A API REST fornece a carga inicial de estado para as telas em `http://localhost:8080`:

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/pacientes` | Lista os pacientes monitorados |
| `GET` | `/api/enfermagem` | Últimos sinais de todos os pacientes |
| `GET` | `/api/medico?paciente={id}` | Sinais atuais e histórico de um paciente específico |
| `GET` | `/api/movel` | Alertas críticos ativos, histórico agrupado por paciente e limite configurado |
| `GET` | `/api/movel/historico?paciente={id}` | Histórico de alertas críticos (de um paciente ou de todos) |

**WebSocket / STOMP** (endpoint `/ws` com fallback SockJS):

| Tópico | Conteúdo | Consumidor |
|---|---|---|
| `/topic/sinais` | Todas as leituras (`LeituraPainel`) | Painel Enfermagem |
| `/topic/paciente/{id}` | Leituras filtradas de um paciente específico | Painel Médico |
| `/topic/criticos` | Apenas leituras críticas e eventos `{"resolvido":true,"paciente":"...","tipo":"..."}` | Painel Móvel |

---

## ⚙️ Configuração

Edite [`resources/application.properties`](resources/application.properties):

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

# Limite de alertas críticos mantidos por paciente no Painel Móvel
monitoramento.movel.historico-limite=50
```

---

## 📁 Estrutura do Projeto

```
docs/
├── publish-subscribe.asta          # Diagramas UML (Astah)
├── padroes_de_projeto.tex          # Documentação dos padrões de software (LaTeX)
└── descricao_arquitetural.tex      # Descrição arquitetural (LaTeX)
resources/
├── application.properties
└── static/
    ├── index.html / index.css      # Painel Enfermagem
    ├── medico.html / medico.css    # Painel Médico
    ├── movel.html / movel.css      # Painel Móvel
    └── padrao.css
src/
├── broker/
│   └── RabbitMQConfig.java         # TopicExchange durável e 4 Priority Queues
├── classificador/
│   ├── Classificacao.java
│   ├── ClassificadorSinais.java    # Strategy: seleciona a regra por tipo
│   ├── RegraClassificacao.java
│   ├── RegraCardiaco.java
│   ├── RegraPressao.java
│   ├── RegraSaturacao.java
│   └── RegraTemperatura.java
├── core/
│   ├── Main.java                   # Entry point Spring Boot
│   └── controller/
│       └── PainelController.java   # API REST (carga inicial)
├── mediator/
│   └── SinalVitalMediator.java     # Mediator + Observer (basicQos=1 + manual ack)
├── model/
│   ├── SinalVital.java
│   └── LeituraPainel.java
├── publisher/
│   ├── Sensor.java                 # Template Method (abstrato)
│   ├── Cardiaco.java
│   ├── Pressao.java
│   ├── Saturacao.java
│   ├── Temperatura.java
│   ├── Publicador.java             # Publica JSON + prioridade AMQP via RabbitTemplate
│   ├── SensorFactory.java
│   └── SimuladorDispositivos.java
├── subscriber/
│   ├── Subscriber.java             # Interface Observer
│   ├── PainelEnfermagem.java
│   ├── PainelMedico.java
│   ├── PainelMovel.java            # Histórico crítico limitado por paciente
│   └── PainelFactory.java
└── websocket/
    ├── WebSocketConfig.java        # Configuração STOMP / SockJS (/ws)
    └── WebSocketBroadcaster.java   # Broadcast por tópicos (/topic/*)
```

---

## 🐳 Docker Compose

O arquivo [`docker-compose.yml`](docker-compose.yml) sobe o RabbitMQ com:
- Imagem `rabbitmq:3-management` (portas `5672` AMQP e `15672` Management UI)
- Reinício automático (`restart: unless-stopped`)
- Usuário/senha padrão `guest`/`guest`
- Healthcheck interno (`rabbitmq-diagnostics ping`)
