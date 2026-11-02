# TP2: Persistencia, Migraciones y Arquitectura Hexagonal

## Levantar la Base de Datos y Migraciones
1. Asegurate de tener Docker Desktop abierto y ejecutándose.
2. Abrí una terminal en la raíz de este proyecto y ejecutá: `docker compose up -d`.
3. Esto levantará un contenedor de PostgreSQL en el puerto 5432 con la base de datos `webii_tp2`.
4. Al arrancar la aplicación de Spring Boot, **Flyway** se encarga automáticamente de correr las migraciones (archivos `.sql` en `db/migration`) para crear y versionar las tablas.

## Arquitectura Hexagonal (Punto 4)
Al migrar de memoria (Map) a PostgreSQL (JPA), **no fue necesario modificar el Dominio, el Service ni el Controller**.
* **Qué cambió:** Eliminamos `InMemoryFavoritoRepository` y creamo la interfaz `FavoritoJpaRepository`, la entidad `FavoritoEntity` y el adaptador `FavoritoRepositoryAdapter`.
* **Por qué fue posible:** Porque `FavoritoRepository` funciona como un **puerto** (un contrato). Tanto la versión en memoria antigua como el nuevo adaptador JPA son implementaciones (adapters) intercambiables que respetan ese contrato. El Service solo conoce el puerto, por lo que ignora los detalles de la infraestructura subyacente.

## Evolución del Esquema (Punto 6)
**¿Por qué usamos una migración nueva (V4) en lugar de modificar la V3?**
Flyway calcula un checksum (hash) de cada migración aplicada y lo guarda en su historial. Si editáramos la migración `V3` ya aplicada, el checksum cambiaría y Flyway rechazaría el arranque para evitar que las bases de datos queden en estados divergentes. Por eso, cualquier cambio nuevo (como hacer que `lista_id` sea NOT NULL) debe hacerse siempre en un archivo de migración nuevo (`V4`).

## Transacciones y ACID (Punto 7)
En la operación de "Mover Favoritos", se realizan varias escrituras: actualizar los favoritos y luego eliminar la lista de origen. 
Utilizamos la anotación `@Transactional` para garantizar la **Atomicidad** (la "A" de ACID). Si sacáramos esta anotación y ocurriera un error (ej. pérdida de conexión) justo después de mover los favoritos pero antes de borrar la lista origen, la base de datos quedaría en un estado inconsistente (favoritos movidos, pero la lista origen seguiría existiendo vacía). `@Transactional` asegura que o se aplican todos los cambios, o no se aplica ninguno.

---

# TP3: Seguridad, Pruebas e Integración Continua

## Justificación de la regla de autorización (Consigna 4.5)
**¿Dónde vive la regla de autorización por propiedad de recursos?**
La regla de autorización vive en la capa de servicios (`ListaServiceImpl`), centralizada en el método privado `buscarPropiaOFallar(Long id)` y apoyada por el componente desacoplado `ContextoSeguridad`.

**¿Por qué la elegimos en el Service y no en el Controller o en un filtro?**
1. **Dependencia del estado del dato (Persistencia):** Para determinar si una lista le pertenece al usuario autenticado, es indispensable consultar primero la base de datos para recuperar la entidad y conocer su `usuarioId`. Ni un filtro de Spring Security ni una anotación genérica en el Controller pueden conocer el dueño de una lista con ID puntual sin consultar la base. En el Service la lista ya se encuentra cargada.
2. **Separación semántica clara entre 404 y 403:** Al centralizar el control en `buscarPropiaOFallar`, primero verificamos si la lista existe (`404 Not Found`). Si existe, verificamos si el usuario actual es el dueño o tiene rol `ADMIN`. Si no lo es, arrojamos `AccesoDenegadoException` (`403 Forbidden`). De este modo no confundimos "el recurso no existe" con "el recurso existe pero no tenés permiso para operarlo".
3. **Separación de responsabilidades:** El Controller solo debe gestionar el protocolo HTTP (recibir el request, validar el formato de entrada, llamar al servicio y retornar el status code correspondiente). Las reglas de negocio y los permisos sobre los recursos pertenecen conceptualmente a la lógica de negocio en el Service.
4. **Reutilización:** Si en el futuro se agregan otros puntos de entrada a la aplicación (colas de mensajería, tareas programadas, llamadas gRPC), la regla de negocio y de propiedad queda intacta y protegida dentro del Service.

## Ejecución de Pruebas Automatizadas (Consigna 5 y 8.2)

### 1. Pruebas Unitarias (con Mockito)
Prueban la lógica pura de negocio en aislamiento sin levantar Spring ni conectarse a bases de datos:
* `UsuarioServiceImplTest`: Verifica que la contraseña del usuario se encripte con BCrypt y nunca se guarde en texto plano.
* `ListaServiceImplTest`: Verifica que al crear una lista, esta quede asociada automáticamente al ID del usuario autenticado obtenido del contexto.

Para ejecutarlas:
```bash
./mvnw test -Dtest=*Test
```

### 2. Pruebas de Integración (con Testcontainers)
En `ListaControllerIT` se levanta la aplicación completa (`@SpringBootTest`, `@AutoConfigureMockMvc`) contra un contenedor de **PostgreSQL real** administrado automáticamente por Testcontainers. Flyway ejecuta todas las migraciones (`V1` a `V6`) contra esa base limpia en cada corrida.

Casos verificados:
- Login con credenciales válidas ➜ `200 OK` con token JWT.
- Login con credenciales inválidas ➜ `401 Unauthorized`.
- Acceso a `/api/listas` sin token ➜ `401 Unauthorized`.
- Acceso a una lista propia con token ➜ `200 OK`.
- Acceso a una lista ajena con token de otro usuario ➜ `403 Forbidden`.
- Acceso de un usuario con rol `ADMIN` a una lista ajena ➜ `200 OK`.

Para ejecutarlas *(requiere tener Docker Desktop en ejecución)*:
```bash
./mvnw test
```

## Integración Continua (CI) en GitHub Actions (Consigna 6 y 8.2)
El repositorio cuenta con un pipeline automatizado en `.github/workflows/ci.yml`.
* **Disparador:** Se ejecuta en cada `push` o `pull request` hacia cualquier rama.
* **Flujo:** Levanta un entorno Ubuntu con Java 25, compila el código y ejecuta tanto las pruebas unitarias como las de integración (levantando Testcontainers y PostgreSQL).
* **Cómo leer el resultado:**
  * En la pestaña **Actions** de GitHub (o en el icono al lado de cada commit):
    * Un tilde verde indica que el build y todos los tests pasaron exitosamente.
    * Una cruz roja indica que hubo un fallo de compilación o que al menos un test unitario/integración falló. Al hacer clic se accede a la consola de ejecución con el log detallado del error.

## Despliegue en Render (Consigna 7 y 8.2)

### 1. Base de Datos PostgreSQL
1. En el dashboard de **Render**, crear una nueva base de datos: `New +` ➜ `PostgreSQL`.
2. Asignar un nombre (ej. `webii-db`) y seleccionar el plan gratuito.
3. Copiar los datos de conexión que provee Render:
   * **Host**, **Database**, **Username**, **Password**.
   * O el string de conexión (reemplazando `postgres://` por `jdbc:postgresql://`).

### 2. Web Service (Aplicación Spring Boot)
1. En **Render**, crear un nuevo servicio web: `New +` ➜ `Web Service`.
2. Conectar el repositorio de GitHub y seleccionar la rama correspondiente.
3. Render detectará automáticamente el `Dockerfile` con Java 25.
4. En la sección **Environment Variables**, configurar:
   * `DB_URL`: `jdbc:postgresql://<host-de-render>:5432/<nombre-base>`
   * `DB_USERNAME`: `<usuario-de-render>`
   * `DB_PASSWORD`: `<password-de-render>`
   * `JWT_SECRET`: una clave secreta segura de al menos 48 caracteres.
5. Iniciar el despliegue (**Deploy**).
6. Verificar en los logs que Flyway aplique las migraciones (`V1` a `V6`) y que la aplicación inicie en el puerto asignado.

### 3. Verificación de la URL pública
* `GET https://<tu-app>.onrender.com/health` ➜ debe responder `{"status":"ok","service":"demo"}` con `200 OK`.
* `GET https://<tu-app>.onrender.com/api/listas` sin token ➜ responde `401 Unauthorized`.
* `POST https://<tu-app>.onrender.com/api/auth/login` con credenciales válidas ➜ devuelve el token JWT.
* `GET https://<tu-app>.onrender.com/api/listas` con header `Authorization: Bearer <token>` ➜ responde `200 OK`.
