
-- ============
-- insert roles
-- ============
insert into roles (name)
values ('role_user'),
       ('role_admin');

-- ==============
-- insert admin user
-- ==============
insert into users (username, email, password)
values ('maki_admin', 'maki_admin@makibeans.com', 'maki_admin');

-- ==================
-- assign user roles
-- ==================
insert into user_roles (user_id, role_id)
values (1, 2);

-- ========================
-- insert attribute templates
-- ========================
insert into attribute_templates (name)
values ('origin'),
       ('flavor'),
       ('intensity');

-- ========================
-- insert attribute values
-- ========================
insert into attribute_values (template_id, value)
values (1, 'colombia'),
       (1, 'ethiopia'),
       (1, 'kenya'),
       (1, 'brazil'),
       (2, 'nutty'),
       (2, 'chocolatey'),
       (2, 'fruity'),
       (2, 'spicy'),
       (3, 'mild'),
       (3, 'medium'),
       (3, 'strong'),
       (3, 'extra strong');

-- ===============
-- insert sizes
-- ===============
insert into sizes (name)
values ('100g'),
       ('250g'),
       ('500g'),
       ('small'),
       ('medium'),
       ('large');

-- ===================
-- insert categories
-- ===================
insert into categories (name, description, parent_category_id)
values ('coffee', 'all types of coffee beans and blends', null),
       ('brewing equipment', 'gear for brewing coffee', null),
       ('accessories', 'accessories for your coffee ritual', null),

       ('espresso beans', 'strong, dark-roasted beans perfect for espresso', 1),
       ('filter coffee', 'medium-roasted beans for pour-over or drip', 1),
       ('decaf', 'decaffeinated coffee for late nights', 1),

       ('dark roast', 'deep and bold espresso roast', 4),
       ('medium roast', 'balanced flavor and smooth finish', 4),
       ('single origin', 'unique beans from a specific region', 5),
       ('blends', 'flavorful blends for daily brews', 5),

       ('french press', 'immersion brewing gear', 2),
       ('pour over', 'tools for manual pour-over brewing', 2),
       ('espresso machines', 'machines for pulling perfect shots', 2),
       ('manual', 'lever-based espresso machines', 13),
       ('automatic', 'fully automated espresso brewing', 13),

       ('cups & mugs', 'serve your coffee in style', 3),
       ('grinders', 'manual and electric grinders', 3),
       ('scales', 'precision scales for brewing', 3);

-- ================
-- insert products
-- ================
insert into products (product_name, product_description, category_id)
values ('ethiopian dark roast', 'bold and fruity beans from ethiopia, perfect for espresso lovers.', 7),
       ('colombian medium roast', 'balanced, nutty flavor with a smooth finish.', 8),
       ('single origin kenya aa', 'bright and acidic coffee with citrus notes.', 9),
       ('house blend filter', 'smooth and mild blend for everyday pour-over brews.', 10),
       ('brazilian decaf', 'sweet and nutty decaf with no compromise on flavor.', 6),
       ('bodum french press', 'classic 8-cup french press made of borosilicate glass.', 11),
       ('hario v60 dripper', 'ceramic pour-over cone for precision brewing.', 12),
       ('la marzocco linea mini', 'professional-grade espresso machine for home baristas.', 15),
       ('hand grinder', 'portable manual grinder with ceramic burrs.', 17),
       ('coffee scale', 'digital scale with timer for accurate brewing.', 18);

-- ========================
-- insert product attributes
-- ========================
insert into product_attributes (product_id, template_id)
values (1, 1),
       (1, 2),
       (1, 3),
       (2, 1),
       (2, 2),
       (2, 3),
       (3, 1),
       (3, 2),
       (3, 3),
       (4, 1),
       (4, 2),
       (4, 3),
       (5, 1),
       (5, 2),
       (5, 3);

-- ================================
-- insert product attribute values
-- ================================
insert into product_attribute_values (product_attribute_id, attribute_value_id)
values (1, 2),
       (2, 7),
       (3, 11),
       (4, 1),
       (5, 5),
       (6, 10),
       (7, 3),
       (8, 7),
       (9, 10),
       (10, 4),
       (11, 6),
       (12, 9),
       (13, 4),
       (14, 5),
       (15, 9);

-- ====================
-- insert product variants
-- ====================
insert into product_variants (product_id, size_id, price_in_cents, stock, sku)
values (1, 1, 2000, 50, 'ETH-100G-001'),
       (1, 2, 2500, 30, 'ETH-250G-002'),
       (2, 1, 2200, 40, 'COL-100G-003'),
       (2, 2, 2800, 25, 'COL-250G-004'),
       (3, 1, 2300, 35, 'KEN-100G-005'),
       (3, 2, 3000, 20, 'KEN-250G-006'),
       (4, 1, 1800, 60, 'HBL-100G-007'),
       (4, 2, 2400, 45, 'HBL-250G-008'),
       (5, 1, 1600, 70, 'BZD-100G-009'),
       (5, 2, 2100, 50, 'BZD-250G-010');
