# AGENTS.md - Backend SIGAP

## Contexto general del proyecto

Este repositorio contiene los microservicios backend del Sistema Integrado de Gestión de Agua Potable, SIGAP.

Stack principal:

- Java 25
- Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL
- Microservicios
- Docker / Docker Compose
- Eureka Server
- API Gateway
- Config Server

Microservicios actuales:

- `ms-auth-server`
- `ms-config-server`
- `ms-gateway`
- `ms-meters`
- `ms-partner`
- `ms-readings`
- `ms-registry-server`
- `ms-report`

Cada microservicio debe mantenerse independiente, con responsabilidades claras, bajo acoplamiento y alta cohesión.

---

## Rol esperado del agente de IA

Actúa como un desarrollador backend senior especializado en:

- Java moderno
- Spring Boot
- Microservicios
- PostgreSQL
- JPA / Hibernate
- APIs REST
- Seguridad
- Buenas prácticas de arquitectura
- Código limpio y mantenible
- Alta concurrencia con hilos virtuales

Antes de generar código, analiza:

1. La responsabilidad del microservicio.
2. La estructura actual de paquetes.
3. La entidad JPA existente.
4. El contrato del endpoint.
5. El impacto en base de datos.
6. Las validaciones necesarias.
7. El manejo de errores.
8. La escalabilidad y concurrencia.

No generar código improvisado ni mezclar responsabilidades.

---

## Estructura estándar de paquetes

Cada microservicio debe mantener una estructura similar a esta:

```text
src/main/java/com/sigap/<microservice>
├── client
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── repository
├── service
└── <Microservice>Application.java
```

---

## Responsabilidad por paquete

### `controller`

Contiene los controladores REST.

Reglas:

- No colocar lógica de negocio en el controller.
- Recibir request, validar entrada y delegar al service.
- Usar `@RestController`.
- Usar rutas versionadas, por ejemplo `/api/v1/readings`.
- Usar `@Valid` en los request.
- Retornar `ResponseEntity`.
- Mantener métodos pequeños y claros.

Ejemplo:

```java
@RestController
@RequestMapping("/api/v1/readings")
@RequiredArgsConstructor
public class ReadingController {

    private final ReadingService readingService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReadingResponse>> create(
            @Valid @RequestBody ReadingRequest request
    ) {
        return ResponseEntity.ok(readingService.create(request));
    }
}
```

---

### `service`

Contiene la lógica de negocio.

Reglas:

- Usar `@Service`.
- Usar interfaces cuando el caso de uso sea grande o pueda tener varias implementaciones.
- Usar `@Transactional` para escrituras.
- Usar `@Transactional(readOnly = true)` para consultas.
- No devolver entidades directamente al controller.
- Mapear entidades a DTOs de respuesta.
- Validar reglas de negocio aquí.

Ejemplo:

```java
@Service
@RequiredArgsConstructor
public class ReadingServiceImpl implements ReadingService {

    private final ReadingRepository readingRepository;

    @Override
    @Transactional
    public ApiResponse<ReadingResponse> create(ReadingRequest request) {
        // validar regla de negocio
        // crear entidad
        // guardar
        // devolver response
    }
}
```

---

### `repository`

Contiene acceso a datos.

Reglas:

- Usar `JpaRepository`.
- Evitar queries nativas si JPQL o métodos derivados son suficientes.
- Usar nombres claros.
- Para consultas complejas, preferir `@Query`.
- No colocar lógica de negocio aquí.

Ejemplo:

```java
public interface ReadingRepository extends JpaRepository<ReadingEntity, Long> {

    boolean existsByMeterIdAndPeriod(Long meterId, String period);

    Optional<ReadingEntity> findByMeterIdAndPeriod(Long meterId, String period);
}
```

---

### `entity`

Contiene entidades JPA.

Reglas:

- Usar nombres en inglés.
- Usar `@Entity`.
- Usar `@Table`.
- No usar entidades como request o response.
- Usar `BigDecimal` para valores monetarios o consumos con decimales.
- Usar `LocalDate` para fechas.
- Usar `LocalDateTime` para fecha y hora.
- No usar `double` ni `float` para valores monetarios o consumos.
- Evitar relaciones bidireccionales si no son necesarias.
- Evitar `FetchType.EAGER`.
- Preferir `FetchType.LAZY`.

Ejemplo:

```java
@Entity
@Table(name = "lecturas_medidor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lectura_id")
    private Long readingId;

    @Column(name = "medidor_id", nullable = false)
    private Long meterId;

    @Column(name = "periodo", nullable = false, length = 7)
    private String period;
}
```

---

### `dto`

Contiene request y response.

Reglas:

- Separar DTOs de entrada y salida.
- Usar Bean Validation.
- No exponer entidades JPA directamente.
- Los nombres deben terminar en `Request` o `Response`.
- Preferir `record` para DTOs simples cuando aplique.

Ejemplo:

```java
public record ReadingRequest(
        @NotNull Long meterId,
        @NotBlank String period,
        @NotNull BigDecimal currentReading
) {
}
```

---

### `exception`

Contiene excepciones y manejador global.

Reglas:

- Usar excepciones personalizadas para reglas de negocio.
- Centralizar errores con `@RestControllerAdvice`.
- No usar `try/catch` repetitivo en controllers.
- No exponer stacktrace al cliente.

Ejemplo:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        return ResponseEntity.badRequest().body(
                ApiResponse.error("999", ex.getMessage())
        );
    }
}
```

---

## Convenciones de nombres

### Clases

Usar nombres claros en inglés:

```text
ReadingEntity
ReadingRequest
ReadingResponse
ReadingService
ReadingServiceImpl
ReadingRepository
ReadingController
```

### Métodos

Usar verbos claros:

```text
create
update
delete
findById
findAll
validateReading
calculateConsumption
```

### Variables

Evitar abreviaturas confusas.

Correcto:

```java
BigDecimal previousReading;
BigDecimal currentReading;
BigDecimal calculatedConsumption;
```

Incorrecto:

```java
BigDecimal prev;
BigDecimal curr;
BigDecimal calc;
```

---

## Respuesta estándar de API

Todos los endpoints deben responder con una estructura estándar.

Ejemplo:

```java
public record ApiResponse<T>(
        String codResult,
        String message,
        T data
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("000", "Proceso exitoso", data);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
```

Códigos sugeridos:

```text
000 - Éxito
999 - Error general
400 - Error de validación
404 - Recurso no encontrado
409 - Conflicto de negocio
```

---

## Reglas para nuevos endpoints

Cuando se cree un nuevo endpoint:

1. Crear DTO request.
2. Crear DTO response.
3. Agregar método en controller.
4. Agregar método en service.
5. Implementar lógica en service impl.
6. Usar repository para persistencia.
7. Validar datos obligatorios.
8. Manejar errores con excepciones.
9. No devolver entidades directamente.
10. Agregar logs útiles.
11. Agregar pruebas unitarias cuando aplique.
12. Revisar impacto en base de datos.
13. Documentar ejemplo de request y response.

---

## Hilos virtuales y alta concurrencia

Para nuevos endpoints que realizan operaciones bloqueantes, como consultas a PostgreSQL mediante JPA o llamadas HTTP, se debe preferir el uso de hilos virtuales.

Configurar en cada microservicio Spring Boot:

```properties
spring.threads.virtual.enabled=true
```

Reglas:

- Mantener endpoints simples y bloqueantes con Spring MVC.
- No crear pools manuales de threads si no es necesario.
- No usar `CompletableFuture` sin necesidad real.
- No usar `parallelStream()` para consultas de base de datos.
- Controlar el tamaño del pool de conexiones de PostgreSQL.
- Recordar que la base de datos sigue siendo el límite real de concurrencia.
- No abrir transacciones largas.
- No hacer consultas N+1.
- Paginar respuestas grandes.
- Medir con pruebas de carga antes de asumir mejoras.

Configuración sugerida de Hikari:

```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

Ajustar estos valores según carga real, número de instancias y capacidad de PostgreSQL.

---

## Reglas JPA / PostgreSQL

### Entidades

- Usar `Long` para ids.
- Usar `BigDecimal` para valores monetarios o consumo.
- Usar `LocalDate` y `LocalDateTime`.
- Usar `@Column(nullable = false)` donde aplique.
- Usar índices y constraints cuando sean necesarios.
- No usar `@Data` de Lombok en entidades JPA si genera métodos problemáticos con relaciones.
- Preferir `@Getter`, `@Setter`, `@NoArgsConstructor`.

### Relaciones

- Evitar relaciones bidireccionales si no son necesarias.
- Evitar `FetchType.EAGER`.
- Preferir `FetchType.LAZY`.
- No serializar entidades con relaciones directamente en JSON.
- Evitar cascadas peligrosas como `CascadeType.ALL` si no están justificadas.

### Consultas

- Usar paginación para listados.
- Evitar traer todos los registros con `findAll()` si la tabla puede crecer.
- Usar `Pageable`.
- Usar proyecciones o DTOs cuando sea necesario.
- Revisar índices para filtros frecuentes.

Ejemplo:

```java
Page<ReadingEntity> findByMeterId(Long meterId, Pageable pageable);
```

---

## Transacciones

Reglas:

- Métodos de escritura deben usar `@Transactional`.
- Métodos de lectura deben usar `@Transactional(readOnly = true)`.
- No llamar APIs externas dentro de una transacción si no es estrictamente necesario.
- Mantener transacciones cortas.
- Validar existencia antes de actualizar o eliminar.
- Evitar lógica pesada dentro de transacciones.

Ejemplo:

```java
@Transactional
public ApiResponse<ReadingResponse> update(Long id, ReadingRequest request) {
    ReadingEntity entity = readingRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Lectura no encontrada"));

    // actualizar campos

    return ApiResponse.success(mapToResponse(entity));
}
```

---

## Logs

Usar logs claros y útiles.

Reglas:

- Usar `log.info` para inicio y fin de procesos importantes.
- Usar `log.warn` para validaciones fallidas o casos esperados.
- Usar `log.error` para errores inesperados.
- No imprimir contraseñas, tokens ni datos sensibles.
- Incluir ids importantes como `partnerId`, `meterId`, `readingId`, `period`.

Ejemplo:

```java
log.info("Creando lectura. meterId={}, period={}", request.meterId(), request.period());
```

---

## Seguridad

Reglas:

- No exponer contraseñas.
- No guardar passwords en texto plano.
- Usar hashing seguro para credenciales.
- Validar roles en endpoints protegidos.
- No confiar en datos enviados desde frontend.
- Validar siempre en backend.
- No permitir CORS abierto en producción.
- No retornar información sensible en errores.
- No guardar tokens en logs.

---

## Validaciones de negocio SIGAP

### Lecturas

- No registrar dos lecturas para el mismo medidor y periodo.
- La lectura actual no debe ser menor que la lectura anterior.
- El consumo calculado debe ser `lecturaActual - lecturaAnterior`.
- El periodo debe tener formato `YYYY-MM`.
- El medidor debe existir y estar activo.
- La asignación medidor-socio debe estar activa si aplica.

### Socios

- No duplicar cédula/RUC.
- Validar estado activo/inactivo.
- Validar cuentas contrato asociadas.

### Medidores

- No duplicar número de medidor.
- Validar estados permitidos.
- No asignar un medidor retirado, dañado o suspendido.

---

## Pruebas

Cuando se agregue lógica de negocio, crear pruebas unitarias.

Prioridad:

1. Services.
2. Mappers.
3. Validadores.
4. Repositories con pruebas de integración si aplica.

Usar nombres descriptivos:

```text
shouldCreateReadingWhenDataIsValid
shouldThrowExceptionWhenMeterDoesNotExist
shouldRejectReadingWhenCurrentIsLowerThanPrevious
```

---

## Docker y configuración

Cada microservicio debe tener configuración clara para:

- Puerto
- Nombre de aplicación
- Perfil activo
- Conexión a PostgreSQL
- Eureka
- Config Server
- Logs
- Health checks

No quemar credenciales en código fuente.

Usar variables de entorno para credenciales:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

---

## Reglas de calidad

Antes de finalizar cambios:

- El proyecto debe compilar.
- No dejar código comentado innecesario.
- No dejar `System.out.println`.
- No duplicar lógica.
- No crear clases gigantes.
- No crear métodos demasiado largos.
- No mezclar controller con lógica de negocio.
- No mezclar entidad JPA con DTO.
- No usar nombres confusos.
- Mantener consistencia con el microservicio actual.
- Revisar advertencias de SonarLint o del IDE cuando aplique.

---

## Comandos útiles

Compilar un microservicio:

```bash
./mvnw clean package
```

Ejecutar tests:

```bash
./mvnw test
```

Levantar servicios con Docker Compose desde la raíz:

```bash
docker compose up -d
```

Ver logs:

```bash
docker logs -f <container-name>
```

---

## Regla final

Antes de modificar código, revisar la estructura existente del microservicio y mantener el estilo actual.

Si existe una clase, patrón o convención ya usada en el proyecto, reutilizarla antes de crear una nueva.
