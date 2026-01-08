/* ============================================================
   GymTonic - Script completo SQL Server (T-SQL)
   - Crea la BD "GymTonic"
   - Crea tablas, campos (con tipos), PK, UQ, y relaciones (FK)
   - Basado en el modelo E/R de la imagen
   ============================================================ */

USE master;
GO

----------------------------------------------------------
-- 1. ELIMINAR BASE DE DATOS SI EXISTE
----------------------------------------------------------
IF DB_ID('GymTonic') IS NOT NULL
BEGIN
    ALTER DATABASE GymTonic SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE GymTonic;
END
GO

----------------------------------------------------------
-- 2. CREAR LA BASE DE DATOS
----------------------------------------------------------
CREATE DATABASE GymTonic;
GO

USE GymTonic;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
GO

/* ============================================================
   3. TABLAS PRINCIPALES
   ============================================================ */

----------------------------------------------------------
-- 3.1 USERS
----------------------------------------------------------
CREATE TABLE dbo.Users
(
    user_id        INT IDENTITY(1,1) NOT NULL,
    user_username  NVARCHAR(255) NOT NULL,   -- String NN UQ
    user_name      NVARCHAR(255) NOT NULL,   -- String NN
    user_password  NVARCHAR(255) NOT NULL,   -- String NN
    user_birthdate DATE NOT NULL,            -- Date NN
    user_email     NVARCHAR(255) NOT NULL,   -- String NN UQ
    user_height    FLOAT NOT NULL,           -- Float NN
    user_weight    FLOAT NOT NULL,           -- Float NN
    user_objetive  INT NOT NULL,             -- Int NN
    user_points    INT NULL,                 -- Int (sin NN en el diagrama)
    user_role      INT NOT NULL,             -- Int NN

    CONSTRAINT PK_Users PRIMARY KEY (user_id),
    CONSTRAINT UQ_Users_user_username UNIQUE (user_username),
    CONSTRAINT UQ_Users_user_email UNIQUE (user_email)
);
GO

----------------------------------------------------------
-- 3.2 EXERCISES
----------------------------------------------------------
CREATE TABLE dbo.Exercises
(
    exercise_id          INT IDENTITY(1,1) NOT NULL,
    exercise_name        NVARCHAR(255) NOT NULL,  -- String NN
    exercise_description NVARCHAR(MAX) NOT NULL,  -- String NN
    exercise_type        INT NOT NULL,            -- Int NN
    exercise_video       NVARCHAR(500) NULL,      -- String
    exercise_image       NVARCHAR(500) NULL,      -- String

    CONSTRAINT PK_Exercises PRIMARY KEY (exercise_id)
);
GO

----------------------------------------------------------
-- 3.3 ROUTINES
----------------------------------------------------------
CREATE TABLE dbo.Routines
(
    routine_id   INT IDENTITY(1,1) NOT NULL,
    routine_name NVARCHAR(255) NOT NULL, -- String NN

    CONSTRAINT PK_Routines PRIMARY KEY (routine_id)
);
GO

----------------------------------------------------------
-- 3.4 MISSIONS
----------------------------------------------------------
CREATE TABLE dbo.Missions
(
    mission_id       INT IDENTITY(1,1) NOT NULL,
    mission_name     NVARCHAR(255) NOT NULL, -- String NN
    mission_type     INT NOT NULL,           -- Int NN
    mission_points   INT NOT NULL,           -- Int NN
    mission_objetive INT NOT NULL,           -- Int NN

    CONSTRAINT PK_Missions PRIMARY KEY (mission_id)
);
GO

----------------------------------------------------------
-- 3.5 GROUPS
----------------------------------------------------------
CREATE TABLE dbo.Groups
(
    group_id   INT IDENTITY(1,1) NOT NULL,
    group_name NVARCHAR(255) NOT NULL, -- String NN

    CONSTRAINT PK_Groups PRIMARY KEY (group_id)
);
GO

/* ============================================================
   4. TABLAS RELACIONALES (N:N) Y RELACIONES (FK)
   ============================================================ */

----------------------------------------------------------
-- 4.1 ROUTINE_X_EXERCISE  (N:N entre Routines y Exercises)
----------------------------------------------------------
CREATE TABLE dbo.Routine_X_Exercise
(
    routine_x_exercise_id         INT IDENTITY(1,1) NOT NULL,
    routine_x_exercise_routineid  INT NOT NULL,  -- Int NN FK
    routine_x_exercise_exerciseid INT NOT NULL,  -- Int NN FK

    CONSTRAINT PK_Routine_X_Exercise PRIMARY KEY (routine_x_exercise_id),

    CONSTRAINT FK_Routine_X_Exercise_Routines
        FOREIGN KEY (routine_x_exercise_routineid)
        REFERENCES dbo.Routines (routine_id),

    CONSTRAINT FK_Routine_X_Exercise_Exercises
        FOREIGN KEY (routine_x_exercise_exerciseid)
        REFERENCES dbo.Exercises (exercise_id)
);
GO

----------------------------------------------------------
-- 4.2 USER_X_ROUTINE (relación Users - Routines)
----------------------------------------------------------
CREATE TABLE dbo.User_X_Routine
(
    user_x_routine_id        INT IDENTITY(1,1) NOT NULL,
    user_x_routine_userid    INT NOT NULL,  -- Int NN FK
    user_x_routine_routineid INT NOT NULL,  -- Int NN FK

    CONSTRAINT PK_User_X_Routine PRIMARY KEY (user_x_routine_id),

    CONSTRAINT FK_User_X_Routine_Users
        FOREIGN KEY (user_x_routine_userid)
        REFERENCES dbo.Users (user_id),

    CONSTRAINT FK_User_X_Routine_Routines
        FOREIGN KEY (user_x_routine_routineid)
        REFERENCES dbo.Routines (routine_id)
);
GO

----------------------------------------------------------
-- 4.3 GROUP_X_USER (N:N entre Groups y Users) + range
----------------------------------------------------------
CREATE TABLE dbo.Group_x_user
(
    Group_x_user_id      INT IDENTITY(1,1) NOT NULL,
    Group_x_user_groupid INT NOT NULL,  -- Int NN FK
    Group_x_user_userid  INT NOT NULL,  -- Int NN FK
    Group_x_user_range   INT NOT NULL,  -- Int NN

    CONSTRAINT PK_Group_x_user PRIMARY KEY (Group_x_user_id),

    CONSTRAINT FK_Group_x_user_Groups
        FOREIGN KEY (Group_x_user_groupid)
        REFERENCES dbo.Groups (group_id),

    CONSTRAINT FK_Group_x_user_Users
        FOREIGN KEY (Group_x_user_userid)
        REFERENCES dbo.Users (user_id)
);
GO

----------------------------------------------------------
-- 4.4 FRIENDS (N:N entre Users y Users)
----------------------------------------------------------
CREATE TABLE dbo.Friends
(
    friend_id      INT IDENTITY(1,1) NOT NULL,
    friend_userid1 INT NOT NULL, -- Int NN FK
    friend_userid2 INT NOT NULL, -- Int NN FK

    CONSTRAINT PK_Friends PRIMARY KEY (friend_id),

    CONSTRAINT FK_Friends_Users_1
        FOREIGN KEY (friend_userid1)
        REFERENCES dbo.Users (user_id),

    CONSTRAINT FK_Friends_Users_2
        FOREIGN KEY (friend_userid2)
        REFERENCES dbo.Users (user_id)
);
GO

----------------------------------------------------------
-- 4.5 FREQUEST (1:N desde Users hacia Frequest por sender y receiver)
----------------------------------------------------------
CREATE TABLE dbo.Frequest
(
    frequest_id       INT IDENTITY(1,1) NOT NULL,
    frequest_sender   INT NOT NULL, -- Int NN FK
    frequest_receiver INT NOT NULL, -- Int NN FK
    frequest_status   INT NOT NULL, -- Int NN

    CONSTRAINT PK_Frequest PRIMARY KEY (frequest_id),

    CONSTRAINT FK_Frequest_Users_Sender
        FOREIGN KEY (frequest_sender)
        REFERENCES dbo.Users (user_id),

    CONSTRAINT FK_Frequest_Users_Receiver
        FOREIGN KEY (frequest_receiver)
        REFERENCES dbo.Users (user_id)
);
GO

----------------------------------------------------------
-- 4.6 USER_X_MISSION (N:N entre Users y Missions) + expiration
-- Nota: en el diagrama aparece "expiration : Date NN FK",
-- pero no hay tabla destino para esa FK, así que se crea como DATE NOT NULL.
----------------------------------------------------------
CREATE TABLE dbo.User_X_Mission
(
    user_x_mission_id         INT IDENTITY(1,1) NOT NULL,
    user_x_mission_userid     INT NOT NULL,  -- Int NN FK
    user_x_mission_missionid  INT NOT NULL,  -- Int NN FK
    user_x_mission_expiration DATE NOT NULL, -- Date NN

    CONSTRAINT PK_User_X_Mission PRIMARY KEY (user_x_mission_id),

    CONSTRAINT FK_User_X_Mission_Users
        FOREIGN KEY (user_x_mission_userid)
        REFERENCES dbo.Users (user_id),

    CONSTRAINT FK_User_X_Mission_Missions
        FOREIGN KEY (user_x_mission_missionid)
        REFERENCES dbo.Missions (mission_id)
);
GO