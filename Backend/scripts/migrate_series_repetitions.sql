-- Migración: añadir series y repeticiones a Routine_X_Exercise
-- Ejecutar en bases de datos ya creadas (GymTonic existente)
-- Es idempotente: comprueba existencia antes de alterar

IF COL_LENGTH('dbo.Routine_X_Exercise', 'routine_x_exercise_series') IS NULL
BEGIN
    ALTER TABLE dbo.Routine_X_Exercise
    ADD routine_x_exercise_series INT NOT NULL
        CONSTRAINT DF_RXE_series DEFAULT 3;
END
GO

IF COL_LENGTH('dbo.Routine_X_Exercise', 'routine_x_exercise_repetitions') IS NULL
BEGIN
    ALTER TABLE dbo.Routine_X_Exercise
    ADD routine_x_exercise_repetitions INT NOT NULL
        CONSTRAINT DF_RXE_repetitions DEFAULT 10;
END
GO
