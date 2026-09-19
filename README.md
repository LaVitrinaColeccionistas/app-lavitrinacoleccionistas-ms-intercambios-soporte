# Microservicio de Intercambios y Soporte

Encargado de la gestión de intercambios entre usuarios y de las solicitudes de soporte.

## Arquitectura

El microservicio utiliza una arquitectura organizada por capas:

- **Controller:** expone los endpoints REST y delega las operaciones a la capa de servicio.
- **Service:** contiene la lógica de negocio del microservicio.
- **Repository:** gestiona el acceso a la base de datos mediante Spring Data JPA.
- **Mapper:** realiza la conversión entre Entities y DTOs mediante MapStruct.
- **Specification:** permite construir filtros dinámicos para la búsqueda de intercambios.
- **Exception:** centraliza el manejo de errores de la API.
- **Config:** contiene la configuración de OpenAPI/Swagger.

El flujo principal de las operaciones es:

`Controller → Service → Repository`

## Entidades utilizadas

El microservicio trabaja principalmente con:

- **Intercambio:** representa una propuesta de intercambio entre dos usuarios.
- **DetalleIntercambio:** almacena los productos involucrados, cantidad y tipo de movimiento.
- **Producto:** entidad referenciada desde los detalles del intercambio.
- **TicketSoporte:** representa una solicitud enviada mediante el formulario de soporte.

### Relación muchos a muchos

La relación entre **Intercambio** y **Producto** se gestiona mediante la entidad intermedia `DetalleIntercambio`.

De esta forma, un intercambio puede contener varios productos y un producto puede participar en distintos intercambios.

`Intercambio 1:N DetalleIntercambio N:1 Producto`

La entidad intermedia permite además almacenar información propia de la relación, como la cantidad y el tipo de movimiento del producto.

## DTOs y módulo Common

Las entidades, DTOs y enumeraciones compartidas son reutilizadas desde el JAR:

`app-lavitrinacoleccionistas-common`

La dependencia se administra mediante **Maven**, evitando redefinir estos modelos dentro del microservicio.

La conversión entre DTOs y Entities se realiza mediante **MapStruct**, incluyendo operaciones Entity → DTO y DTO → Entity.

## API REST

### Intercambios

Se implementan operaciones para:

- Crear un intercambio.
- Obtener un intercambio por ID.
- Listar intercambios con paginación.
- Buscar intercambios mediante filtros opcionales por usuario y estado.
- Actualizar un intercambio.
- Actualizar el estado de un intercambio.
- Realizar eliminación lógica.

La eliminación mediante `DELETE` no elimina físicamente el registro de la base de datos. El intercambio se marca como inactivo mediante el atributo `activo`.

### Detalles de intercambio

Se implementan operaciones para:

- Listar los detalles asociados a un intercambio.
- Agregar productos a un intercambio.

### Soporte

Se implementan operaciones para:

- Crear tickets de soporte.
- Listar tickets utilizando paginación.

## Paginación y filtros

Los listados utilizan `Page` y `Pageable` de Spring Data.

La búsqueda de intercambios permite aplicar filtros opcionales mediante `Specification`, actualmente por:

- Usuario participante.
- Estado del intercambio.

Los intercambios eliminados lógicamente no se incluyen en las consultas activas.

## Manejo de excepciones

El manejo de errores se encuentra centralizado mediante `@RestControllerAdvice`.

Se controlan, entre otros:

- Recursos no encontrados.
- Rutas inexistentes.
- Errores de validación.
- Errores internos no controlados.

Las respuestas presentan mensajes controlados y evitan exponer detalles técnicos internos.

## Logging

El proyecto utiliza **SLF4J y Logback**.

Se registran:

- Solicitudes recibidas por los Controllers.
- Operaciones relevantes de la capa Service.
- Consultas SQL generadas por Hibernate en nivel DEBUG.
- Errores controlados y no controlados.

Los logs también se almacenan en archivos mediante la configuración definida en `logback-spring.xml`.

## Swagger / OpenAPI

Los endpoints REST se encuentran documentados mediante **Springdoc OpenAPI**.

Se utilizan anotaciones como:

- `@Tag`
- `@Operation`
- `@Parameter`

Esto permite visualizar y probar la API desde Swagger UI.

## Anotaciones principales

| Anotación | Uso |
|---|---|
| `@RestController` | Define los controladores REST |
| `@RequestMapping` | Define la ruta base de un controlador |
| `@GetMapping` | Operaciones HTTP GET |
| `@PostMapping` | Operaciones HTTP POST |
| `@PutMapping` | Operaciones HTTP PUT |
| `@PatchMapping` | Actualizaciones parciales |
| `@DeleteMapping` | Operaciones HTTP DELETE |
| `@Service` | Define componentes de lógica de negocio |
| `@Repository` | Define componentes de acceso a datos |
| `@Transactional` | Controla las transacciones |
| `@RequiredArgsConstructor` | Genera constructores para inyección de dependencias |
| `@Mapper` | Define mappers de MapStruct |
| `@RestControllerAdvice` | Centraliza el manejo de excepciones |
| `@ExceptionHandler` | Gestiona excepciones específicas |
| `@Slf4j` | Proporciona logging mediante SLF4J |
| `@Tag`, `@Operation`, `@Parameter` | Documentación OpenAPI |

## Dependencias principales

- Spring Boot
- Spring Web MVC
- Spring Data JPA
- PostgreSQL Driver
- La Vitrina Coleccionistas Common
- MapStruct
- Lombok
- Springdoc OpenAPI
- Spring Validation


