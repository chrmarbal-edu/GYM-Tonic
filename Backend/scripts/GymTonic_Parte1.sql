/* ============================================================
   PARTE 1 - Ejecutar conectado como SA o administrador
============================================================ */

USE master;
GO

-- Login (si no existe)
IF NOT EXISTS (
    SELECT 1 FROM sys.server_principals WHERE name = 'gymtonic'
)
BEGIN
    CREATE LOGIN gymtonic
        WITH PASSWORD      = 'gintonic',
             CHECK_POLICY     = OFF,
             CHECK_EXPIRATION = OFF;
    PRINT 'Login gymtonic creado.';
END
ELSE
    PRINT 'Login gymtonic ya existía, se omite.';
GO

-- Eliminar BD si existe
IF DB_ID('GymTonic') IS NOT NULL
BEGIN
    ALTER DATABASE GymTonic SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE GymTonic;
    PRINT 'Base de datos GymTonic eliminada.';
END
GO

-- Crear BD
CREATE DATABASE GymTonic;
PRINT 'Base de datos GymTonic creada.';
GO

USE GymTonic;
GO

-- Crear usuario en la BD
IF NOT EXISTS (
    SELECT 1 FROM sys.database_principals WHERE name = 'gymtonic'
)
BEGIN
    CREATE USER gymtonic FOR LOGIN gymtonic;
    PRINT 'Usuario gymtonic creado en GymTonic.';
END
ELSE
    PRINT 'Usuario gymtonic ya existía en GymTonic.';
GO

ALTER ROLE db_owner ADD MEMBER gymtonic;
PRINT 'Permisos db_owner asignados a gymtonic.';
GO