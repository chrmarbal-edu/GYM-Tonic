# Documentación de Códigos de Base de Datos - GymTonic

Este documento detalla el significado de los valores numéricos utilizados en diversas columnas de la base de datos para representar estados, tipos o rangos.

## 1. Usuarios (`Users`)

### `user_role`
- **0**: Usuario estándar (Acceso a la app, seguimiento y grupos).
- **1**: Administrador (Gestión de ejercicios, misiones y usuarios).

### `user_objetive`
- **0**: Mantenimiento (Mantener peso y mejorar salud general).
- **1**: Pérdida de peso (Enfoque en déficit calórico y cardio).
- **2**: Ganancia muscular (Enfoque en hipertrofia y fuerza).
- **3**: Rendimiento / Resistencia (Preparación para competiciones o fondo).

## 2. Ejercicios (`Exercises`)

### `exercise_type`
- **0** : **CARDIO**
- **1** : **PECTORALES**
- **2** : **ESPALDA**
- **3** : **BICEPS / MUÑECAS**
- **4** : **TRICEPS**
- **5** : **CUADRICEPS**
- **6** : **FEMORALES / ISQUIOS**
- **7** : **HOMBROS**
- **8** : **GEMELOS**
- **9** : **ABDOMINALES**
- **10** : **FULL BODY**

## 3. Amistad y Solicitudes (`Frequest`)

### `frequest_status`
- **0**: Pendiente (Solicitud enviada, esperando respuesta).
- **1**: Aceptada (Ahora son amigos).
- **2**: Rechazada (La solicitud fue declinada).

## 4. Misiones (`Missions`)

### `mission_type`
- **0**: Diaria (Se reinicia cada 24 horas).
- **1**: Semanal (Objetivos a cumplir en 7 días).
- **2**: Mensual (Retos de larga duración).

## 5. Grupos (`Group_x_user`)

### `Group_x_user_range`
- **0**: Miembro (Participante del grupo).
- **1**: Moderador (Capacidad para gestionar miembros y rutinas).
- **2**: Líder (Creador del grupo con control total).

## 6. Otros Estados

### `user_x_mission_completed` (BIT)
- **0**: No completada.
- **1**: Completada.