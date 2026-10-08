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