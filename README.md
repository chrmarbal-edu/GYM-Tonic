# GYM-Tonic

Documentación técnica integral del proyecto.

Fecha de actualización de esta documentación: **2026-04-28**

## 1) Resumen del proyecto

GYM-Tonic está dividido en dos grandes módulos:

- `Backend/`: API REST en Node.js + Express + SQL Server (`mssql`).
- `Frontend/`: app Android en Kotlin + Jetpack Compose + Retrofit + Room.

La arquitectura funcional actual del frontend usa enfoque **remote-first con fallback local/mock** en varios casos (especialmente rutinas, training, retos semanales y grupos).

---

## 2) Directiva de endpoints acordada (estado actual)

Directiva acordada para consumo frontend:

1. `POST users/login`
2. `POST users`
3. `GET users/logout`
4. `GET routines/routines`
5. `GET routines/routine/{id}`

Estado en código (cumplimiento):

- Backend expone rutas `users` y `routines` bajo `/api/${API_VERSION}`.
- Frontend consume exactamente esos paths en `ApiService`.
- Frontend inyecta `Authorization: Bearer <token>` en endpoints protegidos y excluye login/register.
- `RegisterResponse` soporta dos formas de respuesta (`user` y `data`).
- DTOs de rutinas adaptados a `snake_case` (`routine_id`, `routine_name`, `exercise_id`, `image_key`).

Nota:

- Aunque esta directiva está integrada, existen otras áreas del backend con deuda técnica y rutas inconsistentes fuera de esta directiva (detalladas en sección de riesgos).

---

## 3) Estructura global de paquetes/carpetas

```text
GYM-Tonic/
├─ Backend/
│  ├─ index.js
│  ├─ controllers/
│  ├─ routes/
│  ├─ middlewares/
│  ├─ models/
│  ├─ utils/
│  └─ scripts/
└─ Frontend/
   └─ app/src/main/java/edu/gymtonic_app/
      ├─ MainActivity.kt
      ├─ data/
      │  ├─ remote/
      │  │  ├─ services/
      │  │  ├─ datasource/
      │  │  └─ model/
      │  ├─ repository/
      │  └─ local/
      │     ├─ GymTonicDatabase.kt
      │     ├─ dao/
      │     ├─ localModel/
      │     └─ datasource/local/
      └─ ui/
         ├─ navigation/
         ├─ viewmodel/
         ├─ screens/
         ├─ components/
         └─ theme/
```

---

## 4) Backend documentado (módulo por módulo)

## 4.1 Archivo raíz

### `Backend/index.js`

Responsabilidad:

- Configura Express.
- Carga middlewares globales (`urlencoded`, `json`, `method-override`, static).
- Monta rutas por versión de API.
- Registra `errorHandler`.
- Arranca servidor.

Montajes relevantes:

- `/api/${API_VERSION}/users`
- `/api/${API_VERSION}/missions`
- `/api/${API_VERSION}/routines`
- `/api/${API_VERSION}/groups`
- `/api/${API_VERSION}/exercises`
- `friendsRoutes` está montado actualmente también bajo `/api/${API_VERSION}/exercises` (inconsistencia).

## 4.2 Middlewares

### `Backend/middlewares/jwt.mw.js`

Funciones:

- `authenticate(req,res,next)`: valida JWT en `Authorization: Bearer` o `query.token`; adjunta `req.userLogued`.
- `createJWT(req,res,next,userData)`: genera token con expiración de 2 días.

### `Backend/middlewares/rutasProtegidas.mw.js`

Funciones:

- `requireAdmin(req,res,next)`: autoriza solo `user_role == 1`.

### `Backend/middlewares/errorHandler.mw.js`

Funciones:

- `errorHandler(err,req,res,next)`: normaliza error, loguea y responde status + mensaje.

## 4.3 Utilidades

### `Backend/utils/AppError.js`

Clase:

- `AppError extends Error`: encapsula `{ error: message }` + `status`.

### `Backend/utils/bcrypt.js`

Funciones:

- `hashPassword`: hash bcrypt (cost 12).
- `compareLogin`: compara password plana vs hash.

### `Backend/utils/mssql.config.js`

Responsabilidad:

- Exporta configuración SQL Server desde variables de entorno.

### `Backend/utils/logger.js`

Responsabilidad:

- Configuración log4js para access/error logs en `Backend/temp/`.

## 4.4 Rutas y controladores (capas HTTP)

### `Backend/routes/users.routes.js` + `Backend/controllers/users.controller.js`

Endpoints:

1. `GET /users/` (admin)
2. `POST /users/` (register)
3. `PATCH /users/:id`
4. `DELETE /users/:id`
5. `POST /users/login`
6. `GET /users/logout`
7. `GET /users/:id` (admin)

Responsabilidad del controlador:

- CRUD de usuarios.
- Validaciones de contraseña en register/update.
- Login + emisión JWT.
- Logout lógico (respuesta de sesión cerrada).

### `Backend/routes/routines.routes.js` + `Backend/controllers/routines.controller.js`

Endpoints:

1. `GET /routines/routines`
2. `GET /routines/routine/:id`
3. `PATCH /routines/routine/:id` (admin)
4. `DELETE /routines/routine/:id` (admin)
5. `POST /routines/routine/new` (admin)

Responsabilidad del controlador:

- Listado, detalle y mantenimiento de rutinas.
- Uso de `routines.model.js`.

### `Backend/routes/missions.routes.js` + `Backend/controllers/missions.controller.js`

Endpoints:

1. `GET /missions/`
2. `GET /missions/:id`
3. `PATCH /missions/:id` (admin)
4. `DELETE /missions/:id` (admin)
5. `POST /missions/new` (admin)

Responsabilidad:

- Operaciones CRUD de misiones.

### `Backend/routes/groups.routes.js` + `Backend/controllers/groups.controller.js`

Endpoints:

1. `GET /groups/`
2. `GET /groups/:id`
3. `PATCH /groups/:id` (admin)
4. `DELETE /groups/:id` (admin)
5. `POST /groups/new` (admin)

Responsabilidad:

- Operaciones CRUD de grupos.

### `Backend/routes/exercises.routes.js` + `Backend/controllers/exercises.controller.js`

Endpoints:

1. `GET /exercises/`
2. `POST /exercises/` (admin)
3. `PATCH /exercises/:id` (admin)
4. `DELETE /exercises/:id` (admin)
5. `GET /exercises/:id`

Responsabilidad:

- CRUD de ejercicios y validaciones básicas.

### `Backend/routes/friends.routes.js` + `Backend/controllers/friends.controller.js`

Endpoints declarados:

1. `GET /friends/` (admin)
2. `POST /friends/` (admin)
3. `DELETE /friends/:id`
4. `GET /friends/user/:userId`
5. `GET /friends/:id` (admin)

Estado real:

- El router existe, pero en `index.js` está montado bajo prefijo `exercises`, no `friends`.
- Hay inconsistencias de orden de middlewares y de parámetro (`userId` vs `id`) en controlador.

### `Backend/controllers/friendRequests.controller.js`

Estado:

- Archivo presente pero **sin implementación funcional**.

## 4.5 Modelos de datos (`Backend/models`)

Modelos principales activos:

- `users.model.js`: tabla `Users`.
- `routines.model.js`: tabla `Routines`.
- `missions.model.js`: tabla `Missions`.
- `groups.model.js`: tabla `Groups`.
- `exercises.model.js`: tabla `Exercises`.
- `friends.model.js`: tabla `Friends`.

Modelos de relaciones/auxiliares:

- `friendRequests.model.js`
- `groupUsers.model.js`
- `routineExercises.model.js`
- `userMissions.model.js`
- `userRoutines.model.js`

Observaciones de funcionamiento:

- En varios modelos hay métodos `create` incompletos (se construye SQL pero no se ejecuta query final).
- En varios modelos auxiliares se exporta `routine` en vez de la entidad real.
- Existen referencias a variables no definidas (`id`) en create de distintos modelos.

## 4.6 Scripts SQL

- `Backend/scripts/GymTonic_Parte1.sql`
- `Backend/scripts/GymTonic_Parte2.sql`

Responsabilidad:

- Definición/carga de estructura de base de datos del proyecto.

---

## 5) Frontend documentado (paquete por paquete)

## 5.1 Arranque de app

### `MainActivity.kt`

Responsabilidad:

- Entry point de la app Compose.
- Inicializa `SessionManager` y lo inyecta en `RetrofitClient.setSessionManager(...)`.
- Renderiza navegación principal.

## 5.2 Capa `data.remote.services`

### `ReterofitClient.kt`

Clases:

- `object RetrofitClient`
- `interface ApiService`

Funcionamiento:

- Construye Retrofit con `BASE_URL` desde `BuildConfig`.
- Añade interceptor de auth:
  - Inyecta `Authorization: Bearer <token>` cuando hay token.
  - Excluye `POST users/login` y `POST users`.
- Define endpoints API consumidos por frontend:
  - `POST users/login`
  - `POST users`
  - `GET users/logout`
  - `GET routines/routines`
  - `GET routines/routine/{routineId}`

### `SessionManager.kt` (en `data/remote/services`)

Estado:

- Archivo **placeholder vacío** (solo package).

### `GymTonicApiService.kt`

Estado:

- Archivo **placeholder vacío** (solo package).

## 5.3 Capa `data.remote.datasource`

### `AuthRemoteDataSource.kt`

Clase:

- `AuthRemoteDataSource`

Funcionamiento:

- Llama a login/register/logout vía Retrofit.
- Maneja éxito/error HTTP y lanza excepciones con logs.

### `RoutineRemoteDataSource.kt`

Clase:

- `RoutineRemoteDataSource`

Funcionamiento:

- Mantiene catálogo mock local (`mockRoutineDetails`).
- `getRoutinesFromApi()` y `getRoutineByIdFromApi()` hacen remote-first con fallback a mock.

### `TrainingRemoteDataSource.kt`

Clase:

- `TrainingRemoteDataSource`

Funcionamiento:

- Devuelve categorías de entrenamiento mock.

### `WeekRemoteDataSource.kt`

Clase:

- `WeekRemoteDataSource`

Funcionamiento:

- Devuelve objetivos semanales y calendario mock.

### `ExerciseRemoteDataSource.kt`

Clase:

- `ExerciseRemoteDataSource`

Funcionamiento:

- Devuelve detalle de ejercicios mock.

### `GroupRemoteDataSource.kt`

Clase:

- `GroupRemoteDataSource`

Funcionamiento:

- Devuelve grupos mock (lista reducida si no hay sesión).

## 5.4 Capa `data.remote.model`

## 5.4.1 Auth

### `LoginUser.kt`

Data classes:

- `LoginRequest`
- `LoginResponse`
- `Data`

Uso:

- Modelo principal usado hoy para login real.

### `SessionManager.kt` (en `data/remote/model/auth`)

Clase:

- `SessionManager`

Funcionamiento:

- DataStore de sesión (`token`, `userId`, `username`, `email`, `role`).
- API de guardado, lectura (`sessionFlow`) y limpieza.

### `LoginRequest.kt` y `LoginResponse.kt`

Estado:

- Archivos **placeholder vacíos** (solo package).

## 5.4.2 User

### `UserDto.kt`

Data classes:

- `UserDto`
- `RegisterRequest`
- `RegisterResponse`

Detalle funcional:

- `RegisterResponse` soporta forma dual:
  - `{ user: ... }`
  - `{ data: ... }`
- Helper:
  - `resolvedUser()`

### `UserMissionDto.kt` y `UserRoutineDto.kt`

Data classes de relaciones usuario-misión y usuario-rutina.

## 5.4.3 Routine

### `RoutineDto.kt`

Data classes:

- `RoutineDto`
- `RoutineExerciseDto`
- `RoutineDetailDto`

Detalle:

- Mapeo `snake_case` para backend SQL (`routine_id`, `routine_name`, etc.).
- `exercises` nullable.
- Helper:
  - `safeExercises()`

### `RoutineDetailData.kt`

Data classes de modelo interno UI:

- `RoutineExerciseData`
- `RoutineDetailData`

## 5.4.4 Resto de modelos remotos

- `exercise/ExerciseDetailDto.kt`
- `group/GroupDto.kt`, `GroupUserDto.kt`
- `social/FriendDto.kt`, `FrequestDto.kt`
- `training/TrainingCatalogDto.kt`
- `week/WeeklyGoalDto.kt`, `WeeklyCalendarDayDto.kt`

Todos son data classes de transporte para UI/repositorios.

## 5.5 Capa `data.repository`

### `AuthRepository.kt`

Clase:

- `AuthRepository`

Rol:

- Fachada de login/register/logout sobre `AuthRemoteDataSource`.

### `RoutineRepository.kt`

Clase:

- `RoutineRepository`

Rol:

- Mapeo DTO -> modelo de UI.
- Estrategia remote-first con fallback.
- Generación de IDs fallback de ejercicios.

### `ExerciseRepository.kt`

Data class y clase:

- `ExerciseDetailData`
- `ExerciseRepository`

Rol:

- Mapea ejercicio remoto y aplica fallback inteligente por ID.

### `GroupRepository.kt`

Data class y clase:

- `GroupSummaryData`
- `GroupRepository`

Rol:

- Mapea grupos remotos a formato de presentación.

### `TrainingRepository.kt`

Clase:

- `TrainingRepository`

Rol:

- Exponer categorías de entrenamiento.

### `WeekRepository.kt`

Clase:

- `WeekRepository`

Rol:

- Exponer objetivos y calendario semanales.

### `UserRepository.kt` y `SocialRepository.kt`

Estado:

- Archivos **placeholder vacíos** (solo package).

## 5.6 Capa `data.local`

### `GymTonicDatabase.kt`

Clase:

- `GymTonicDatabase : RoomDatabase`

Rol:

- Define entidades Room.
- Expone DAOs.
- Singleton de base de datos local.

## 5.6.1 Entidades (`data/local/localModel`)

Data classes:

1. `UserEntity`
2. `ExerciseEntity`
3. `RoutineEntity`
4. `MissionEntity`
5. `GroupEntity`
6. `RoutineExerciseEntity`
7. `UserRoutineEntity`
8. `GroupUserEntity`
9. `FriendEntity`
10. `FrequestEntity`
11. `UserMissionEntity`

Rol:

- Representación local de tablas y relaciones para Room.

## 5.6.2 DAOs (`data/local/dao`)

Interfaces:

1. `UserDao`
2. `ExerciseDao`
3. `RoutineDao`
4. `MissionDao`
5. `GroupDao`
6. `RoutineExerciseDao`
7. `UserRoutineDao`
8. `GroupUserDao`
9. `FriendDao`
10. `FrequestDao`
11. `UserMissionDao`

Funcionalidad común:

- Consultas `Flow`.
- CRUD local.
- Búsquedas, conteos, relaciones cruzadas.

Estado especial:

- `UserMissionDao` está prácticamente vacío/incompleto.

## 5.6.3 Local datasources (`data/local/datasource/local/*`)

Archivos:

1. `user/UserLocalDataSource.kt`
2. `social/SocialLocalDataSource.kt`
3. `routine/RoutineLocalDataSource.kt`
4. `mission/MissionLocalDataSource.kt`
5. `group/GroupLocalDataSource.kt`
6. `exercise/ExerciseLocalDataSource.kt`

Estado:

- Son **placeholders con comentarios**, sin implementación concreta.

## 5.7 Capa `ui.navigation`

### `Routes.kt`

Objeto:

- `Routes`

Rol:

- Centraliza rutas estáticas y dinámicas (`routine/{routineId}`, `exercise/{exerciseId}`).

### `Navigation.kt`

Composable:

- `Navigation(...)`

Rol:

- Define NavHost y flujo de pantallas.
- Determina start destination en base a sesión DataStore.
- Orquesta logout y navegación bottom/tab.

## 5.8 Capa `ui.viewmodel`

Clases y estados:

1. `LoginViewModel` + `LoginState`
2. `RegisterViewModel` + `RegisterState`
3. `HomeViewModel`
4. `RoutineCatalogViewModel` + `RoutineCatalogUiState` + modelos UI
5. `TrainingScreenViewModel` + `TrainingUiState` + modelos UI
6. `WeekChallengesViewModel` + `WeekChallengesUiState` + modelos UI
7. `ProfileViewModel` + `ProfileUiState` + modelos UI
8. `ExerciseViewModel` + `ExerciseUiState` + modelo UI
9. `GroupViewModel` (placeholder vacío)

Patrón común:

- `StateFlow` + `viewModelScope`.
- Manejo de `loading/success/error`.
- Conversión de DTOs/repositorios a modelos de UI.

## 5.9 Capa `ui.screens`

Módulos Compose (por feature):

- Login:
  - `LoginScreen.kt`
  - `LoginFormScreen.kt`
- Register:
  - `RegisterScreen.kt`
  - `RegisterScreen2.kt`
- Home:
  - `MainViewScreen.kt`
- Training/Exercise:
  - `TrainingShellScreen.kt`
  - `TrainingScreen.kt`
  - `ExerciseDetailScreen.kt`
- Routines:
  - `RoutineCatalogScreens.kt`
  - `RoutineTemplate.kt`
  - `CreateRoutineScreen.kt`
- Missions:
  - `WeekChallengesScreen.kt`
- Profile:
  - `ProfileScreen.kt`

## 5.10 `ui.components` y `ui.theme`

- `BottomNavBar.kt`: navegación inferior y selección de tabs.
- `Color.kt`, `Theme.kt`, `Type.kt`: sistema visual Compose (colores, tipografía, tema).

---

## 6) Endpoints operativos documentados para frontend

Base URL:

- `http://10.0.2.2:3010/api/v1/` (por defecto en build config).

Endpoints actualmente integrados en frontend:

1. `POST users/login`
2. `POST users`
3. `GET users/logout`
4. `GET routines/routines`
5. `GET routines/routine/{routineId}`

Reglas de auth actuales:

- `Authorization` se inyecta automáticamente si hay token.
- Login/register quedan excluidos de auth header.

---

## 7) Riesgos y deuda técnica relevante (estado actual)

## 7.1 Backend

1. `friendsRoutes` montado con prefijo `exercises` en `index.js`.
2. `friendRequests.controller.js` sin implementación.
3. Varios modelos auxiliares con `module.exports` incorrecto.
4. Métodos `create` incompletos en varios modelos.
5. Referencias a variables no definidas en algunos creates.
6. Inconsistencias de naming entre algunos controladores y modelos.

## 7.2 Frontend

1. Archivos duplicados/placeholder vacíos en auth/services/repositories.
2. Fuerte dependencia de datos mock/fallback en varias features.
3. `GroupViewModel` sin implementación.
4. Parte de la capa local datasource está declarada pero no implementada.

---

## 8) Convenciones y notas para desarrollo

1. Mantener contratos backend `snake_case` en DTOs de integración.
2. Centralizar nuevos endpoints en `ReterofitClient.kt`.
3. Mantener `SessionManager` de `data/remote/model/auth` como fuente de verdad de sesión.
4. Si se activa más backend real, migrar gradualmente mocks de `RemoteDataSource` a respuestas API.

---

## 9) Índice rápido de clases y objetos (inventario)

## 9.1 Backend

- Clase:
  - `AppError`
- Módulos de middleware:
  - `jwt.mw`, `rutasProtegidas.mw`, `errorHandler.mw`
- Módulos de ruta:
  - `users.routes`, `routines.routes`, `missions.routes`, `groups.routes`, `exercises.routes`, `friends.routes`
- Módulos de controlador:
  - `users`, `routines`, `missions`, `groups`, `exercises`, `friends`, `friendRequests` (vacío)
- Módulos de modelo:
  - `users`, `routines`, `missions`, `groups`, `exercises`, `friends`, `friendRequests`, `groupUsers`, `routineExercises`, `userMissions`, `userRoutines`

## 9.2 Frontend

- Clases/objetos de arranque y red:
  - `MainActivity`
  - `RetrofitClient`, `ApiService`
  - `SessionManager` (auth model)
- Repositorios:
  - `AuthRepository`, `RoutineRepository`, `ExerciseRepository`, `GroupRepository`, `TrainingRepository`, `WeekRepository`
- ViewModels:
  - `LoginViewModel`, `RegisterViewModel`, `HomeViewModel`, `RoutineCatalogViewModel`, `TrainingScreenViewModel`, `WeekChallengesViewModel`, `ProfileViewModel`, `ExerciseViewModel`
- Base local:
  - `GymTonicDatabase`
  - 11 entidades Room
  - 11 DAOs
- Modelos remotos:
  - auth, user, routine, group, social, exercise, training, week

---

## 10) Estado de documentación

Este README describe la implementación real actual del repositorio, incluyendo:

1. Estructura de paquetes.
2. Funcionamiento por clase/módulo.
3. Endpoints integrados y directiva vigente.
4. Riesgos y placeholders existentes.

Si se modifican rutas, modelos o capas de datos, actualizar esta guía en el mismo PR.
