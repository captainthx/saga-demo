# Debezium Outbox Pattern & Saga Choreography บน Spring Boot และ Docker

## Setup
- run docker compose
- สร้าง connector Debezium Connector Configuration โดยการส่ง JSON นี้ไปที่ http://localhost:8083/connectors/

```json
{
  "name": "order-outbox-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgres-order",
    "database.port": "5432",
    "database.user": "user",
    "database.password": "password",
    "database.dbname": "orderdb",
    "database.server.name": "orderdb",
    "plugin.name": "pgoutput",
    "topic.prefix": "orderdb",
    "table.include.list": "public.outbox_event",
    "transforms": "outbox",
    "transforms.outbox.type": "io.debezium.transforms.outbox.EventRouter",
    "transforms.outbox.topic.regex": "^outbox\\.event\\.(.*)$",
    "transforms.outbox.topic.replacement": "$1"
  }
}
```
- test send request 
    ```bash
    curl POST localhost:8082/v1/orders -H "Content-Type: application/json" -d '{"productName":"coffee","amount":2}'
    ```

