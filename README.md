# Microservicio de Intercambios y Soporte

Microservicio de **La Vitrina Coleccionistas** encargado de gestionar intercambios de productos entre usuarios y tickets de soporte.

## Funcionalidades
- CRUD de intercambios con borrado lógico.
- Consultas con filtros y paginación.
- Registro y consulta de tickets de soporte.
- Manejo global de excepciones.
- Logs mediante SLF4J y Logback.

## Arquitectura
El proyecto está organizado en capas **Controller, Service y Repository**, además de Mapper para convertir entidades y DTOs. Se utilizan clases base para reutilizar operaciones comunes.

## Relación muchos a muchos
Un intercambio puede incluir varios productos y un producto puede participar en distintos intercambios.

Esta relación se resuelve mediante la entidad intermedia `DetalleIntercambio`, que guarda el identificador del producto, la cantidad y el tipo de movimiento (`OFRECIDO` o `SOLICITADO`).

**Intercambio → DetalleIntercambio → Producto**

## Anotaciones principales
- `@RestController`: define los endpoints REST.
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping` y `@DeleteMapping`: operaciones HTTP.
- `@Service`: identifica la capa de lógica de negocio.
- `@Repository`: acceso a datos mediante JPA.
- `@Transactional`: gestión de transacciones.
- `@Mapper`: conversión entre entidades y DTOs.
- `@RestControllerAdvice`: manejo global de excepciones.
- `@Slf4j`: registro de logs.

## Configuración
Utiliza variables de entorno para conectarse a PostgreSQL, Eureka para el descubrimiento de servicios y API Gateway para el acceso a los endpoints.