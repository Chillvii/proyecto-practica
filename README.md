# proyecto-practica

**Primer paso:** abre [`MANUAL.html`](MANUAL.html) en el navegador (doble clic o arrastrar el archivo). Ahí tienes un *overview* visual del proyecto: qué instalar, objetivos, stack, retos y flujo de trabajo antes de seguir con este README.

Proyecto didáctico para que un desarrollador junior se familiarice con el stack
**Spring Boot + MongoDB + Angular** y rompa mano construyendo su propio código a
partir de una base mínima ya funcionando.

## Qué es esto

Un monorepo con:

- Un **backend** en Java 21 + Spring Boot 3 que expone un único endpoint:
  `GET /api/employees` (listado completo, sin paginación ni filtros).
- Un **frontend** en Angular 17+ (standalone, signals, SCSS) que consume ese
  endpoint y muestra la lista en una tabla simple.
- Un **`docker-compose.yml`** que levanta MongoDB y Mongo Express (UI web para
  ver los datos).

El alcance es **deliberadamente minimalista**. Lo que está hecho es exactamente
"la base"; el resto (detalle, alta, edición, borrado, validaciones, modales,
filtros, paginación, segunda entidad…) son retos para ti. Mira la sección
[Tu siguiente reto](#tu-siguiente-reto).

## Prerrequisitos

| Herramienta    | Versión recomendada | Cómo verificar          |
|----------------|---------------------|-------------------------|
| Java JDK       | 21 (Eclipse Temurin)| `java --version`        |
| Maven          | 3.9 o superior      | `mvn --version`         |
| Node.js        | 20 LTS o superior   | `node --version`        |
| npm            | 10 o superior       | `npm --version`         |
| Angular CLI    | 17 o superior       | `ng version`            |
| Docker Desktop | reciente            | `docker --version`      |

Si no tienes Angular CLI instalado globalmente:
```bash
npm install -g @angular/cli@17
```

## Arranca en 3 pasos

Abre tres terminales en la raíz del proyecto y ejecuta uno por uno:

```bash
# 1. Levanta MongoDB y Mongo Express en segundo plano
docker compose up -d

# 2. En otra terminal, arranca el backend
cd backend
mvn spring-boot:run

# 3. En otra terminal, arranca el frontend
cd frontend
npm install        # solo la primera vez
ng serve
```

Cuando termine:

- Frontend: <http://localhost:4200>
- Swagger UI: <http://localhost:8080/swagger-ui.html>
- Mongo Express: <http://localhost:8081> (usuario: `admin`, contraseña: `admin`)

Si todo va bien, en la página de inicio verás la lista de **10 empleados de
ejemplo** que el backend insertó automáticamente en la primera ejecución.

## Estructura del proyecto

```
proyecto-practica/
├── docker-compose.yml              ← MongoDB + Mongo Express
├── README.md
│
├── backend/                        ← Spring Boot 3 · Java 21 · Maven
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/example/proyectopractica/
│       │   │   ├── ProyectoPracticaApplication.java  ← punto de entrada
│       │   │   ├── config/         ← CORS, etc.
│       │   │   ├── common/         ← ApiError + GlobalExceptionHandler
│       │   │   └── employees/      ← feature completa (entity, dto,
│       │   │                          mapper, repo, service, controller,
│       │   │                          seeder)
│       │   └── resources/
│       │       └── application.yml ← config (Mongo URI, puertos, logs)
│       └── test/
│           └── java/com/example/proyectopractica/employees/
│               ├── EmployeeServiceTest.java
│               └── EmployeeControllerTest.java
│
└── frontend/                       ← Angular 17 · standalone · signals · SCSS
    ├── package.json
    ├── angular.json
    ├── tsconfig*.json
    └── src/
        ├── index.html
        ├── main.ts
        ├── styles.scss
        └── app/
            ├── app.component.{ts,html,scss}
            ├── app.config.ts       ← providers globales (HttpClient,
            │                          interceptor, API_URL)
            ├── app.routes.ts
            ├── core/               ← código transversal
            │   ├── interceptors/error.interceptor.ts
            │   └── tokens/api-url.token.ts
            ├── shared/             ← (vacío; reservado para reutilizables)
            └── features/
                └── employees/
                    ├── models/employee.model.ts
                    ├── services/employees.service.ts
                    └── pages/employee-list/
                        ├── employee-list.component.{ts,html,scss}
                        └── employee-list.component.spec.ts
```

## Endpoints disponibles

Hoy sólo hay uno:

| Método | Ruta                     | Descripción                  |
|--------|--------------------------|------------------------------|
| GET    | `/api/employees`         | Devuelve todos los empleados |

Documentación interactiva: <http://localhost:8080/swagger-ui.html>

Para ver los datos en MongoDB directamente: <http://localhost:8081> (Mongo Express).

## Tu siguiente reto

Los retos están ordenados de menor a mayor complejidad. La idea es que cojas
uno, lo termines y subas el listón. **No hay solución de referencia a propósito.**

1. **Detalle**
   - Backend: añade `GET /api/employees/{id}` (404 si no existe).
   - Frontend: añade la ruta `/employees/:id` y un componente de detalle.

2. **Alta**
   - Backend: añade `POST /api/employees` con validación (`@Valid`,
     `@NotBlank`, `@Email`). Maneja el conflicto por email duplicado.
   - Frontend: añade `/employees/new` con un Reactive Form.

3. **Edición y borrado**
   - Backend: `PUT /api/employees/{id}` y `DELETE /api/employees/{id}`.
   - Frontend: pantalla de edición y un modal de confirmación para borrar.

4. **Filtros y paginación**
   - Backend: acepta `?firstName=`, `?position=`, `?page=`, `?size=`.
   - Frontend: barra de búsqueda y paginador en la tabla.

5. **Segunda entidad relacionada**
   - Modela `Department` y relaciónalo con `Employee` (decisión: ¿embed o
     referencia? piensa por qué).
   - Endpoints, listados y formularios para la nueva entidad.

6. **Mejoras transversales**
   - Toasts globales en el front cuando el back devuelve un error.
   - Tests adicionales con cobertura razonable.
   - Perfiles `dev`/`prod` en el back con orígenes CORS distintos.
   - Autenticación (Spring Security + JWT, Auth Guards en Angular).

## Comandos útiles

```bash
# Backend
cd backend
mvn spring-boot:run                  # arrancar en modo dev
mvn test                             # ejecutar tests
mvn clean package                    # construir el JAR

# Frontend
cd frontend
ng serve                             # arrancar en modo dev
ng test                              # tests con Karma (modo watch)
ng test --watch=false --browsers=ChromeHeadless   # tests en CI / una pasada
ng build                             # build de producción en dist/

# Infraestructura
docker compose up -d                 # arrancar Mongo + Mongo Express
docker compose down                  # parar (conserva datos)
docker compose down -v               # parar y borrar datos (volverá a sembrar)
```

## Troubleshooting

### El frontend recibe error de CORS

- Verifica que el backend está arrancado en `http://localhost:8080`.
- El perfil activo del back debe ser `dev` (es el default; mira `application.yml`).
- La URL del API en el front se configura en `frontend/src/app/app.config.ts`
  (`{ provide: API_URL, useValue: 'http://localhost:8080/api' }`).

### Puerto en uso (`8080`, `4200`, `27017` u `8081`)

```powershell
# Windows / PowerShell — detectar quién ocupa el puerto 8080
Get-NetTCPConnection -LocalPort 8080
```

```bash
# Linux / macOS
lsof -i :8080
```

### MongoDB no arranca o el back no se conecta

- Asegúrate de que Docker Desktop está corriendo.
- `docker compose ps` debe mostrar `proyecto-practica-mongo` en estado `Up`.
- Si no, mira logs: `docker compose logs mongo`.

### Lombok no compila en el IDE

- IntelliJ: instala el plugin Lombok y activa "Enable annotation processing"
  en *Settings → Build → Compiler → Annotation Processors*.
- VS Code: instala las extensiones *Extension Pack for Java* y *Lombok Annotations Support*.

### `npm install` falla con errores de versión de Node

- Comprueba que tienes Node 20+: `node --version`.
- Si tienes `nvm`, ejecuta `nvm use 20` antes de instalar.

### El seed no inserta los 10 empleados

- Sólo se ejecuta si la colección `employees` está **vacía**. Para reiniciar:
  ```bash
  docker compose down -v && docker compose up -d
  cd backend && mvn spring-boot:run
  ```
  Eso borra el volumen de Mongo y vuelve a sembrar.
