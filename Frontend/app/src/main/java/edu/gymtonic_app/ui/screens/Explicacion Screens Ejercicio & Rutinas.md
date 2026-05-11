![[Pasted image 20260509113814.png]]

### **Pantallas Principales (Screens)**

#### 1. TrainingShellScreen (Marco/Contenedor)
- **Qué es**: Plantilla reutilizable que envuelve todas las pantallas
- **Lo que ves**: Header con título + botón atrás + barra inferior de navegación
- **Flujo**: Recibe contenido como parámetro `content` y lo pinta dentro de su estructura
- **Usado por**: ExerciseDetailScreen, RoutineCatalogScreen, TrainingScreen, CreateRoutineScreen
#### 2. ExerciseDetailScreen 
- **Qué es**: Pantalla de detalle de UN ejercicio individual
- **Lo que ves**:
    - Imagen del ejercicio
    - Nombre + **icono corazón** (favorito/no favorito)
    - Duración
    - Instrucciones paso a paso
- **Cómo funciona**:
    1. Recibe `exerciseId` como parámetro
    2. Carga el ejercicio desde API con `loadSpecificExercise()`
    3. Observa `favoritesSet` → si el ID está en el set, muestra ❤️ rojo, si no ⭘
    4. Al tocar el corazón → `onToggleFavorite()` → guarda/elimina de Room
- **Dentro de**: TrainingShellScreen
- **A dónde va**: Se accede desde `RoutineExerciseRow` (al tocar una fila)
#### 3.RoutineCatalogScreen 
- **Qué es**: Catálogo de ejercicios DE UNA RUTINA
- **Lo que ves**:
    - Titulo de la rutina
    - Lista de ejercicios con imágenes y botones de favorito
- **Cómo funciona**:
    1. Recibe `routineId`
    2. Crea TWO ViewModels:
        - `RoutineCatalogViewModel` → carga datos de la rutina
        - `ExerciseViewModel` → maneja favoritos
    3. Observa `favoritesSet` para mostrar corazones
    4. Renderiza con `RoutineTemplateScreen` (componente más pequeño)
    5. Al tocar ejercicio → abre `ExerciseDetailScreen`
- **Dentro de**: TrainingShellScreen
- **A dónde va**: Se accede desde `TrainingScreen` (al tocar una rutina)

####  4.RoutineTemplateScreen 
- **Qué es**: Componente que RENDERIZA la lista de ejercicios
- **Lo que ves**: Repeats de `RoutineExerciseRow` (filas de ejercicios)
- **Cómo funciona**:
    - Recibe lista de ejercicios + `favoritesSet`
    - Para cada ejercicio → crea un `RoutineExerciseRow`
    - Pasa callbacks para favorito y click
- **Dentro de**: RoutineCatalogScreen
#### 5.RoutineExerciseRow 
- **Qué es**: UNA FILA de ejercicio (componente reutilizable)
- **Lo que ves**:
    - Miniatura imagen izquierda
    - Nombre del ejercicio
    - **Icono corazón** (observable)
    - Badge con sets/reps derecha
- **Cómo funciona**:
    - Recibe `isFavorite: Boolean` (calculado como `favoritesSet.contains(id)`)
    - Muestra ❤️ rojo si `isFavorite == true`, sino ⭘
    - Al tocar corazón → ejecuta callback `onToggleFavorite()`
    - Al tocar fila → ejecuta callback `onClick()` → abre `ExerciseDetailScreen`
- **Dentro de**: RoutineTemplateScreen (se repite N veces)
#### 6. TrainingScreen
- **Qué es**: Pantalla de categorías de entrenamientos
- **Lo que ves**:
    - Título "Categorías disponibles"
    - Listas horizontales de rutinas (por categoría)
    - Botón + para crear nueva rutina
- **Cómo funciona**: NO toca favoritos, solo lista rutinas
- **Dentro de**: TrainingShellScreen (sin barra inferior si no lo pasas)
- **A dónde va**: Al tocar una rutina → abre `RoutineCatalogScreen`
#### 7. CreateRoutineScreen
- **Qué es**: Formulario para crear una nueva rutina
- **Lo que ves**:
    - Campo nombre rutina
    - Campo descripción
    - Botón + Agregar ejercicio
    - Lista de ejercicios añadidos
    - Botón Guardar
- **Cómo funciona**: NO toca favoritos, solo formula datos
- **Dentro de**: TrainingShellScreen

----
## Flujo Completo: Marcar un ejercicio como Favorito

```
Usuario toca ❤️ en RoutineExerciseRow
    ↓
onToggleFavorite() callback → RoutineCatalogScreen
    ↓
ExerciseViewModel.onToggleFavorite(FavoriteExercisePayload)
    ↓
Repository.updateFavWord(exercise) → Room
    ↓
_favoritesSet se actualiza (Flow de Room)
    ↓
Compose recompone: RoutineExerciseRow ve nuevo favoritesSet
    ↓
Icono cambia a ❤️ ROJO (inmediato)
```

