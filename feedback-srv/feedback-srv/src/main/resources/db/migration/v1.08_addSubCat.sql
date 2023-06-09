ALTER TABLE messages ADD COLUMN subcat integer;
UPDATE messages SET subcat = category;

INSERT INTO categories (id, parent, code, description, archetype) VALUES
    (9, 2, '1', 'безопасность', 'PM_BUG'),
    (10, 2, '2', 'другое', 'PM_BUG'),
    (11, 2, '3', 'навигация', 'PM_BUG'),

    (12, 3, '4', 'доставка просрочена', 'PARTNER_BUG'),
    (13, 3, '5', 'другое', 'PARTNER_BUG'),
    (14, 3, '6', 'ячейка пустая', 'PARTNER_BUG'),

    (15, 6, '7', 'другое', 'PM_BUG'),
    (16, 6, '8', 'не могу получить заказ', 'PM_BUG'),
    (17, 6, '9', 'не работает', 'PM_BUG'),
    (18, 6, '10', 'не реагирует на нажатия', 'PM_BUG'),

    (19, 4, '11', 'бракованный товар', 'PARTNER_BUG'),
    (20, 4, '12', 'возврат заказа', 'PARTNER_BUG'),
    (21, 4, '13', 'доставили другой товар', 'PARTNER_BUG'),
    (22, 4, '14', 'другое', 'PARTNER_BUG'),
    (23, 4, '15', 'не доставили часть заказа', 'PARTNER_BUG'),
    (24, 4, '16', 'повреждённый товар', 'PARTNER_BUG'),
    (25, 4, '17', 'просроченный товар', 'PARTNER_BUG'),
    (26, 4, '18', 'товар б/у', 'PARTNER_BUG'),
    (27, 4, '19', 'товар не подошел', 'PARTNER_BUG'),

    (28, 7, '20', 'некачественная упаковка', 'PARTNER_BUG'),
    (29, 7, '21', 'другое', 'PARTNER_BUG'),
    (31, 7, '22', 'повреждена упаковка', 'PARTNER_BUG');