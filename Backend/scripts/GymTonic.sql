/* ============================================================
   GymTonic - SCRIPT DEFINITIVO Y LIMPIO
   ============================================================ */

USE master;
GO

----------------------------------------------------------
-- 0. ASEGURAR LOGIN SQL LIMPIO
----------------------------------------------------------
IF EXISTS (SELECT * FROM sys.server_principals WHERE name = 'gymtonic')
BEGIN
    DROP LOGIN gymtonic;
END
GO

CREATE LOGIN gymtonic
WITH PASSWORD = 'gintonic',
     CHECK_POLICY = OFF,
     CHECK_EXPIRATION = OFF;
GO

----------------------------------------------------------
-- 1. ELIMINAR BD SI EXISTE
----------------------------------------------------------
IF DB_ID('GymTonic') IS NOT NULL
BEGIN
    ALTER DATABASE GymTonic SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE GymTonic;
END
GO

----------------------------------------------------------
-- 2. CREAR BD Y ASIGNAR OWNER
----------------------------------------------------------
CREATE DATABASE GymTonic;
GO

ALTER AUTHORIZATION ON DATABASE::GymTonic TO gymtonic;
GO

USE GymTonic;
GO

----------------------------------------------------------
-- 3. CREAR USUARIO LIMPIO Y PERMISOS
----------------------------------------------------------
IF EXISTS (SELECT * FROM sys.database_principals WHERE name = 'gymtonic')
BEGIN
    DROP USER gymtonic;
END
GO

CREATE USER gymtonic FOR LOGIN gymtonic;
GO

ALTER ROLE db_owner ADD MEMBER gymtonic;
GO

----------------------------------------------------------
-- 4. TABLAS
----------------------------------------------------------

CREATE TABLE dbo.Users
(
    user_id INT IDENTITY(1,1) NOT NULL,
    user_username NVARCHAR(255) NOT NULL,
    user_name NVARCHAR(255) NOT NULL,
    user_password NVARCHAR(255) NOT NULL,
    user_birthdate DATE NOT NULL,
    user_email NVARCHAR(255) NOT NULL,
    user_height FLOAT NOT NULL,
    user_weight FLOAT NOT NULL,
    user_objective INT NOT NULL,
    user_points INT NULL,
    user_role INT NOT NULL,
    CONSTRAINT PK_Users PRIMARY KEY (user_id),
    CONSTRAINT UQ_Users_username UNIQUE (user_username),
    CONSTRAINT UQ_Users_email UNIQUE (user_email)
);
GO

CREATE TABLE dbo.Exercises
(
    exercise_id INT IDENTITY(1,1) NOT NULL,
    exercise_name NVARCHAR(255) NOT NULL,
    exercise_description NVARCHAR(MAX) NOT NULL,
    exercise_type INT NOT NULL,
    exercise_video NVARCHAR(500) NULL,
    exercise_image NVARCHAR(500) NULL,
    CONSTRAINT PK_Exercises PRIMARY KEY (exercise_id)
);
GO

CREATE TABLE dbo.Routines
(
    routine_id INT IDENTITY(1,1) NOT NULL,
    routine_name NVARCHAR(255) NOT NULL,
    CONSTRAINT PK_Routines PRIMARY KEY (routine_id)
);
GO

CREATE TABLE dbo.Missions
(
    mission_id INT IDENTITY(1,1) NOT NULL,
    mission_name NVARCHAR(255) NOT NULL,
    mission_type INT NOT NULL,
    mission_points INT NOT NULL,
    mission_objetive INT NOT NULL,
    CONSTRAINT PK_Missions PRIMARY KEY (mission_id)
);
GO

CREATE TABLE dbo.Groups
(
    group_id INT IDENTITY(1,1) NOT NULL,
    group_name NVARCHAR(255) NOT NULL,
    CONSTRAINT PK_Groups PRIMARY KEY (group_id)
);
GO

----------------------------------------------------------
-- 5. RELACIONES
----------------------------------------------------------

CREATE TABLE dbo.Routine_X_Exercise
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    routine_id INT NOT NULL,
    exercise_id INT NOT NULL,
    FOREIGN KEY (routine_id) REFERENCES dbo.Routines(routine_id),
    FOREIGN KEY (exercise_id) REFERENCES dbo.Exercises(exercise_id)
);
GO

CREATE TABLE dbo.User_X_Routine
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    routine_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES dbo.Users(user_id),
    FOREIGN KEY (routine_id) REFERENCES dbo.Routines(routine_id)
);
GO

CREATE TABLE dbo.Group_X_User
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    group_id INT NOT NULL,
    user_id INT NOT NULL,
    range INT NOT NULL,
    FOREIGN KEY (group_id) REFERENCES dbo.Groups(group_id),
    FOREIGN KEY (user_id) REFERENCES dbo.Users(user_id)
);
GO

CREATE TABLE dbo.Friends
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    user1 INT NOT NULL,
    user2 INT NOT NULL,
    FOREIGN KEY (user1) REFERENCES dbo.Users(user_id),
    FOREIGN KEY (user2) REFERENCES dbo.Users(user_id)
);
GO

CREATE TABLE dbo.Frequest
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    sender INT NOT NULL,
    receiver INT NOT NULL,
    status INT NOT NULL,
    FOREIGN KEY (sender) REFERENCES dbo.Users(user_id),
    FOREIGN KEY (receiver) REFERENCES dbo.Users(user_id)
);
GO

CREATE TABLE dbo.User_X_Mission
(
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    mission_id INT NOT NULL,
    expiration DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES dbo.Users(user_id),
    FOREIGN KEY (mission_id) REFERENCES dbo.Missions(mission_id)
);
GO

----------------------------------------------------------
-- 6. ADMIN POR DEFECTO
----------------------------------------------------------
INSERT INTO dbo.Users
VALUES
('admin','Administrador','$2b$12$bvvrPTWlj.GC8RDiz3ZtRezksJJVtvmB9GVzJBQUBQfc6ZUxfNExG','1990-01-01','admin@gymtonic.com',0,0,0,0,1);
GO