INSERT INTO categories (code, description, archetype, parent) VALUES
    ('-', 'Проблема отсутствует', 'NOT_BUG', '0'),
    ('0', 'Городская среда', 'PM_BUG', '1'),
    ('1', 'Доставка', 'PARTNER_BUG', '2'),
    ('2', 'Товар', 'PARTNER_BUG', '3'),
    ('3', 'Поддержка', 'PARTNER_BUG', '4'),
    ('4', 'Постамат', 'PM_BUG', '5'),
    ('5', 'Упаковка', 'PARTNER_BUG', '6'),
    ('6', 'Другое', 'OTHER', '7');

INSERT INTO archetypes (id, description) VALUES
    ('OTHER', 'Неизвестная проблема');