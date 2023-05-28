ALTER TABLE tickets DROP COLUMN messages;
ALTER TABLE tickets ADD COLUMN messages BIGINT;
ALTER TABLE tickets ALTER COLUMN partner TYPE text;