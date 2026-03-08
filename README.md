# challenger-inventory-service

Microservicio de inventario para gestionar stock por producto.

## Stack

- Java 17
- Spring Boot WebFlux
- Spring Data JPA
- H2 en memoria
- OpenAPI Generator

## Ejecutar local

```powershell
.\gradlew.bat clean bootRun
```

La API queda disponible en `http://localhost:8082`.

## Endpoints principales

- `POST /inventory`: crea o actualiza stock por `productId`
- `GET /inventory`: lista inventario
- `GET /inventory/{productId}`: obtiene inventario de un producto

### Ejemplo request

```json
{
  "productId": 1,
  "stock": 20
}
```

## Respuestas de error

- `400` datos invalidos
- `404` inventario no encontrado
- `500` error interno inesperado

## Documentacion API

- Especificacion OpenAPI: `src/main/resources/openapi/inventory-api.yaml`
- Swagger UI: `http://localhost:8082/swagger-ui.html`

## Validacion tecnica

```powershell
.\gradlew.bat test --no-daemon
.\gradlew.bat openApiGenerate --no-daemon
.\gradlew.bat javadoc --no-daemon
```

Javadoc generado en `build/docs/javadoc/index.html`.
