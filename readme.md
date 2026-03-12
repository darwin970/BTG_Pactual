# BTG Pactual — Plataforma de Gestión de Fondos de Inversión

API REST para la gestión de fondos de inversión: registro de clientes, autenticación, suscripción y cancelación de fondos, y consulta del historial de transacciones.

---

## Tabla de Contenidos

1. [Tecnologías](#tecnologías)
2. [Arquitectura](#arquitectura)
3. [Requisitos Previos](#requisitos-previos)
4. [Configuración de Variables de Entorno](#configuración-de-variables-de-entorno)
5. [Ejecución del Proyecto](#ejecución-del-proyecto)
6. [Datos Semilla (precargados automáticamente)](#datos-semilla)
7. [Endpoints de la API](#endpoints-de-la-api)
8. [Pruebas con Insomnia (colección incluida)](#pruebas-con-insomnia)
9. [Estructura del Proyecto](#estructura-del-proyecto)
10. [Ejecución de Tests](#ejecución-de-tests)

---

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 17 (OpenJDK Temurin 17.0.12) |
| Spring Boot | 2.7.18 |
| AWS DynamoDB | SDK v2 (Enhanced Client) |
| Spring Security + JWT | — |
| Lombok | — |
| Gradle | 7.6.4 |
| JUnit | 4.12 |
| Mockito | 4.11.0 |

---

## Arquitectura

El proyecto sigue **Arquitectura Hexagonal (Puertos y Adaptadores)** con **CQRS** por paquetes, organizado en tres módulos Gradle:

```
microservicio/
├── dominio/          # Lógica de negocio pura (servicios, entidades, puertos)
├── aplicacion/       # Manejadores de comandos y consultas (CQRS)
└── infraestructura/  # Adaptadores REST, DynamoDB, Spring Security
```

**Separación CQRS:**
- **Comando** (`repositorio/`): operaciones de escritura atómicas con `transactWriteItems`
- **Consulta** (`dao/`): operaciones de lectura por PK o GSI

---

## Requisitos Previos

- **Java 11+** instalado y en `PATH`
- **AWS DynamoDB** accesible (cuenta AWS o instancia local)
  - Región configurada: `us-east-2` (tests de integración) / `us-east-1` (por defecto en producción)
- **Credenciales AWS** configuradas (`~/.aws/credentials` o variables de entorno `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY`)
- **Lombok** configurado en el IDE ([descargar plugin](https://projectlombok.org/download))

---

## Configuración de Variables de Entorno

El proyecto requiere las siguientes variables de entorno antes de iniciar:

| Variable | Descripción | Ejemplo |
|---|---|---|
| `JWT_SECRET` | Clave secreta para firmar tokens JWT | `mi-clave-super-secreta-256bits` |
| `AWS_REGION` | Región de AWS DynamoDB | `us-east-1` |
| `jasyptpwd` | Contraseña para desencriptar propiedades Jasypt | `password` |
| `AWS_ACCESS_KEY_ID` | Access key de AWS | `AKIAIOSFODNN7EXAMPLE` |
| `AWS_SECRET_ACCESS_KEY` | Secret key de AWS | `wJalrXUtnFEMI/K7MDENG/...` |

**Ejemplo con variables en línea de comando:**
```bash
JWT_SECRET=mi-clave-secreta AWS_REGION=us-east-1 jasyptpwd=password ./gradlew bootRun
```

---

## Ejecución del Proyecto

```bash
cd microservicio
./gradlew bootRun
```

El servidor inicia en:
```
http://localhost:8083/btg
```

> En Windows usar `gradlew.bat bootRun`

---

## Datos Semilla

Al iniciar la aplicación por primera vez, se crean automáticamente las tablas DynamoDB y se precargan los datos semilla:

### Clientes precargados

| ID | Nombre | Email | Contraseña | Rol | Saldo inicial |
|---|---|---|---|---|---|
| `admin-001` | Administrador BTG | admin@btg.com | `Admin123*` | ADMIN | $500.000 |
| `cliente-001` | Cliente BTG | cliente@btg.com | `Cliente123*` | CLIENTE | $500.000 |

### Fondos de inversión precargados

| ID | Nombre | Monto mínimo | Categoría |
|---|---|---|---|
| `1` | FPV_BTG_PACTUAL_RECAUDADORA | $75.000 | FPV |
| `2` | FPV_BTG_PACTUAL_ECOPETROL | $125.000 | FPV |
| `3` | DEUDAPRIVADA | $50.000 | FIC |
| `4` | FDO-ACCIONES | $250.000 | FIC |
| `5` | FPV_BTG_PACTUAL_DINAMICA | $100.000 | FPV |

---

## Endpoints de la API

**URL base:** `http://localhost:8083/btg`

> Todos los endpoints excepto `POST /clientes` y `POST /auth/login` requieren el header:
> ```
> Authorization: Bearer <token>
> ```

---

### 🔐 Autenticación

#### `POST /auth/login`
Autentica un cliente y retorna un JWT.

**Request:**
```json
{
  "email": "cliente@btg.com",
  "contrasena": "Cliente123*"
}
```

**Response 200:**
```json
{
  "valor": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "clienteId": "cliente-001"
  }
}
```

**Errores:** `401` credenciales inválidas

---

### 👤 Clientes

#### `POST /clientes`
Registra un nuevo cliente. Endpoint público (sin token).

**Request:**
```json
{
  "nombre": "Juan Pérez",
  "email": "juan@email.com",
  "telefono": "3001234567",
  "saldo": 500000.0,
  "contrasena": "MiClave123*"
}
```

**Response 201:**
```json
{
  "valor": "uuid-del-cliente-creado"
}
```

**Errores:** `400` email ya registrado

---

#### `GET /clientes/{id}`
Consulta el perfil de un cliente. **Requiere token.**

**Response 200:**
```json
{
  "id": "cliente-001",
  "nombre": "Cliente BTG",
  "email": "cliente@btg.com",
  "telefono": "3002000000",
  "saldo": 500000.0,
  "rol": "CLIENTE"
}
```

**Errores:** `404` cliente no existe | `401` sin token

---

### 💰 Fondos

#### `GET /fondos`
Lista todos los fondos disponibles. **Requiere token.**

**Response 200:**
```json
[
  { "id": "1", "nombre": "FPV_BTG_PACTUAL_RECAUDADORA", "montoMinimo": 75000.0, "categoria": "FPV" },
  { "id": "2", "nombre": "FPV_BTG_PACTUAL_ECOPETROL",   "montoMinimo": 125000.0, "categoria": "FPV" }
]
```

---

### 📊 Suscripciones (Transacciones — Escritura)

#### `POST /clientes/{clienteId}/suscripciones`
Suscribe al cliente a un fondo de inversión. Descuenta el monto del saldo. **Requiere token propio.**

**Request:**
```json
{
  "fondoId": "1",
  "monto": 75000.0,
  "preferenciaNotificacion": "EMAIL"
}
```

**Response 201:**
```json
{
  "valor": {
    "id": "uuid-transaccion",
    "fondoId": "1",
    "nombreFondo": "FPV_BTG_PACTUAL_RECAUDADORA",
    "clienteId": "cliente-001",
    "monto": 75000.0,
    "estado": "ACTIVA",
    "preferenciaNotificacion": "EMAIL",
    "fecha": "2026-03-12T10:00:00",
    "fechaCancelacion": null
  }
}
```

**Errores:** `400` saldo insuficiente | `404` fondo no existe | `401`/`403` sin token / token ajeno

---

#### `DELETE /clientes/{clienteId}/suscripciones/{transaccionId}`
Cancela una suscripción activa y devuelve el monto al saldo del cliente. **Requiere token propio.**

**Response 200:**
```json
{
  "valor": {
    "id": "uuid-transaccion",
    "fondoId": "1",
    "nombreFondo": "FPV_BTG_PACTUAL_RECAUDADORA",
    "clienteId": "cliente-001",
    "monto": 75000.0,
    "estado": "CANCELADA",
    "preferenciaNotificacion": "EMAIL",
    "fecha": "2026-03-12T10:00:00",
    "fechaCancelacion": "2026-03-12T11:30:00"
  }
}
```

**Errores:** `400` no tiene suscripción activa al fondo | `404` transacción no encontrada | `401`/`403` sin token / token ajeno

---

### 📋 Historial de Transacciones (Consulta)

#### `GET /clientes/{clienteId}/transacciones`
Consulta el historial completo de transacciones del cliente, ordenado por fecha descendente. **Requiere token propio.**

**Response 200:**
```json
[
  {
    "id": "uuid-transaccion-2",
    "fondoId": "3",
    "clienteId": "cliente-001",
    "monto": 50000.0,
    "estado": "CANCELADA",
    "preferenciaNotificacion": "SMS",
    "fecha": "2026-03-12T11:00:00",
    "fechaCancelacion": "2026-03-12T12:00:00"
  },
  {
    "id": "uuid-transaccion-1",
    "fondoId": "1",
    "clienteId": "cliente-001",
    "monto": 75000.0,
    "estado": "ACTIVA",
    "preferenciaNotificacion": "EMAIL",
    "fecha": "2026-03-12T10:00:00",
    "fechaCancelacion": "N/A"
  }
]
```

> Las transacciones activas muestran `"fechaCancelacion": "N/A"`.

**Errores:** `404` cliente no existe | `401`/`403` sin token / token ajeno

---

### Resumen de Endpoints

| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| `POST` | `/auth/login` | ❌ Pública | Autenticar y obtener token JWT |
| `POST` | `/clientes` | ❌ Pública | Registrar nuevo cliente |
| `GET` | `/clientes/{id}` | ✅ Token | Consultar perfil de cliente |
| `GET` | `/fondos` | ✅ Token | Listar fondos disponibles |
| `POST` | `/clientes/{clienteId}/suscripciones` | ✅ Token propio | Suscribirse a un fondo |
| `DELETE` | `/clientes/{clienteId}/suscripciones/{transaccionId}` | ✅ Token propio | Cancelar suscripción |
| `GET` | `/clientes/{clienteId}/transacciones` | ✅ Token propio | Consultar historial |

---

## Pruebas con Insomnia

El proyecto incluye una **colección Insomnia lista para usar** con todos los endpoints configurados:

```
microservicio/btg-pactual-insomnia-collection.json
```

### Pasos para importar y ejecutar

1. **Instalar Insomnia** — descargar en [insomnia.rest](https://insomnia.rest/download)

2. **Importar la colección:**
   - Abrir Insomnia
   - Clic en `Import` → `From File`
   - Seleccionar el archivo `microservicio/btg-pactual-insomnia-collection.json`

3. **Configurar el Environment** (variables base ya incluidas):

   | Variable | Valor por defecto | Descripción |
   |---|---|---|
   | `base_url` | `http://localhost:8083/btg` | URL base del servicio |
   | `token` | *(vacío)* | Pegar el token JWT tras hacer login |
   | `cliente_id` | *(vacío)* | ID del cliente con el que operar |
   | `transaccion_id` | *(vacío)* | ID de la transacción para cancelar |

4. **Flujo de prueba recomendado:**

   ```
   1. POST /auth/login          → copiar el "token" y el "clienteId" al Environment
   2. POST /clientes/{id}/suscripciones   → copiar el "id" de la respuesta a "transaccion_id"
   3. GET  /clientes/{id}/transacciones   → verificar historial
   4. DELETE /clientes/{id}/suscripciones/{transaccionId}  → cancelar suscripción
   5. GET  /clientes/{id}/transacciones   → verificar estado "CANCELADA" y "N/A" → fecha real
   ```

### Carpetas incluidas en la colección

| Carpeta | Requests |
|---|---|
| 🔐 Autenticación | Login, credenciales inválidas |
| 👤 Clientes | Registrar cliente, consultar, errores (404, 400) |
| 💰 Fondos | Listar fondos |
| 📊 Suscripciones | Suscribir, saldo insuficiente, fondo inexistente, sin token, 403 |
| 🚫 Cancelaciones | Cancelar, transacción inexistente, ya cancelada, sin token |
| 📋 Historial de Transacciones | Historial, lista vacía, cliente inexistente, sin token |

---

## Estructura del Proyecto

```
BTG_Pactual/
├── comun/                          # Librerías compartidas entre microservicios
│   ├── comun-aplicacion-comando/   # ManejadorComandoRespuesta, ComandoRespuesta
│   ├── comun-dominio/              # Excepciones base (ExcepcionSinDatos, ExcepcionValorInvalido, etc.)
│   ├── comun-infraestructura/      # ManejadorError (mapeo excepciones → HTTP status)
│   └── comun-test/                 # BasePrueba (assertThrows helper)
│
└── microservicio/                  # Módulo principal
    ├── dominio/                    # Lógica de negocio pura
    │   └── src/main/java/.../
    │       ├── cliente/            # Agregado cliente
    │       ├── fondo/              # Agregado fondo
    │       └── transaccion/        # Agregado transacción (suscripción, cancelación, historial)
    │
    ├── aplicacion/                 # Manejadores CQRS
    │   └── src/main/java/.../
    │       ├── cliente/comando/    # ManejadorCrearCliente, ManejadorAutenticar
    │       ├── fondo/consulta/     # ManejadorListarFondos
    │       └── transaccion/
    │           ├── comando/        # ManejadorSuscribirFondo, ManejadorCancelarSuscripcion
    │           └── consulta/       # ManejadorConsultarTransacciones
    │
    ├── infraestructura/            # Adaptadores Spring Boot + DynamoDB
    │   └── src/main/java/.../
    │       ├── cliente/            # Controladores, adaptador DynamoDB cliente
    │       ├── fondo/              # Controlador, adaptador DynamoDB fondo
    │       ├── transaccion/        # Controladores, adaptadores DynamoDB transacción
    │       └── configuracion/      # ConfiguracionBeanServicio, CargaDatosSemilla, Seguridad JWT
    │
    ├── btg-pactual-insomnia-collection.json   # ← Colección Insomnia con todos los requests
    └── src/main/resources/
        └── application.yaml        # Configuración del servidor (puerto 8083, context-path /btg)
```

---

## Ejecución de Tests

### Tests unitarios (sin AWS):
```bash
cd microservicio
./gradlew dominio:test
```

### Tests de integración (requiere DynamoDB us-east-2):
```bash
cd microservicio
./gradlew infraestructura:test
```

### Todos los tests:
```bash
cd microservicio
./gradlew test
```

Los reportes HTML de cobertura se generan en:
```
microservicio/dominio/build/reports/tests/
microservicio/infraestructura/build/reports/tests/
```

---

## Notas de Seguridad

- Los tokens JWT expiran en **24 horas** (`expiracion: 86400000` ms)
- Cada cliente solo puede operar sobre sus propios recursos (validación BOLA)
- El rol `ADMIN` puede acceder a recursos de cualquier cliente
- Las contraseñas se almacenan encriptadas con BCrypt

---

*Proyecto desarrollado con Método Ceiba — Arquitectura Hexagonal + CQRS + DDD*


Este bloque contiene la estructura necesaria para construir un proyecto en java favoreciendo el enfoque de DDD. 

Los principales patrones y estilos de arquitectura que guían este bloque son

#### Arquitectura hexagonal
Arquitectura que fomenta  que nuestro dominio sea el núcleo de todas las capas, también conocida como puertos y adaptadores en la cual el dominio define los puertos y en las capas superiores se definen los adaptadores para desacoplar el dominio. Se divide princialmente en tres capas, **aplicación**, **dominio** e **infraestructura**
- **Infraestructura**: Capa que tiene las responsabilidades de realizar los adaptadores a los puertos definidos en el domino, exponer web services, consumir web services, realizar conexiones a bases de datos, ejecutar sentencias DML, en general todo lo que sea implementaciones de cualquier framework
- **Aplicación**: capa encargada de enrutar los eventos entrantes de la capa de infraestructura hacía la capa del dominio, generalmente se conoce como una barrera transaccional la cual agrupa toda la invocación de un caso de uso, se pueden encontrar patrones como Fabricas, Manejadores de Comandos, Bus de eventos, etc 
- **Dominio**: representa toda la lógica de negocio de la aplicación la cual es la razón de existir del negocio. Se busca evitar el anti-patron [https://martinfowler.com/bliki/AnemicDomainModel.html](https://martinfowler.com/bliki/AnemicDomainModel.html)  y favorecer el principio [https://martinfowler.com/bliki/TellDontAsk.html](https://martinfowler.com/bliki/TellDontAsk.html) en esta capa se pueden encontrar los siguientes patrones agregados, servicios de dominio, entidades, objetos de valor, repositorios (puerto), etc. 

Para obtener mas documentación sobre este tipo de arquitectura se recomienda [https://codely.tv/blog/screencasts/arquitectura-hexagonal-ddd/](https://codely.tv/blog/screencasts/arquitectura-hexagonal-ddd/)

#### Patrón CQRS:  
Patrón con el cual dividimos nuestro modelo de objetos en dos, un modelo para consulta y un modelo para comando (modificación de datos). Este patrón es recomendado cuando se va desarrollar lógica de negocio compleja porque nos ayuda a separar las responsabilidades y a mantener un modelo de negocio consistente. 

 - **Consulta**: modelo a través del cual se divide la responsabilidad para presentar datos en la interfaz de usuario, los objetos se modelan basado en lo que se va a presentar y no en la lógica de negocio, ejm: ver facturas, consultar clientes
 - **Comando**: son todas las operaciones que cambian el estado del sistema, ejm: (facturar, aplicar descuento), este modelo se construye todo el modelo de objetos basado en la lógica de negocio de la aplicación  

Para mayor documentación del patrón [https://martinfowler.com/bliki/CQRS.html](https://martinfowler.com/bliki/CQRS.html)

#### Especificaciones técnicas: 

 - Spring boot 2.1.2
 - Flayway -> administrar todos los script DDL e iniciliazadores de la bd 
 - Integración con Jenkins: Jenkins File
 - Integración con Sonar: Sonar properties
 - Acceso a la base de datos por medio de JDBC template
 - Se entregan pruebas de muestra automatizadas para cada una de las capas 
 - Pruebas de carga de ejemplo en el directorio microservicio/external-resources
 - Ejemplo de como modelar un Comando y un Query
 - Ejemplo de pruebas de integración con H2
 - Java 8
 - Se debe tener configurado el IDE con Lombok, descargar desde (https://projectlombok.org/download)

#### Estructura del proyecto: 
Se identifican dos carpetas principales, común y microservicio. Microservicio es la carpeta que contiene todo el código fuente para el primer microservicio de su proyecto, se recomienda cambiar el nombre de esta carpeta por la de su lógica de negocio y posteriormente modificar el archivo *settings.gradle*,  si necesita crear mas microservicios lo único que debe realizar es duplicar esta carpeta y realizar la modificación en el archivo *settings.gradle*. El proyecto común contiene código fuente que comparten todos los microservicios y capas, es una librería que importan los que requieran este código compartido, es importante tener en cuenta que no debe ir código de negocio en este lugar. 
Como cada microservicio se va realizar con CQRS, cada uno contiene su propia estructura de arquitectura hexagonal en la cual no se comparten los modelos.

#### Importar el proyecto:
Para importar el proyecto se recomienda usar Gradle en la versión 5.0, se debe importar desde la ruta *microservicio/build.gradle*
Después de importar el proyecto se queda viendo de la siguiente manera 

![enter image description here](https://drive.google.com/uc?id=1x2ZVpM2steX0Er-jDNoffQ_V6pRVdW0k)

#### Bloque HealthCheck
Es un bloque que tiene como fin saber el estado de otros bloques o servicios agregados como por ejemplo de mysql,sqlServer etc.Para esto es necesario crear un paquete com.btg.core.actuator en microservicio-consulta-infraestructura    e implementar una interfaz llamada  HealthCheck  con la anotacion @Component sobreescribiendo los siguientes metodos:

- **registrarBloque()**: Que tiene la funcion de registrar el bloque que quiere tenerse encuenta ,para esto es necesario que ala hora de construir la clase que implemente  la interface HealthCheck se inyecte la clase manejadorHealthCheckBloques que tiene en memoria los bloques 
implementados pasandole la cadena del nombre y la clase en si.

- **healthCheck()**: Es un metodo que se le delega al programador para que segun el servicio o el bloque usado implemente una funcionalidad que logre detectar que este ya no esta arriba.No devuleve un valor si no excepcion de tipo ExepcionBloqueSinServicio.

Al momento de crear el bloque principal pedira un tiempo que estara dado en  milisegundos llamado tiempoHealthCheck que estara guardado en archivo application.yaml de resources del microservicio.


nota* Es recomendable  tener muy encuenta el tiempo asignado a HealthCheck como tal en las base de datos el tiempo que tarda en verificar es 30000 milisegundos que en segundos son 30 entoces debe ser mayor a este , para que cuando  healthCheck  realice la revision ,ya todos los bloques hallan devuelto su valor para no tener  inconsistencias de
los valores devuelto .Se esperaria aumentar el tiempo cada vez que un bloque se implemente dependiendo tambien de su tiempo de retardo.



*Cualquier duda o aporte con este bloque contactar a juan.botero@ceiba.com.co o juan.castano@ceiba.com.co*