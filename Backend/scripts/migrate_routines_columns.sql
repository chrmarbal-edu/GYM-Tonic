-- Ejecutar en bases de datos ya creadas (GymTonic existente)

IF COL_LENGTH('dbo.Routines', 'routine_image') IS NULL
BEGIN
    ALTER TABLE dbo.Routines ADD routine_image NVARCHAR(500) NULL;
END
GO

IF COL_LENGTH('dbo.Routines', 'routine_creator_id') IS NULL
BEGIN
    ALTER TABLE dbo.Routines ADD routine_creator_id INT NULL;
END
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = 'FK_Routines_Creator'
)
BEGIN
    ALTER TABLE dbo.Routines
        ADD CONSTRAINT FK_Routines_Creator
        FOREIGN KEY (routine_creator_id) REFERENCES dbo.Users(user_id) ON DELETE SET NULL;
END
GO

UPDATE dbo.Routines SET routine_image = 'images/routines/rutina_fullbody.png' WHERE routine_id = 1 AND routine_image IS NULL;
UPDATE dbo.Routines SET routine_image = 'images/routines/rutina_tren_superior_avanzado.png' WHERE routine_id = 2 AND routine_image IS NULL;
UPDATE dbo.Routines SET routine_image = 'images/routines/rutina_cardio_quemagrasa.png' WHERE routine_id = 3 AND routine_image IS NULL;
UPDATE dbo.Routines SET routine_image = 'images/routines/rutina_piernas_y_gluteos.png' WHERE routine_id = 4 AND routine_image IS NULL;
UPDATE dbo.Routines SET routine_image = 'images/routines/rutina_flexibilidad_y_mobilidad.png' WHERE routine_id = 5 AND routine_image IS NULL;
GO

IF COL_LENGTH('dbo.Routines', 'routine_is_personal_routine') IS NULL
BEGIN
    ALTER TABLE dbo.Routines ADD routine_is_personal_routine INT NOT NULL
        CONSTRAINT DF_Routines_is_personal_existing DEFAULT (0);
END
GO

-- Rutinas creadas por usuario (no grupo) pasan a ser "Mis rutinas"
UPDATE dbo.Routines
SET routine_is_personal_routine = 1
WHERE routine_creator_id IS NOT NULL
  AND routine_is_group_routine = 0;
GO
