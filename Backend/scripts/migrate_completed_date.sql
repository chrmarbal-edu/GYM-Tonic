-- Migración: añadir fecha de completación a User_X_Mission
-- Ejecutar en bases de datos ya creadas (GymTonic existente)
-- Es idempotente: comprueba existencia antes de alterar

IF COL_LENGTH('dbo.User_X_Mission', 'user_x_mission_completed_date') IS NULL
BEGIN
    ALTER TABLE dbo.User_X_Mission
    ADD user_x_mission_completed_date DATE NULL;
END
GO

-- Rellenar misiones ya completadas que no tienen fecha (se asume hoy como fecha de completación)
UPDATE dbo.User_X_Mission
SET user_x_mission_completed_date = CAST(GETDATE() AS DATE)
WHERE user_x_mission_completed = 1
  AND user_x_mission_completed_date IS NULL;
GO
