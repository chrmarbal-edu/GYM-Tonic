/* ============================================================
   PARTE 2 - Ejecutar conectado como GYMTONIC
============================================================ */

USE GymTonic;
GO

/* ============================================================
   TABLAS PRINCIPALES
============================================================ */

CREATE TABLE dbo.Users (
    user_id        INT IDENTITY(1,1) NOT NULL,
    user_username  NVARCHAR(255)     NOT NULL,
    user_name      NVARCHAR(255)     NOT NULL,
    user_password  NVARCHAR(255)     NOT NULL,
    user_birthdate DATE              NOT NULL,
    user_email     NVARCHAR(255)     NOT NULL,
    user_height    FLOAT             NOT NULL,
    user_weight    FLOAT             NOT NULL,
    user_objetive  INT               NOT NULL,
    user_points    INT               NULL,
    user_role      INT               NOT NULL,
    CONSTRAINT PK_Users          PRIMARY KEY (user_id),
    CONSTRAINT UQ_Users_username UNIQUE      (user_username),
    CONSTRAINT UQ_Users_email    UNIQUE      (user_email)
);
GO

CREATE TABLE dbo.Exercises (
    exercise_id          INT IDENTITY(1,1) NOT NULL,
    exercise_name        NVARCHAR(255)     NOT NULL,
    exercise_description NVARCHAR(MAX)     NOT NULL,
    exercise_type        INT               NOT NULL,
    exercise_video       NVARCHAR(500)     NULL,
    exercise_image       NVARCHAR(500)     NULL,
    CONSTRAINT PK_Exercises PRIMARY KEY (exercise_id)
);
GO

CREATE TABLE dbo.Routines (
    routine_id   INT IDENTITY(1,1) NOT NULL,
    routine_name NVARCHAR(255)     NOT NULL,
    CONSTRAINT PK_Routines PRIMARY KEY (routine_id)
);
GO

CREATE TABLE dbo.Missions (
    mission_id       INT IDENTITY(1,1) NOT NULL,
    mission_name     NVARCHAR(255)     NOT NULL,
    mission_type     INT               NOT NULL,
    mission_points   INT               NOT NULL,
    mission_objetive INT               NOT NULL,
    CONSTRAINT PK_Missions PRIMARY KEY (mission_id)
);
GO

CREATE TABLE dbo.Groups (
    group_id   INT IDENTITY(1,1) NOT NULL,
    group_name NVARCHAR(255)     NOT NULL,
    CONSTRAINT PK_Groups PRIMARY KEY (group_id)
);
GO

/* ============================================================
   TABLAS RELACIONALES
============================================================ */

CREATE TABLE dbo.Routine_X_Exercise (
    routine_x_exercise_id INT IDENTITY(1,1) NOT NULL,
    routine_x_exercise_routineid INT NOT NULL,
    routine_x_exercise_exerciseid INT NOT NULL,
    CONSTRAINT PK_Routine_X_Exercise PRIMARY KEY (routine_x_exercise_id),
    FOREIGN KEY (routine_x_exercise_routineid)  REFERENCES dbo.Routines(routine_id),
    FOREIGN KEY (routine_x_exercise_exerciseid) REFERENCES dbo.Exercises(exercise_id)
);
GO

CREATE TABLE dbo.User_X_Routine (
    user_x_routine_id INT IDENTITY(1,1) NOT NULL,
    user_x_routine_userid INT NOT NULL,
    user_x_routine_routineid INT NOT NULL,
    CONSTRAINT PK_User_X_Routine PRIMARY KEY (user_x_routine_id),
    FOREIGN KEY (user_x_routine_userid)  REFERENCES dbo.Users(user_id),
    FOREIGN KEY (user_x_routine_routineid) REFERENCES dbo.Routines(routine_id)
);
GO

CREATE TABLE dbo.Group_x_user (
    Group_x_user_id INT IDENTITY(1,1) NOT NULL,
    Group_x_user_groupid INT NOT NULL,
    Group_x_user_userid INT NOT NULL,
    Group_x_user_range INT NOT NULL,
    CONSTRAINT PK_Group_x_user PRIMARY KEY (Group_x_user_id),
    FOREIGN KEY (Group_x_user_groupid) REFERENCES dbo.Groups(group_id),
    FOREIGN KEY (Group_x_user_userid)  REFERENCES dbo.Users(user_id)
);
GO

CREATE TABLE dbo.Friends (
    friend_id      INT IDENTITY(1,1) NOT NULL,
    friend_userid1 INT NOT NULL,
    friend_userid2 INT NOT NULL,
    CONSTRAINT PK_Friends PRIMARY KEY (friend_id),
    FOREIGN KEY (friend_userid1) REFERENCES dbo.Users(user_id),
    FOREIGN KEY (friend_userid2) REFERENCES dbo.Users(user_id)
);
GO

CREATE TABLE dbo.Frequest (
    frequest_id       INT IDENTITY(1,1) NOT NULL,
    frequest_sender   INT NOT NULL,
    frequest_receiver INT NOT NULL,
    frequest_status   INT NOT NULL,
    CONSTRAINT PK_Frequest PRIMARY KEY (frequest_id),
    FOREIGN KEY (frequest_sender)   REFERENCES dbo.Users(user_id),
    FOREIGN KEY (frequest_receiver) REFERENCES dbo.Users(user_id)
);
GO

CREATE TABLE dbo.User_X_Mission (
    user_x_mission_id INT IDENTITY(1,1) NOT NULL,
    user_x_mission_userid INT NOT NULL,
    user_x_mission_missionid INT NOT NULL,
    user_x_mission_expiration DATE NOT NULL,
    CONSTRAINT PK_User_X_Mission PRIMARY KEY (user_x_mission_id),
    FOREIGN KEY (user_x_mission_userid)    REFERENCES dbo.Users(user_id),
    FOREIGN KEY (user_x_mission_missionid) REFERENCES dbo.Missions(mission_id)
);
GO

/* ============================================================
   ADMIN POR DEFECTO
============================================================ */
INSERT INTO dbo.Users
    (user_username, user_name, user_password, user_birthdate,
     user_email, user_height, user_weight, user_objetive, user_points, user_role)
VALUES
    ('admin', 'Administrador',
     '$2b$12$bvvrPTWlj.GC8RDiz3ZtRezksJJVtvmB9GVzJBQUBQfc6ZUxfNExG',
     '1990-01-01', 'admin@gymtonic.com', 0, 0, 0, 0, 1);
GO

/* ============================================================
   SEEDS DE PRUEBA
============================================================ */

-- Usuarios
-- user_role: 0=normal 1=admin | user_objective: 0=perder peso 1=ganar músculo 2=resistencia
INSERT INTO dbo.Users
    (user_username, user_name, user_password, user_birthdate,
     user_email, user_height, user_weight, user_objetive, user_points, user_role)
VALUES
    ('carlos_g', 'Carlos García',   '$2b$12$bvvrPTWlj.GC8RDiz3ZtRezksJJVtvmB9GVzJBQUBQfc6ZUxfNExG', '1995-03-15', 'carlos@example.com', 178, 80, 1, 120, 0),
    ('laura_m',  'Laura Martínez',  '$2b$12$bvvrPTWlj.GC8RDiz3ZtRezksJJVtvmB9GVzJBQUBQfc6ZUxfNExG', '1998-07-22', 'laura@example.com',  165, 60, 0, 340, 0),
    ('miguel_r', 'Miguel Romero',   '$2b$12$bvvrPTWlj.GC8RDiz3ZtRezksJJVtvmB9GVzJBQUBQfc6ZUxfNExG', '1992-11-05', 'miguel@example.com', 182, 90, 1, 210, 0),
    ('sofia_p',  'Sofía Pérez',     '$2b$12$bvvrPTWlj.GC8RDiz3ZtRezksJJVtvmB9GVzJBQUBQfc6ZUxfNExG', '2000-01-30', 'sofia@example.com',  160, 55, 2, 480, 0),
    ('javier_l', 'Javier López',    '$2b$12$bvvrPTWlj.GC8RDiz3ZtRezksJJVtvmB9GVzJBQUBQfc6ZUxfNExG', '1988-09-18', 'javier@example.com', 175, 75, 0,  90, 0),
    ('ana_s',    'Ana Sánchez',     '$2b$12$bvvrPTWlj.GC8RDiz3ZtRezksJJVtvmB9GVzJBQUBQfc6ZUxfNExG', '1997-05-12', 'ana@example.com',    162, 58, 2, 560, 0),
    ('david_f',  'David Fernández', '$2b$12$bvvrPTWlj.GC8RDiz3ZtRezksJJVtvmB9GVzJBQUBQfc6ZUxfNExG', '1993-08-25', 'david@example.com',  180, 85, 1, 300, 0),
    ('elena_t',  'Elena Torres',    '$2b$12$bvvrPTWlj.GC8RDiz3ZtRezksJJVtvmB9GVzJBQUBQfc6ZUxfNExG', '2001-04-03', 'elena@example.com',  168, 62, 0, 150, 0);
GO

-- Ejercicios (exercise_type: 0=fuerza 1=cardio 2=flexibilidad)
INSERT INTO dbo.Exercises
    (exercise_name, exercise_description, exercise_type, exercise_video, exercise_image)
VALUES
    ('Sentadilla',           'Ejercicio compuesto de piernas. Baja hasta que los muslos queden paralelos al suelo.',         0, 'https://youtube.com/watch?v=squat',     'squat.jpg'),
    ('Press de banca',       'Ejercicio de empuje para pecho. Tumbado en banco, baja la barra al pecho y empuja.',           0, 'https://youtube.com/watch?v=bench',     'bench.jpg'),
    ('Peso muerto',          'Levanta la barra desde el suelo manteniendo la espalda recta.',                               0, 'https://youtube.com/watch?v=deadlift',  'deadlift.jpg'),
    ('Dominadas',            'Cuelga de una barra y tira del cuerpo hasta que la barbilla supere la barra.',                 0, 'https://youtube.com/watch?v=pullup',     'pullup.jpg'),
    ('Press militar',        'De pie o sentado, empuja la barra por encima de la cabeza.',                                   0, 'https://youtube.com/watch?v=ohpress',    'ohpress.jpg'),
    ('Remo con barra',       'Con el torso inclinado, tira de la barra hacia el abdomen.',                                   0, 'https://youtube.com/watch?v=row',       'row.jpg'),
    ('Carrera continua',     'Mantén un ritmo constante durante el tiempo indicado, controlando la respiración.',            1, 'https://youtube.com/watch?v=running',   'running.jpg'),
    ('Saltar a la comba',     'Salta alternando pies o con ambos pies juntos a ritmo constante.',                             1, 'https://youtube.com/watch?v=jumprope',  'jumprope.jpg'),
    ('Burpees',               'Combina sentadilla, plancha, flexión y salto. Ejercicio de alta intensidad.',                  1, 'https://youtube.com/watch?v=burpee',     'burpee.jpg'),
    ('Bicicleta estática',   'Pedalea a intensidad moderada-alta durante el tiempo indicado.',                               1, 'https://youtube.com/watch?v=bike',       'bike.jpg'),
    ('Estiramiento isquios', 'Sentado en el suelo, estira las piernas y alcanza la punta de los pies.',                     2, 'https://youtube.com/watch?v=hamstring', 'hamstring.jpg'),
    ('Yoga - Saludo al sol', 'Secuencia de posturas que trabaja flexibilidad y equilibrio de todo el cuerpo.',               2, 'https://youtube.com/watch?v=sunsalute','sunsalute.jpg');
GO

-- Rutinas
INSERT INTO dbo.Routines (routine_name)
VALUES
    ('Full Body Principiante'),
    ('Tren Superior Avanzado'),
    ('Cardio Quema Grasa'),
    ('Piernas y Glúteos'),
    ('Flexibilidad y Movilidad');
GO

-- Ejercicios por rutina
INSERT INTO dbo.Routine_X_Exercise (routine_x_exercise_routineid, routine_x_exercise_exerciseid)
VALUES
    (1,1),(1,2),(1,6),
    (2,2),(2,4),(2,5),(2,6),
    (3,7),(3,8),(3,9),(3,10),
    (4,1),(4,3),
    (5,11),(5,12);
GO

-- Rutinas asignadas a usuarios (IDs 2-9)
INSERT INTO dbo.User_X_Routine (user_x_routine_userid, user_x_routine_routineid)
VALUES
    (2,1),(2,3),
    (3,3),(3,5),
    (4,2),(4,4),
    (5,3),(5,5),
    (6,1),
    (7,3),(7,5),
    (8,2),(8,4),
    (9,1),(9,3);
GO

-- Misiones (mission_type: 0=diaria 1=semanal 2=mensual)
INSERT INTO dbo.Missions (mission_name, mission_type, mission_points, mission_objetive)
VALUES
    ('Primera sesión',            0,  10,     1),
    ('Racha de 3 días',           0,  30,     3),
    ('Completa 5 entrenamientos', 1,  75,     5),
    ('10 km en una semana',       1, 100,    10),
    ('30 sesiones en un mes',     2, 500,    30),
    ('Quema 10 000 kcal',         2, 300, 10000),
    ('Primer ejercicio de fuerza',0,  10,     1),
    ('Semana de cardio completa', 1,  80,     7);
GO

-- Grupos
INSERT INTO dbo.Groups (group_name)
VALUES
    ('Equipo Alpha'),
    ('Reto Verano 2025'),
    ('Maratonianos'),
    ('Powerlifters');
GO

-- Usuarios en grupos (range: 0=miembro 1=moderador 2=líder)
INSERT INTO dbo.Group_x_user (Group_x_user_groupid, Group_x_user_userid, Group_x_user_range)
VALUES
    (1,2,2),(1,3,0),(1,4,0),(1,8,1),
    (2,5,2),(2,6,0),(2,7,1),(2,9,0),
    (3,3,2),(3,5,0),(3,7,0),
    (4,4,2),(4,8,1),(4,2,0);
GO

-- Amistades
INSERT INTO dbo.Friends (friend_userid1, friend_userid2)
VALUES
    (2,3),(2,4),(3,5),(4,8),(5,7),(6,9);
GO

-- Solicitudes de amistad (status: 0=pendiente 1=aceptada 2=rechazada)
INSERT INTO dbo.Frequest (frequest_sender, frequest_receiver, frequest_status)
VALUES
    (6,2,0),
    (7,4,0),
    (9,3,1),
    (8,5,2);
GO

-- Misiones asignadas a usuarios
INSERT INTO dbo.User_X_Mission (user_x_mission_userid, user_x_mission_missionid, user_x_mission_expiration)
VALUES
    (2,1,'2025-12-31'),(2,3,'2025-07-07'),
    (3,2,'2025-12-31'),(3,4,'2025-07-07'),
    (4,5,'2025-07-31'),(4,7,'2025-12-31'),
    (5,3,'2025-07-07'),(5,8,'2025-07-07'),
    (6,1,'2025-12-31'),
    (7,2,'2025-12-31'),(7,6,'2025-07-31'),
    (8,5,'2025-07-31'),(8,7,'2025-12-31'),
    (9,1,'2025-12-31'),(9,4,'2025-07-07');
GO

PRINT 'GymTonic - BD creada y poblada correctamente.';
GO