# DeliverEats — Issues del Proyecto

> **Contexto:** DeliverEats es una plataforma de delivery de comida con 6 microservicios que se comunican de forma síncrona vía REST. Cada pedido tarda **60 segundos** en procesarse porque cada servicio espera la respuesta del siguiente antes de continuar. Tu misión es transformar esta arquitectura usando los patrones de Event-Driven Architecture (EDA) que aprendiste en el curso.

> **Cómo empezar:** Ejecuta `docker-compose up --build` y abre http://localhost:3000. Haz un pedido y observa el problema.

---

## Fase 1: Arquitectura Event-Driven

### Issue #1: El pedido tarda 60 segundos 🐌

**Contexto:**
Cuando un cliente hace un pedido, el Order Service llama al Kitchen Service por HTTP y espera su respuesta. El Kitchen Service llama al Payment Service y espera. Y así sucesivamente por 6 servicios. El resultado: el cliente espera **60 segundos** mirando un spinner antes de recibir confirmación.

En el mundo real, esto sería inaceptable. Amazon procesa millones de pedidos — ¿te imaginas si cada uno tardara un minuto?

**Cómo reproducir:**
1. Abre http://localhost:3000
2. Selecciona un restaurante y agrega items
3. Haz clic en "Pedir Ahora"
4. Observa cómo el spinner gira durante 60 segundos antes de recibir respuesta

**Criterios de aceptación:**
- [ ] `POST /api/orders` responde en menos de 1 segundo
- [ ] Los 6 servicios siguen completando su trabajo, pero de forma asíncrona
- [ ] El Order Service publica un evento `OrderCreatedEvent` al topic `orders.created`
- [ ] Cada servicio suscribe al topic del servicio anterior y publica su propio evento
- [ ] La cadena completa se ejecuta en ~15 segundos (el servicio más lento, no la suma)
- [ ] Los adapters HTTP (`HttpKitchenAdapter`, `HttpPaymentAdapter`, etc.) son reemplazados por adapters de EventBus

**Pistas:**
- 💡 ¿Qué patrón permite que un servicio publique un mensaje sin esperar respuesta? Revisa el patrón Pub/Sub y la interfaz `EventBus` en el `shared-kernel/`.
- 🔍 Mira los driven ports en cada servicio (ej: `KitchenPort`, `PaymentPort`). ¿Qué pasaría si en vez de hacer un HTTP POST, la implementación publicara un evento a Kafka?
- 🛠️ Para cada servicio: (1) Crea un subscriber en `infrastructure/adapter/in/event/` que escuche el topic de entrada, (2) Crea un adapter en `infrastructure/adapter/out/event/` que publique al topic de salida, (3) Cambia `BROKER=none` a `BROKER=kafka` en `docker-compose.yml`.

**Topics de Kafka:**
| Topic | Productor | Consumidor |
|-------|-----------|------------|
| `orders.created` | Order Service | Kitchen Service |
| `kitchen.confirmed` | Kitchen Service | Payment Service |
| `payment.completed` | Payment Service | Rider Service |
| `rider.assigned` | Rider Service | Notification Service |
| `notification.sent` | Notification Service | Tracking Service |
| `tracking.created` | Tracking Service | (fin de la cadena) |

**Módulos del curso relacionados:** Módulo 1 (EventBus, abstracción del broker), Módulo 3 (Kafka)

---

### Issue #2: Si un servicio falla, todo se pierde 💀

**Contexto:**
Detén el Payment Service (`docker-compose stop payment-service`). Ahora haz un pedido. La cocina confirma... pero el pago nunca se procesa. El pedido queda en limbo para siempre. No hay reintentos, no hay notificación de error, no hay nada.

En producción, esto significaría pedidos perdidos, clientes frustrados y dinero sin cobrar.

**Cómo reproducir:**
1. Resuelve el Issue #1 primero (los servicios deben comunicarse por eventos)
2. Ejecuta `docker-compose stop payment-service`
3. Haz un pedido desde http://localhost:3000
4. Observa que la cocina confirma pero el pedido nunca avanza de ahí
5. Ejecuta `docker-compose start payment-service`
6. El evento se perdió — el pedido sigue atascado

**Criterios de aceptación:**
- [ ] Cuando un servicio falla al procesar un evento, el evento se envía a un Dead Letter Queue (topic `{original}.dlq`)
- [ ] Los eventos fallidos se reintentan con backoff exponencial (3 intentos: 1s, 2s, 4s)
- [ ] Después de agotar reintentos, se publica un evento de fallo y se notifica al cliente
- [ ] Cuando el servicio caído se reinicia, los eventos pendientes en Kafka se procesan automáticamente
- [ ] El sistema no pierde pedidos bajo ninguna circunstancia

**Pistas:**
- 💡 Kafka ya retiene los mensajes. Si un consumer group no ha hecho commit del offset, al reiniciar el consumer recibirá los mensajes pendientes. ¿Estás haciendo commit manual o automático?
- 🔍 Revisa `KafkaEventBus` en el `shared-kernel/`. ¿Qué pasa cuando el handler lanza una excepción? ¿Se pierde el evento?
- 🛠️ Implementa retry con try/catch en el consumer, contando intentos. Si falla 3 veces, publica al topic `{topic}.dlq` y haz commit para avanzar. Agrega un subscriber al DLQ que notifique al cliente.

**Módulos del curso relacionados:** Módulo 3 (Dead Letter Queues, consumer groups, offsets), Módulo 9 (resiliencia)

---

## Fase 2: Patrones Avanzados (requiere Fase 1 completada)

### Issue #3: Consultar el estado del pedido es lento 🐢 (CQRS)

**Contexto:**
El endpoint `GET /api/orders/{id}/status` necesita saber el estado completo del pedido: ¿la cocina confirmó? ¿se cobró? ¿hay rider? ¿hay tracking? Para responder, el Order Service hace 4 llamadas REST a los otros servicios y espera cada respuesta. Resultado: ~47 segundos para una simple consulta de estado.

Esto no escala. Imagina miles de clientes refrescando su página de tracking.

**Cómo reproducir:**
1. Haz un pedido y espera a que se complete
2. Ejecuta: `curl http://localhost:8081/api/orders/{orderId}/status`
3. Observa que tarda ~47 segundos en responder
4. Ahora imagina 1000 clientes haciendo esa consulta simultáneamente

**Criterios de aceptación:**
- [ ] `GET /api/orders/{id}/status` responde en menos de 100ms
- [ ] El estado se lee de una tabla desnormalizada `order_read_model` en SQL Server (ya existe en `init.sql`)
- [ ] Una proyección de eventos actualiza la tabla cuando llegan eventos de cada servicio
- [ ] La proyección escucha los topics: `orders.created`, `kitchen.confirmed`, `payment.completed`, `rider.assigned`, `notification.sent`, `tracking.created`, `kitchen.failed`, `refund.completed`
- [ ] El endpoint NO hace llamadas REST a otros servicios

**Pistas:**
- 💡 CQRS separa el modelo de escritura (crear pedido) del modelo de lectura (consultar estado). ¿Qué pasaría si tuvieras una tabla plana con todo el estado del pedido, actualizada por eventos?
- 🔍 La tabla `order_read_model` ya existe en SQL Server (revisa `init.sql`). Necesitas: (1) un `OrderProjection` que suscriba a todos los topics y haga INSERT/UPDATE en la tabla, (2) un `OrderReadModelRepository` que consulte esa tabla por JDBC.
- 🛠️ Crea `OrderProjection` en `infrastructure/adapter/in/event/`. Para cada evento, mapea el status: `orders.created` → INSERT con status CREATED, `kitchen.confirmed` → UPDATE status a KITCHEN_CONFIRMED, `payment.completed` → UPDATE a PAID, etc.

**Módulos del curso relacionados:** Módulo 9 (CQRS, proyecciones, modelos de lectura)

---

### Issue #4: Pagaste pero no hay comida 😱 (Saga)

**Contexto:**
Pide una "Pizza Imposible" del menú de La Pizzería de Mario. La cocina confirma el pedido, el pago se procesa exitosamente... y luego la cocina falla durante la preparación. Resultado: el cliente pagó, pero nunca recibe su comida. No hay reembolso, no hay disculpa, no hay nada.

Este es el problema clásico de las transacciones distribuidas: ¿cómo deshaces una operación que ya se completó en otro servicio?

**Cómo reproducir:**
1. Abre http://localhost:3000
2. Selecciona "La Pizzería de Mario"
3. Agrega "Pizza Imposible" al pedido
4. Haz clic en "Pedir Ahora"
5. Observa: la cocina confirma ✅, el pago se procesa ✅, luego la cocina falla ❌
6. El cliente pagó pero no hay comida. No hay reembolso automático.

**Criterios de aceptación:**
- [ ] Cuando Kitchen falla después del pago, publica `KitchenFailedEvent` al topic `kitchen.failed`
- [ ] Payment Service escucha `kitchen.failed` → procesa reembolso automático → publica `RefundCompletedEvent`
- [ ] Notification Service escucha `kitchen.failed` → envía disculpa al cliente
- [ ] Order Service escucha `kitchen.failed` → marca el pedido como CANCELLED
- [ ] El flujo de compensación es completamente automático — sin intervención humana
- [ ] La proyección CQRS (Issue #3) refleja el estado CANCELLED y luego REFUNDED

**Pistas:**
- 💡 El Saga Pattern coordina transacciones distribuidas con compensaciones. En una saga coreografiada, cada servicio escucha eventos de fallo y ejecuta su propia compensación.
- 🔍 En `KitchenApplicationService`, después de confirmar y que el pago se procese, el servicio detecta "Pizza Imposible" y publica `KitchenFailedEvent`. En el código base esto no hace nada. Necesitas que Payment, Notification y Order reaccionen a ese evento.
- 🛠️ En Payment Service: crea un subscriber para `kitchen.failed` que llame a `refundPayment()` y publique `RefundCompletedEvent`. En Notification Service: subscriber que envíe notificación de tipo ORDER_CANCELLED. En Order Service: subscriber que actualice el status a CANCELLED.

**Módulos del curso relacionados:** Módulo 9 (Saga Pattern, compensación, transacciones distribuidas)

---

### Issue #5: El rastreador se actualiza cada 10 segundos ⏳ (SSE)

**Contexto:**
Abre la página de tracking después de hacer un pedido. El estado se actualiza cada 10 segundos porque el frontend hace polling con `$.ajax`. Esto significa:
- El cliente puede esperar hasta 10 segundos para ver que su pedido avanzó
- Cada poll es una petición HTTP al servidor — multiplica por 1000 clientes
- La mayoría de las peticiones devuelven el mismo estado (desperdicio de recursos)

**Cómo reproducir:**
1. Haz un pedido y ve a la página de tracking
2. Abre las DevTools del navegador (Network tab)
3. Observa las peticiones GET cada 10 segundos
4. Nota el delay entre que un evento ocurre y el frontend se entera

**Criterios de aceptación:**
- [ ] El frontend usa `EventSource` (SSE) en vez de polling con `$.ajax`
- [ ] Las actualizaciones de estado aparecen en el browser en menos de 1 segundo
- [ ] No hay peticiones periódicas en la Network tab — solo la conexión SSE abierta
- [ ] El endpoint `GET /api/orders/{id}/stream` envía eventos SSE conforme llegan
- [ ] Si la conexión SSE se pierde, el frontend reconecta automáticamente
- [ ] Se elimina el `setInterval` de `tracking.js`

**Pistas:**
- 💡 Server-Sent Events (SSE) permite al servidor "empujar" datos al cliente por una conexión HTTP persistente. El cliente abre la conexión y el servidor envía eventos conforme ocurren.
- 🔍 Jersey soporta SSE con `SseEventSink` y `SseBroadcaster`. Revisa la dependencia `jersey-media-sse` que ya está en el `pom.xml`. Necesitas un endpoint que registre sinks y un bridge que escuche eventos de Kafka y los reenvíe.
- 🛠️ (1) Crea `OrderSseResource` con `GET /api/orders/{id}/stream` que devuelva `void` y acepte un `@Context SseEventSink`. (2) Crea `SseEventBridge` que suscriba a los topics de Kafka y llame a `broadcaster.broadcast()`. (3) En `tracking.js`, reemplaza el `setInterval` con `new EventSource('/api/orders/' + orderId + '/stream')` y `source.addEventListener(...)`.

**Módulos del curso relacionados:** Módulo 7 (SSE, SseBroadcaster, EventSource, dashboard en tiempo real)
