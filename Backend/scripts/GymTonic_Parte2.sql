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
    user_picture   NVARCHAR(500)     DEFAULT 'images/users/default/user.jpg' NOT NULL,
    user_height    FLOAT             NOT NULL,
    user_weight    FLOAT             NOT NULL,
    user_objective  INT               NOT NULL,
    user_points    INT               NULL,
    user_role      INT               NOT NULL,
    user_oauth     NVARCHAR(50)      NULL,
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
    routine_image NVARCHAR(500)    NULL,
    routine_creator_id INT         NULL,
    routine_is_personal_routine INT NOT NULL CONSTRAINT DF_Routines_is_personal DEFAULT (0),
    routine_is_group_routine INT NOT NULL CONSTRAINT DF_Routines_is_group_routine DEFAULT (0),
    routine_groupid INT NULL,
    CONSTRAINT PK_Routines PRIMARY KEY (routine_id),
    CONSTRAINT FK_Routines_Creator FOREIGN KEY (routine_creator_id) REFERENCES dbo.Users(user_id) ON DELETE SET NULL
);
GO

CREATE TABLE dbo.Missions (
    mission_id       INT IDENTITY(1,1) NOT NULL,
    mission_name     NVARCHAR(255)     NOT NULL,
    mission_type     INT               NOT NULL,
    mission_points   INT               NOT NULL,
    mission_objective INT               NOT NULL,
    mission_goal      INT               DEFAULT 0 NOT NULL,
    CONSTRAINT PK_Missions PRIMARY KEY (mission_id)
);
GO

CREATE TABLE dbo.Groups (
    group_id          INT IDENTITY(1,1) NOT NULL,
    group_name        NVARCHAR(255)     NOT NULL,
    group_description NVARCHAR(MAX)      NULL,
    group_image       NVARCHAR(500)     NULL,
    group_points      INT               DEFAULT 0 NOT NULL,
    group_creator_id  INT               NOT NULL,
    CONSTRAINT PK_Groups PRIMARY KEY (group_id),
    CONSTRAINT FK_Groups_Creator FOREIGN KEY (group_creator_id) REFERENCES dbo.Users(user_id)
);
GO

ALTER TABLE dbo.Routines ADD CONSTRAINT FK_Routines_Group
    FOREIGN KEY (routine_groupid) REFERENCES dbo.Groups (group_id) ON DELETE SET NULL;
GO

/* ============================================================
   TABLAS RELACIONALES
============================================================ */

CREATE TABLE dbo.Routine_X_Exercise (
    routine_x_exercise_id INT IDENTITY(1,1) NOT NULL,
    routine_x_exercise_routineid INT NOT NULL,
    routine_x_exercise_exerciseid INT NOT NULL,
    routine_x_exercise_reps NVARCHAR(50) NULL,
    routine_x_exercise_sets INT NULL,
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
    user_x_mission_completed BIT DEFAULT 0 NOT NULL,
    user_x_mission_progress INT DEFAULT 0 NOT NULL,
    user_x_mission_points_deducted BIT DEFAULT 0 NOT NULL,
    user_x_mission_completed_date DATE NULL,
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
     user_email, user_height, user_weight, user_objective, user_points, user_role)
VALUES
    ('admin', 'Administrador',
     '$2b$12$bvvrPTWlj.GC8RDiz3ZtRezksJJVtvmB9GVzJBQUBQfc6ZUxfNExG',
     '1990-01-01', 'admin@gymtonic.com', 0, 0, 0, 0, 1);
GO

/* ============================================================
   SEEDS DE PRUEBA
============================================================ */

-- Usuarios
INSERT INTO dbo.Users
    (user_username, user_name, user_password, user_birthdate,
     user_email, user_height, user_weight, user_objective, user_points, user_role)
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

-- Ejercicios
INSERT INTO dbo.Exercises
    (exercise_name, exercise_description, exercise_type, exercise_video, exercise_image)
VALUES
    ('Press Banca con Mancuernas', 'Acostado en un banco plano, empuja las mancuernas hacia arriba desde el nivel del pecho hasta extender los brazos. Controla el descenso para trabajar el pectoral mayor y tríceps.', 1, 'videos/exercises/press_banca_con_mancuernas.mp4', 'images/exercises/press_banca_con_mancuernas.png'),
    ('Fondos en Paralelas', 'Sujétate del respaldo de dos sillas estables y desciende el cuerpo flexionando los codos. Empuja hacia arriba para volver a la posición inicial, enfocando el esfuerzo en los tríceps.', 4, 'videos/exercises/fondos_en_paralelas.mp4', 'images/exercises/fondos_en_paralelas.png'),
    ('Crunch Abdominal en Máquina', 'Sentado en la máquina, flexiona el torso hacia adelante contrayendo los abdominales contra la resistencia. Mantén el movimiento controlado para maximizar la activación del core.', 9, 'videos/exercises/crunch_abdominal_en_maquina.mp4', 'images/exercises/crunch_abdominal_en_maquina.png'),
    ('Remo con Barra', 'Con el torso inclinado y la espalda recta, tira de la barra hacia la parte baja del abdomen. Junta las escápulas al final del movimiento para trabajar la densidad de la espalda.', 2, 'videos/exercises/remo_con_barra.mp4', 'images/exercises/remo_con_barra.png'),
    ('Curl de Muñeca con Mancuernas', 'Apoya los antebrazos sobre un banco plano y realiza flexiones de muñeca con las mancuernas. Este ejercicio aísla y fortalece los flexores del antebrazo', 3, 'videos/exercises/curl_de_muneca_con_mancuernas.mp4', 'images/exercises/curl_de_muneca_con_mancuernas.png'),
    ('Cruce de Poleas', 'De pie entre dos poleas, lleva los agarres hacia el centro del cuerpo cruzándolos ligeramente. Mantén una flexión mínima en los codos para trabajar el pectoral de forma aislada.', 1, 'videos/exercises/cruce_de_poleas.mp4', 'images/exercises/cruce_de_poleas.png'),
    ('Zancada con Mancuerna Overhead', 'Realiza una zancada manteniendo una mancuerna extendida sobre la cabeza. Este movimiento trabaja intensamente los cuádriceps, glúteos y la estabilidad del hombro.', 5, 'videos/exercises/zancada_con_mancuerna_overhead.mp4', 'images/exercises/zancada_con_mancuerna_overhead.png'),
    ('Sentadilla Frontal con Barra', 'Coloca la barra sobre la parte anterior de los hombros y desciende manteniendo el torso erguido. Esta variante enfoca el trabajo en los cuádriceps y reduce la carga en la espalda baja.', 5, 'videos/exercises/sentadilla_frontal_con_barra.mp4', 'images/exercises/sentadilla_frontal_con_barra.png'),
    ('Clean and Press con Barra', 'Levanta la barra desde el suelo hasta los hombros en un movimiento explosivo y luego empújala sobre la cabeza. Trabaja potencia, piernas, espalda y hombros.', 10, 'videos/exercises/clean_and_press_con_barra.mp4', 'images/exercises/clean_and_press_con_barra.png'),
    ('Remo al Mentón con Barra', 'Sujeta la barra con un agarre estrecho y elévala verticalmente hacia el mentón manteniendo los codos por encima de las manos. Enfocado en deltoides y trapecios.', 7, 'videos/exercises/remo_al_menton_con_barra.mp4', 'images/exercises/remo_al_menton_con_barra.png'),
    ('Flexiones de Brazos', 'Manteniendo el cuerpo recto, desciende el pecho hacia el suelo mediante la flexión de los brazos y vuelve a subir. Ejercicio fundamental de empuje para pectoral y tríceps.', 1, 'videos/exercises/flexiones_de_brazos.mp4', 'images/exercises/flexiones_de_brazos.png'),
    ('Hiperextensiones Lumbares', 'En un banco específico, desciende el torso y elévalo hasta quedar alineado con las piernas. Fortalece la musculatura lumbar, glúteos e isquiotibiales.', 2, 'videos/exercises/hiperextensiones_lumbares.mp4', 'images/exercises/hiperextensiones_lumbares.png'),
    ('Press Militar con Mancuernas Sentado', 'Sentado en un banco con respaldo, empuja las mancuernas verticalmente desde los hombros hasta extender los brazos por completo. Enfoca el esfuerzo en los deltoides y mantén el core estable.', 7, 'videos/exercises/press_militar_con_mancuernas_sentado.mp4', 'images/exercises/press_militar_con_mancuernas_sentado.png'),
    ('Press Francés con Barra', 'Acostado en un banco, baja la barra hacia la frente flexionando los codos y luego extiéndelos para trabajar de forma aislada el tríceps braquial.', 4, 'videos/exercises/press_frances_con_barra.mp4', 'images/exercises/press_frances_con_barra.png'),
    ('Hip Thrust con Mancuerna', 'Apoyando la parte superior de la espalda en un banco, eleva la cadera con peso sobre la pelvis hasta alinear el cuerpo, contrayendo intensamente glúteos y cuádriceps.', 5, 'videos/exercises/hip_thrust_con_mancuerna.mp4', 'images/exercises/hip_thrust_con_mancuerna.png'),
    ('Extensión de Tríceps sobre la Cabeza con Polea', 'De espaldas a la máquina y con un pie adelantado, extiende los brazos hacia adelante y arriba usando la polea para trabajar la cabeza larga del tríceps.', 4, 'videos/exercises/extension_de_triceps_sobre_la_cabeza_con_polea.mp4', 'images/exercises/extension_de_triceps_sobre_la_cabeza_con_polea.png'),
    ('Sentadilla Búlgara con Mancuernas', 'Con un pie apoyado atrás en un banco, desciende la cadera manteniendo la espalda recta. Este ejercicio enfoca el trabajo intensamente en el cuádriceps de la pierna delantera y el glúteo.', 5, 'videos/exercises/sentadilla_bulgara_con_mancuernas.mp4', 'images/exercises/sentadilla_bulgara_con_mancuernas.png'),
    ('Peso Muerto con Mancuerna', 'Manteniendo las piernas ligeramente flexionadas, baja las mancuernas hacia el suelo con la espalda recta y sube mediante la extensión de cadera. Trabaja cuádriceps y zona lumbar.', 5, 'videos/exercises/peso_muerto_con_mancuerna.mp4', 'images/exercises/peso_muerto_con_mancuerna.png'),
    ('Press de Hombros con Mancuernas Sentado', 'Sentado sin apoyo lumbar, empuja las mancuernas hacia arriba desde la altura de las orejas. Requiere mayor estabilidad del core para trabajar deltoides y tríceps.', 7, 'videos/exercises/press_de_hombros_con_mancuernas_sentado.mp4', 'images/exercises/press_de_hombros_con_mancuernas_sentado.png'),
    ('Press Arnold con Mancuernas', 'Empieza con las palmas hacia ti a la altura de los hombros, gira las muñecas mientras empujas hacia arriba hasta que las palmas miren al frente. Excelente para el deltoide completo.', 7, 'videos/exercises/press_arnold_con_mancuernas.mp4', 'images/exercises/press_arnold_con_mancuernas.png'),
    ('Dominadas Asistidas', 'Utiliza la máquina de asistencia para realizar una tracción vertical. Se enfoca principalmente en el desarrollo del dorsal ancho y la musculatura de la espalda alta.', 2, 'videos/exercises/dominadas_asistidas.mp4', 'images/exercises/dominadas_asistidas.png'),
    ('Sentadilla Goblet', 'Sujeta una mancuerna frente al pecho y desciende flexionando rodillas y cadera. Excelente para trabajar cuádriceps y glúteos manteniendo el torso erguido.', 5, 'videos/exercises/sentadilla_goblet.mp4', 'images/exercises/sentadilla_goblet.png'),
    ('Press Francés con Mancuernas', 'Acostado en un banco, flexiona los codos para bajar las mancuernas hacia las sienes y luego extiende totalmente. Aísla y fortalece las cabezas del tríceps.', 4, 'videos/exercises/press_frances_con_mancuernas.mp4', 'images/exercises/press_frances_con_mancuernas.png');
    -- ('Cruces en Polea Alta', 'De pie entre poleas altas, tira de los agarres hacia el centro y abajo del cuerpo. Ideal para trabajar la contracción máxima de las fibras de los pectorales.', 1, 'videos/exercises/cruces_en_polea_alta.mp4', 'images/exercises/cruces_en_polea_alta.png');
GO

-- Rutinas
INSERT INTO dbo.Routines (routine_name, routine_image)
VALUES
    ('Full Body Principiante', 'images/routines/rutina_fullbody.png'),
    ('Tren Superior Avanzado', 'images/routines/rutina_tren_superior_avanzado.png'),
    ('Cardio Quema Grasa', 'images/routines/rutina_cardio_quemagrasa.png'),
    ('Piernas y Glúteos', 'images/routines/rutina_piernas_y_gluteos.png');
GO

-- Ejercicios por rutina
INSERT INTO dbo.Routine_X_Exercise (routine_x_exercise_routineid, routine_x_exercise_exerciseid, routine_x_exercise_reps, routine_x_exercise_sets)
VALUES
    (1,1,'12',3),(1,2,'10',3),(1,6,'15',3),
    (2,2,'8',4),(2,4,'10',3),(2,5,'12',3),(2,6,'12',3),
    (3,7,'20',4),(3,8,'15',4),(3,9,'12',3),(3,10,'10',3), -- Cardio Quema Grasa
    (4,1,'12',3),(4,3,'20',3); -- Piernas y Glúteos
GO

-- Rutinas asignadas a usuarios (IDs 2-9)
INSERT INTO dbo.User_X_Routine (user_x_routine_userid, user_x_routine_routineid)
VALUES
    (2,1),(2,3),
    (3,3),
    (4,2),(4,4),
    (5,3),(5,4),
    (6,1),
    (7,3),
    (8,2),(8,4),
    (9,1),(9,3);
GO

-- Misiones (mission_type: 0=diaria 1=semanal 2=mensual)
-- mission_objective: 0=Mantenimiento 1=Pérdida de peso 2=Ganancia muscular 3=Rendimiento
INSERT INTO dbo.Missions (mission_name, mission_type, mission_points, mission_objective, mission_goal)
VALUES
    ('Primera sesión',            0,  10,  0, 0),
    ('Racha de 3 días',           0,  30,  0, 3),
    ('Completa 5 entrenamientos', 1,  75,  1, 5),
    ('10 km en una semana',       1, 100,  1, 10),
    ('30 sesiones en un mes',     2, 500,  2, 30),
    ('Quema 10 000 kcal',         2, 300,  2, 10000),
    ('Primer ejercicio de fuerza',0,  10,  3, 0),
    ('Semana de cardio completa', 1,  80,  3, 0);
GO

-- Grupos
INSERT INTO dbo.Groups (group_name, group_description, group_image, group_points, group_creator_id)
VALUES
    ('Equipo Alpha', 'Grupo para los mejores atletas del gimnasio.', 'alpha.jpg', 1500, 2),
    ('Reto Verano 2025', 'Ponte en forma para el próximo verano con nosotros.', 'summer.jpg', 800, 5),
    ('Maratonianos', 'Corredores de fondo buscando mejorar sus tiempos.', 'marathon.jpg', 2200, 3),
    ('Powerlifters', 'Entrenamiento enfocado en fuerza pura y competición.', 'power.jpg', 1900, 4);
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
INSERT INTO dbo.User_X_Mission (user_x_mission_userid, user_x_mission_missionid, user_x_mission_expiration, user_x_mission_completed, user_x_mission_progress)
VALUES
    (2,1,'2026-12-31', 0, 0),
    (2,3,'2026-07-07', 1, 5),
    (3,2,'2026-12-31', 0, 0),
    (3,4,'2026-07-07', 0, 2),
    (4,5,'2026-07-31', 1, 30),
    (4,7,'2026-12-31', 0, 0),
    (5,3,'2026-07-07', 0, 1),
    (5,8,'2026-07-07', 0, 0),
    (6,1,'2026-12-31', 1, 1),
    (7,2,'2026-12-31', 0, 0),
    (7,6,'2026-07-31', 0, 500),
    (8,5,'2026-07-31', 0, 10),
    (8,7,'2026-12-31', 1, 1),
    (9,1,'2026-12-31', 0, 0),
    (9,4,'2026-07-07', 0, 4);
GO

PRINT 'GymTonic - BD creada y poblada correctamente.';
GO