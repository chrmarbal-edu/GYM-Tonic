/* ============================================================
   Migración: rutinas de grupo (BD ya existente)
   Ejecutar conectado a GymTonic con permisos DDL
============================================================ */

USE GymTonic;
GO

IF COL_LENGTH('dbo.Routines', 'routine_is_group_routine') IS NULL
BEGIN
    ALTER TABLE dbo.Routines ADD
        routine_is_group_routine INT NOT NULL
            CONSTRAINT DF_Routines_is_group_routine DEFAULT (0),
        routine_groupid INT NULL;
END
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.foreign_keys WHERE name = 'FK_Routines_Group'
)
BEGIN
    ALTER TABLE dbo.Routines ADD CONSTRAINT FK_Routines_Group
        FOREIGN KEY (routine_groupid) REFERENCES dbo.Groups (group_id)
        ON DELETE SET NULL;
END
GO

PRINT 'Migración routine_is_group_routine / routine_groupid aplicada.';
GO
