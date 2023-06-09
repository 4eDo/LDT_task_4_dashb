ALTER TABLE tickets ADD COLUMN subcat integer;
UPDATE tickets SET subcat = category;