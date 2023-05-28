INSERT INTO categories (id, code, description, archetype, parent) VALUES
    (1, '-', 'Проблема отсутствует', 'NOT_BUG', '0'),
    (2, '0', 'Городская среда', 'PM_BUG', '1'),
    (3, '1', 'Доставка', 'PARTNER_BUG', '2'),
    (4, '2', 'Товар', 'PARTNER_BUG', '3'),
    (5, '3', 'Поддержка', 'PARTNER_BUG', '4'),
    (6, '4', 'Постамат', 'PM_BUG', '5'),
    (7, '5', 'Упаковка', 'PARTNER_BUG', '6'),
    (8, '6', 'Другое', 'OTHER', '7');

INSERT INTO archetypes (id, description) VALUES
    ('OTHER', 'Неизвестная проблема');