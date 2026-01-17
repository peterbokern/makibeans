-- Makibeans seed data matching your current schema (PostgreSQL)
-- Generated: 2026-01-11
-- Inserts only; all is_deleted explicitly set.

BEGIN;

-- Categories
INSERT INTO categories (name, slug, description, parent_category_id, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES
    ('Coffee Beans', 'coffee-beans', 'All roasted coffee bean products.', NULL, NOW(), NOW(), 'system', 'system', false),
    ('Coffee Machines', 'coffee-machines', 'Espresso, filter machines, and grinders.', NULL, NOW(), NOW(), 'system', 'system', false),
    ('Accessories', 'accessories', 'Filters, tampers, mugs, scales, and more.', NULL, NOW(), NOW(), 'system', 'system', false),
    ('Pods & Capsules', 'pods-capsules', 'Coffee pods and compatible capsules.', NULL, NOW(), NOW(), 'system', 'system', false);
-- Subcategories
INSERT INTO categories (name, slug, description, parent_category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT v.name, v.slug, v.description, c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c
         JOIN (VALUES
                   ('Single Origin', 'single-origin', 'Single origin coffee beans.'),
                   ('Blends', 'blends', 'Signature blends.'),
                   ('Decaf', 'decaf', 'Decaffeinated beans.')
) AS v(name, slug, description) ON true
WHERE c.slug = 'coffee-beans';

INSERT INTO categories (name, slug, description, parent_category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT v.name, v.slug, v.description, c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c
         JOIN (VALUES
                   ('Espresso Machines', 'espresso-machines', 'Espresso machines.'),
                   ('Filter Machines', 'filter-machines', 'Drip and filter coffee makers.'),
                   ('Grinders', 'grinders', 'Coffee grinders.')
) AS v(name, slug, description) ON true
WHERE c.slug = 'coffee-machines';

INSERT INTO categories (name, slug, description, parent_category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT v.name, v.slug, v.description, c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c
         JOIN (VALUES
                   ('Barista Tools', 'barista-tools', 'Tampers, pitchers, tools.'),
                   ('Brewing', 'brewing', 'Filters, drippers, kettles.'),
                   ('Drinkware', 'drinkware', 'Mugs, cups, glasses.')
) AS v(name, slug, description) ON true
WHERE c.slug = 'accessories';

INSERT INTO categories (name, slug, description, parent_category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT v.name, v.slug, v.description, c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c
         JOIN (VALUES
                   ('Nespresso Compatible', 'nespresso-compatible', 'Nespresso original compatible capsules.'),
                   ('Dolce Gusto Compatible', 'dolce-gusto-compatible', 'Dolce Gusto compatible pods.'),
                   ('Senseo Compatible', 'senseo-compatible', 'Senseo compatible pads.')
) AS v(name, slug, description) ON true
WHERE c.slug = 'pods-capsules';

-- Sizes
INSERT INTO sizes (name, slug, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES
    ('250g', '250g', NOW(), NOW(), 'system', 'system', false),
    ('500g', '500g', NOW(), NOW(), 'system', 'system', false),
    ('1kg', '1kg', NOW(), NOW(), 'system', 'system', false),
    ('Standard', 'standard', NOW(), NOW(), 'system', 'system', false),
    ('Plus', 'plus', NOW(), NOW(), 'system', 'system', false),
    ('Pro', 'pro', NOW(), NOW(), 'system', 'system', false),
    ('Single', 'single', NOW(), NOW(), 'system', 'system', false);

-- Attributes
INSERT INTO attributes (name, description, slug, data_type, input_type, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES
    ('Roast Level', 'Degree of roast for coffee beans', 'roast-level', 'STRING', 'DROPDOWN', NOW(), NOW(), 'system', 'system', false),
    ('Origin', 'Country/region of origin', 'origin', 'STRING', 'MULTISELECT', NOW(), NOW(), 'system', 'system', false),
    ('Process', 'Processing method', 'process', 'STRING', 'DROPDOWN', NOW(), NOW(), 'system', 'system', false),
    ('Tasting Notes', 'Flavor notes', 'tasting-notes', 'STRING', 'MULTISELECT', NOW(), NOW(), 'system', 'system', false),
    ('Organic', 'Certified organic', 'organic', 'BOOLEAN', 'CHECKBOX', NOW(), NOW(), 'system', 'system', false),

    ('Machine Type', 'Type of machine', 'machine-type', 'STRING', 'DROPDOWN', NOW(), NOW(), 'system', 'system', false),
    ('Pump Pressure (bar)', 'Maximum pump pressure', 'pressure-bar', 'NUMBER', 'NUMERIC', NOW(), NOW(), 'system', 'system', false),
    ('Milk System', 'Has milk system', 'milk-system', 'BOOLEAN', 'CHECKBOX', NOW(), NOW(), 'system', 'system', false),
    ('Built-in Grinder', 'Has built-in grinder', 'built-in-grinder', 'BOOLEAN', 'CHECKBOX', NOW(), NOW(), 'system', 'system', false),

    ('Accessory Type', 'Accessory type', 'accessory-type', 'STRING', 'DROPDOWN', NOW(), NOW(), 'system', 'system', false),
    ('Material', 'Material', 'material', 'STRING', 'DROPDOWN', NOW(), NOW(), 'system', 'system', false),

    ('Capsule System', 'Compatible system', 'capsule-system', 'STRING', 'DROPDOWN', NOW(), NOW(), 'system', 'system', false),
    ('Intensity', 'Strength/intensity', 'intensity', 'NUMBER', 'SLIDER', NOW(), NOW(), 'system', 'system', false);

-- Attribute Values (strings)
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Light Roast', 'light-roast', 0, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'roast-level';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Medium Roast', 'medium-roast', 1, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'roast-level';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Dark Roast', 'dark-roast', 2, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'roast-level';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Ethiopia', 'ethiopia', 0, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'origin';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Colombia', 'colombia', 1, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'origin';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Kenya', 'kenya', 2, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'origin';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Brazil', 'brazil', 3, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'origin';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Guatemala', 'guatemala', 4, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'origin';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Indonesia', 'indonesia', 5, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'origin';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Washed', 'washed', 0, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'process';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Natural', 'natural', 1, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'process';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Honey', 'honey', 2, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'process';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Chocolate', 'chocolate', 0, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'tasting-notes';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Citrus', 'citrus', 1, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'tasting-notes';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Floral', 'floral', 2, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'tasting-notes';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Nutty', 'nutty', 3, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'tasting-notes';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Berry', 'berry', 4, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'tasting-notes';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Caramel', 'caramel', 5, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'tasting-notes';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Espresso Machine', 'espresso', 0, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'machine-type';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Filter Machine', 'filter', 1, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'machine-type';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Grinder', 'grinder', 2, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'machine-type';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Filters', 'filters', 0, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'accessory-type';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Tamper', 'tamper', 1, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'accessory-type';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Milk Pitcher', 'milk-pitcher', 2, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'accessory-type';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Scale', 'scale', 3, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'accessory-type';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Mug', 'mug', 4, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'accessory-type';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Kettle', 'kettle', 5, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'accessory-type';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Stainless Steel', 'stainless-steel', 0, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'material';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Ceramic', 'ceramic', 1, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'material';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Glass', 'glass', 2, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'material';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Paper', 'paper', 3, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'material';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Plastic', 'plastic', 4, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'material';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Nespresso Original', 'nespresso-original', 0, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'capsule-system';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Dolce Gusto', 'dolce-gusto', 1, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'capsule-system';
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Senseo', 'senseo', 2, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug = 'capsule-system';

-- Attribute Values (booleans)
INSERT INTO attribute_values (attribute_id, boolean_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, true, 'true', 0, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug IN ('organic','milk-system','built-in-grinder');

INSERT INTO attribute_values (attribute_id, boolean_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, false, 'false', 1, NOW(), NOW(), 'system', 'system', false
FROM attributes a WHERE a.slug IN ('organic','milk-system','built-in-grinder');

-- Attribute Values (numeric)
INSERT INTO attribute_values (attribute_id, numeric_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, v.val, v.slug, v.ord, NOW(), NOW(), 'system', 'system', false
FROM attributes a
         JOIN (VALUES (9,'9',0),(15,'15',1),(19,'19',2)) AS v(val,slug,ord) ON true
WHERE a.slug = 'pressure-bar';

INSERT INTO attribute_values (attribute_id, numeric_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, v.val, v.slug, v.ord, NOW(), NOW(), 'system', 'system', false
FROM attributes a
         JOIN (VALUES (3,'3',0),(5,'5',1),(7,'7',2),(9,'9',3),(11,'11',4)) AS v(val,slug,ord) ON true
WHERE a.slug = 'intensity';

-- Category Attributes (root categories)
INSERT INTO category_attributes (category_id, attribute_id, sort_order, required, filterable, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT c.id, a.id, v.sort_order, v.required, v.filterable, NOW(), NOW(), 'system','system', false
FROM categories c
         JOIN (VALUES
                   ('coffee-beans','roast-level',0,true,true),
                   ('coffee-beans','origin',1,true,true),
                   ('coffee-beans','process',2,false,true),
                   ('coffee-beans','tasting-notes',3,false,true),
                   ('coffee-beans','organic',4,false,true),

                   ('coffee-machines','machine-type',0,true,true),
                   ('coffee-machines','pressure-bar',1,false,true),
                   ('coffee-machines','milk-system',2,false,true),
                   ('coffee-machines','built-in-grinder',3,false,true),

                   ('accessories','accessory-type',0,true,true),
                   ('accessories','material',1,false,true),

                   ('pods-capsules','capsule-system',0,true,true),
                   ('pods-capsules','intensity',1,false,true),
                   ('pods-capsules','roast-level',2,false,true)
) AS v(cat_slug, attr_slug, sort_order, required, filterable) ON c.slug = v.cat_slug
         JOIN attributes a ON a.slug = v.attr_slug;

-- Products (23)
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Ethiopian Yirgacheffe', 'Floral, tea-like acidity and jasmine notes.', 'ethiopian-yirgacheffe', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'single-origin';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Kenyan AA', 'Bright citrus with berry sweetness.', 'kenyan-aa', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'single-origin';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Colombian Supremo', 'Balanced, nutty and chocolate finish.', 'colombian-supremo', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'single-origin';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Brazil Santos', 'Low acidity with caramel and cocoa.', 'brazil-santos', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'single-origin';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Guatemala Antigua', 'Spiced chocolate with citrus brightness.', 'guatemala-antigua', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'single-origin';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Sumatra Mandheling', 'Earthy body with dark chocolate notes.', 'sumatra-mandheling', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'single-origin';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'House Espresso Blend', 'Classic espresso blend for milk drinks.', 'house-espresso-blend', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'blends';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Morning Filter Blend', 'Smooth daily-driver blend.', 'morning-filter-blend', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'blends';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Decaf Colombia', 'Decaf with cocoa notes.', 'decaf-colombia', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'decaf';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Decaf Espresso Blend', 'Decaf espresso with rich crema.', 'decaf-espresso-blend', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'decaf';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Maki Espresso Mini', 'Compact 15-bar espresso machine.', 'maki-espresso-mini', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'espresso-machines';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Maki Espresso Pro', '19-bar espresso with PID control.', 'maki-espresso-pro', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'espresso-machines';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Maki Drip Brewer', 'Programmable filter machine.', 'maki-drip-brewer', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'filter-machines';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Maki Burr Grinder', 'Conical burr grinder with 40 steps.', 'maki-burr-grinder', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'grinders';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Maki All-in-One Barista', 'Espresso machine with built-in grinder.', 'maki-all-in-one-barista', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'espresso-machines';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Stainless Tamper 58mm', 'Heavy stainless-steel tamper.', 'tamper-58mm', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'barista-tools';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Milk Pitcher 350ml', 'Stainless pitcher for latte art.', 'milk-pitcher-350', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'barista-tools';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Precision Scale', '0.1g precision coffee scale.', 'precision-scale', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'barista-tools';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Pour-over Kettle', 'Gooseneck kettle for pour-over.', 'pour-over-kettle', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'brewing';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Ceramic Mug 300ml', 'Thick ceramic mug.', 'ceramic-mug-300', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'drinkware';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Nespresso Classic Capsules', 'Nespresso compatible, medium roast.', 'nespresso-classic-capsules', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'nespresso-compatible';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Dolce Gusto Intenso Pods', 'Dolce Gusto compatible, dark roast.', 'dolce-gusto-intenso-pods', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'dolce-gusto-compatible';
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Senseo Mild Pads', 'Senseo compatible mild pads.', 'senseo-mild-pads', c.id, NOW(), NOW(), 'system', 'system', false
FROM categories c WHERE c.slug = 'senseo-compatible';

-- Product Variants
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 1299, 'ETHIOP-250', 80, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'ethiopian-yirgacheffe' AND s.slug = '250g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 2299, 'ETHIOP-500', 50, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'ethiopian-yirgacheffe' AND s.slug = '500g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 3999, 'ETHIOP-1KG', 25, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'ethiopian-yirgacheffe' AND s.slug = '1kg';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 1299, 'KENYAN-250', 80, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'kenyan-aa' AND s.slug = '250g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 2299, 'KENYAN-500', 50, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'kenyan-aa' AND s.slug = '500g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 3999, 'KENYAN-1KG', 25, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'kenyan-aa' AND s.slug = '1kg';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 1299, 'COLOMB-250', 80, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'colombian-supremo' AND s.slug = '250g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 2299, 'COLOMB-500', 50, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'colombian-supremo' AND s.slug = '500g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 3999, 'COLOMB-1KG', 25, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'colombian-supremo' AND s.slug = '1kg';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 1299, 'BRAZIL-250', 80, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'brazil-santos' AND s.slug = '250g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 2299, 'BRAZIL-500', 50, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'brazil-santos' AND s.slug = '500g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 3999, 'BRAZIL-1KG', 25, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'brazil-santos' AND s.slug = '1kg';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 1299, 'GUATEM-250', 80, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'guatemala-antigua' AND s.slug = '250g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 2299, 'GUATEM-500', 50, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'guatemala-antigua' AND s.slug = '500g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 3999, 'GUATEM-1KG', 25, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'guatemala-antigua' AND s.slug = '1kg';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 1299, 'SUMATR-250', 80, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'sumatra-mandheling' AND s.slug = '250g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 2299, 'SUMATR-500', 50, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'sumatra-mandheling' AND s.slug = '500g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 3999, 'SUMATR-1KG', 25, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'sumatra-mandheling' AND s.slug = '1kg';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 1299, 'HOUSEE-250', 80, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'house-espresso-blend' AND s.slug = '250g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 2299, 'HOUSEE-500', 50, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'house-espresso-blend' AND s.slug = '500g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 3999, 'HOUSEE-1KG', 25, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'house-espresso-blend' AND s.slug = '1kg';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 1299, 'MORNIN-250', 80, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'morning-filter-blend' AND s.slug = '250g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 2299, 'MORNIN-500', 50, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'morning-filter-blend' AND s.slug = '500g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 3999, 'MORNIN-1KG', 25, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'morning-filter-blend' AND s.slug = '1kg';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 1299, 'DECAFC-250', 80, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'decaf-colombia' AND s.slug = '250g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 2299, 'DECAFC-500', 50, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'decaf-colombia' AND s.slug = '500g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 3999, 'DECAFC-1KG', 25, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'decaf-colombia' AND s.slug = '1kg';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 1299, 'DECAFE-250', 80, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'decaf-espresso-blend' AND s.slug = '250g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 2299, 'DECAFE-500', 50, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'decaf-espresso-blend' AND s.slug = '500g';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 3999, 'DECAFE-1KG', 25, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'decaf-espresso-blend' AND s.slug = '1kg';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 19900, 'MAKIESPR-STD', 15, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'maki-espresso-mini' AND s.slug = 'standard';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 22900, 'MAKIESPR-PLXS', 10, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'maki-espresso-mini' AND s.slug = 'plus';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 49900, 'MAKIESPR-STE', 15, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'maki-espresso-pro' AND s.slug = 'standard';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 54900, 'MAKIESPR-PLUS', 10, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'maki-espresso-pro' AND s.slug = 'plus';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 59900, 'MAKIESPR-PRO', 6, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'maki-espresso-pro' AND s.slug = 'pro';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 12900, 'MAKIDRIP-STD', 15, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'maki-drip-brewer' AND s.slug = 'standard';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 14900, 'MAKIDRIP-PLUS', 10, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'maki-drip-brewer' AND s.slug = 'plus';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 17900, 'MAKIBURR-STD', 15, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'maki-burr-grinder' AND s.slug = 'standard';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 19900, 'MAKIBURR-PLUS', 10, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'maki-burr-grinder' AND s.slug = 'plus';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 69900, 'MAKIALLI-STD', 15, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'maki-all-in-one-barista' AND s.slug = 'standard';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 74900, 'MAKIALLI-PLUS', 10, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'maki-all-in-one-barista' AND s.slug = 'plus';
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 79900, 'MAKIALLI-PRO', 6, false, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'maki-all-in-one-barista' AND s.slug = 'pro';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 2999, 'TAMPER58MM-S', 100, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'tamper-58mm' AND s.slug = 'single';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 1999, 'MILKPITCHE-S', 100, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'milk-pitcher-350' AND s.slug = 'single';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 3499, 'PRECISIONS-S', 100, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'precision-scale' AND s.slug = 'single';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 4999, 'POUROVERKE-S', 100, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'pour-over-kettle' AND s.slug = 'single';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 1599, 'CERAMICMUG-S', 100, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'ceramic-mug-300' AND s.slug = 'single';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 899, 'NESPRESSOC-P', 200, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'nespresso-classic-capsules' AND s.slug = 'single';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 1099, 'DOLCEGUSTO-P', 200, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'dolce-gusto-intenso-pods' AND s.slug = 'single';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, is_default, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 799, 'SENSEOMILD-P', 200, true, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'senseo-mild-pads' AND s.slug = 'single';


-- Product Attributes & Values
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'roast-level'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'ethiopian-yirgacheffe';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'medium-roast'
WHERE p.slug = 'ethiopian-yirgacheffe' AND a.slug = 'roast-level';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'origin'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'ethiopian-yirgacheffe';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'ethiopia'
WHERE p.slug = 'ethiopian-yirgacheffe' AND a.slug = 'origin';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'process'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'ethiopian-yirgacheffe';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'washed'
WHERE p.slug = 'ethiopian-yirgacheffe' AND a.slug = 'process';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'tasting-notes'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'ethiopian-yirgacheffe';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'floral'
WHERE p.slug = 'ethiopian-yirgacheffe' AND a.slug = 'tasting-notes';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'citrus'
WHERE p.slug = 'ethiopian-yirgacheffe' AND a.slug = 'tasting-notes';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'organic'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'ethiopian-yirgacheffe';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'true'
WHERE p.slug = 'ethiopian-yirgacheffe' AND a.slug = 'organic';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'roast-level'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'kenyan-aa';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'light-roast'
WHERE p.slug = 'kenyan-aa' AND a.slug = 'roast-level';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'origin'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'kenyan-aa';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'kenya'
WHERE p.slug = 'kenyan-aa' AND a.slug = 'origin';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'process'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'kenyan-aa';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'washed'
WHERE p.slug = 'kenyan-aa' AND a.slug = 'process';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'tasting-notes'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'kenyan-aa';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'berry'
WHERE p.slug = 'kenyan-aa' AND a.slug = 'tasting-notes';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'citrus'
WHERE p.slug = 'kenyan-aa' AND a.slug = 'tasting-notes';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'organic'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'kenyan-aa';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'kenyan-aa' AND a.slug = 'organic';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'roast-level'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'colombian-supremo';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'medium-roast'
WHERE p.slug = 'colombian-supremo' AND a.slug = 'roast-level';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'origin'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'colombian-supremo';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'colombia'
WHERE p.slug = 'colombian-supremo' AND a.slug = 'origin';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'process'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'colombian-supremo';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'washed'
WHERE p.slug = 'colombian-supremo' AND a.slug = 'process';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'tasting-notes'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'colombian-supremo';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'chocolate'
WHERE p.slug = 'colombian-supremo' AND a.slug = 'tasting-notes';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'nutty'
WHERE p.slug = 'colombian-supremo' AND a.slug = 'tasting-notes';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'organic'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'colombian-supremo';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'colombian-supremo' AND a.slug = 'organic';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'roast-level'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'brazil-santos';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'dark-roast'
WHERE p.slug = 'brazil-santos' AND a.slug = 'roast-level';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'origin'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'brazil-santos';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'brazil'
WHERE p.slug = 'brazil-santos' AND a.slug = 'origin';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'process'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'brazil-santos';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'natural'
WHERE p.slug = 'brazil-santos' AND a.slug = 'process';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'tasting-notes'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'brazil-santos';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'caramel'
WHERE p.slug = 'brazil-santos' AND a.slug = 'tasting-notes';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'chocolate'
WHERE p.slug = 'brazil-santos' AND a.slug = 'tasting-notes';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'organic'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'brazil-santos';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'brazil-santos' AND a.slug = 'organic';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'roast-level'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'guatemala-antigua';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'medium-roast'
WHERE p.slug = 'guatemala-antigua' AND a.slug = 'roast-level';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'origin'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'guatemala-antigua';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'guatemala'
WHERE p.slug = 'guatemala-antigua' AND a.slug = 'origin';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'process'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'guatemala-antigua';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'honey'
WHERE p.slug = 'guatemala-antigua' AND a.slug = 'process';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'tasting-notes'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'guatemala-antigua';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'chocolate'
WHERE p.slug = 'guatemala-antigua' AND a.slug = 'tasting-notes';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'citrus'
WHERE p.slug = 'guatemala-antigua' AND a.slug = 'tasting-notes';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'organic'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'guatemala-antigua';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'true'
WHERE p.slug = 'guatemala-antigua' AND a.slug = 'organic';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'roast-level'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'sumatra-mandheling';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'dark-roast'
WHERE p.slug = 'sumatra-mandheling' AND a.slug = 'roast-level';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'origin'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'sumatra-mandheling';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'indonesia'
WHERE p.slug = 'sumatra-mandheling' AND a.slug = 'origin';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'process'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'sumatra-mandheling';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'natural'
WHERE p.slug = 'sumatra-mandheling' AND a.slug = 'process';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'tasting-notes'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'sumatra-mandheling';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'chocolate'
WHERE p.slug = 'sumatra-mandheling' AND a.slug = 'tasting-notes';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'organic'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'sumatra-mandheling';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'sumatra-mandheling' AND a.slug = 'organic';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'roast-level'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'house-espresso-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'dark-roast'
WHERE p.slug = 'house-espresso-blend' AND a.slug = 'roast-level';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'origin'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'house-espresso-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'brazil'
WHERE p.slug = 'house-espresso-blend' AND a.slug = 'origin';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'colombia'
WHERE p.slug = 'house-espresso-blend' AND a.slug = 'origin';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'process'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'house-espresso-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'natural'
WHERE p.slug = 'house-espresso-blend' AND a.slug = 'process';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'tasting-notes'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'house-espresso-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'chocolate'
WHERE p.slug = 'house-espresso-blend' AND a.slug = 'tasting-notes';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'caramel'
WHERE p.slug = 'house-espresso-blend' AND a.slug = 'tasting-notes';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'organic'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'house-espresso-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'house-espresso-blend' AND a.slug = 'organic';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'roast-level'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'morning-filter-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'medium-roast'
WHERE p.slug = 'morning-filter-blend' AND a.slug = 'roast-level';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'origin'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'morning-filter-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'colombia'
WHERE p.slug = 'morning-filter-blend' AND a.slug = 'origin';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'guatemala'
WHERE p.slug = 'morning-filter-blend' AND a.slug = 'origin';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'process'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'morning-filter-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'washed'
WHERE p.slug = 'morning-filter-blend' AND a.slug = 'process';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'tasting-notes'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'morning-filter-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'nutty'
WHERE p.slug = 'morning-filter-blend' AND a.slug = 'tasting-notes';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'caramel'
WHERE p.slug = 'morning-filter-blend' AND a.slug = 'tasting-notes';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'organic'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'morning-filter-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'morning-filter-blend' AND a.slug = 'organic';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'roast-level'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'decaf-colombia';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'medium-roast'
WHERE p.slug = 'decaf-colombia' AND a.slug = 'roast-level';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'origin'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'decaf-colombia';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'colombia'
WHERE p.slug = 'decaf-colombia' AND a.slug = 'origin';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'process'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'decaf-colombia';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'washed'
WHERE p.slug = 'decaf-colombia' AND a.slug = 'process';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'tasting-notes'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'decaf-colombia';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'chocolate'
WHERE p.slug = 'decaf-colombia' AND a.slug = 'tasting-notes';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'organic'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'decaf-colombia';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'decaf-colombia' AND a.slug = 'organic';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'roast-level'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'decaf-espresso-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'dark-roast'
WHERE p.slug = 'decaf-espresso-blend' AND a.slug = 'roast-level';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'origin'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'decaf-espresso-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'brazil'
WHERE p.slug = 'decaf-espresso-blend' AND a.slug = 'origin';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'colombia'
WHERE p.slug = 'decaf-espresso-blend' AND a.slug = 'origin';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'process'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'decaf-espresso-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'natural'
WHERE p.slug = 'decaf-espresso-blend' AND a.slug = 'process';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'tasting-notes'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'decaf-espresso-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'chocolate'
WHERE p.slug = 'decaf-espresso-blend' AND a.slug = 'tasting-notes';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-beans'
         JOIN attributes a ON a.slug = 'organic'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'decaf-espresso-blend';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'decaf-espresso-blend' AND a.slug = 'organic';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'machine-type'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-espresso-mini';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'espresso'
WHERE p.slug = 'maki-espresso-mini' AND a.slug = 'machine-type';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'pressure-bar'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-espresso-mini';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = '15'
WHERE p.slug = 'maki-espresso-mini' AND a.slug = 'pressure-bar';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'milk-system'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-espresso-mini';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'maki-espresso-mini' AND a.slug = 'milk-system';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'built-in-grinder'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-espresso-mini';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'maki-espresso-mini' AND a.slug = 'built-in-grinder';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'machine-type'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-espresso-pro';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'espresso'
WHERE p.slug = 'maki-espresso-pro' AND a.slug = 'machine-type';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'pressure-bar'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-espresso-pro';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = '19'
WHERE p.slug = 'maki-espresso-pro' AND a.slug = 'pressure-bar';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'milk-system'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-espresso-pro';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'maki-espresso-pro' AND a.slug = 'milk-system';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'built-in-grinder'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-espresso-pro';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'maki-espresso-pro' AND a.slug = 'built-in-grinder';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'machine-type'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-drip-brewer';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'filter'
WHERE p.slug = 'maki-drip-brewer' AND a.slug = 'machine-type';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'pressure-bar'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-drip-brewer';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = '9'
WHERE p.slug = 'maki-drip-brewer' AND a.slug = 'pressure-bar';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'milk-system'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-drip-brewer';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'maki-drip-brewer' AND a.slug = 'milk-system';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'built-in-grinder'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-drip-brewer';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'maki-drip-brewer' AND a.slug = 'built-in-grinder';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'machine-type'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-burr-grinder';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'grinder'
WHERE p.slug = 'maki-burr-grinder' AND a.slug = 'machine-type';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'pressure-bar'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-burr-grinder';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = '9'
WHERE p.slug = 'maki-burr-grinder' AND a.slug = 'pressure-bar';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'milk-system'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-burr-grinder';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'false'
WHERE p.slug = 'maki-burr-grinder' AND a.slug = 'milk-system';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'built-in-grinder'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-burr-grinder';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'true'
WHERE p.slug = 'maki-burr-grinder' AND a.slug = 'built-in-grinder';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'machine-type'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-all-in-one-barista';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'espresso'
WHERE p.slug = 'maki-all-in-one-barista' AND a.slug = 'machine-type';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'pressure-bar'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-all-in-one-barista';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = '19'
WHERE p.slug = 'maki-all-in-one-barista' AND a.slug = 'pressure-bar';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'milk-system'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-all-in-one-barista';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'true'
WHERE p.slug = 'maki-all-in-one-barista' AND a.slug = 'milk-system';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'coffee-machines'
         JOIN attributes a ON a.slug = 'built-in-grinder'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'maki-all-in-one-barista';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'true'
WHERE p.slug = 'maki-all-in-one-barista' AND a.slug = 'built-in-grinder';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'accessories'
         JOIN attributes a ON a.slug = 'accessory-type'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'tamper-58mm';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'tamper'
WHERE p.slug = 'tamper-58mm' AND a.slug = 'accessory-type';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'accessories'
         JOIN attributes a ON a.slug = 'material'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'tamper-58mm';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'stainless-steel'
WHERE p.slug = 'tamper-58mm' AND a.slug = 'material';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'accessories'
         JOIN attributes a ON a.slug = 'accessory-type'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'milk-pitcher-350';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'milk-pitcher'
WHERE p.slug = 'milk-pitcher-350' AND a.slug = 'accessory-type';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'accessories'
         JOIN attributes a ON a.slug = 'material'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'milk-pitcher-350';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'stainless-steel'
WHERE p.slug = 'milk-pitcher-350' AND a.slug = 'material';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'accessories'
         JOIN attributes a ON a.slug = 'accessory-type'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'precision-scale';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'scale'
WHERE p.slug = 'precision-scale' AND a.slug = 'accessory-type';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'accessories'
         JOIN attributes a ON a.slug = 'material'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'precision-scale';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'plastic'
WHERE p.slug = 'precision-scale' AND a.slug = 'material';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'accessories'
         JOIN attributes a ON a.slug = 'accessory-type'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'pour-over-kettle';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'kettle'
WHERE p.slug = 'pour-over-kettle' AND a.slug = 'accessory-type';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'accessories'
         JOIN attributes a ON a.slug = 'material'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'pour-over-kettle';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'stainless-steel'
WHERE p.slug = 'pour-over-kettle' AND a.slug = 'material';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'accessories'
         JOIN attributes a ON a.slug = 'accessory-type'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'ceramic-mug-300';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'mug'
WHERE p.slug = 'ceramic-mug-300' AND a.slug = 'accessory-type';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'accessories'
         JOIN attributes a ON a.slug = 'material'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'ceramic-mug-300';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'ceramic'
WHERE p.slug = 'ceramic-mug-300' AND a.slug = 'material';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'pods-capsules'
         JOIN attributes a ON a.slug = 'capsule-system'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'nespresso-classic-capsules';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'nespresso-original'
WHERE p.slug = 'nespresso-classic-capsules' AND a.slug = 'capsule-system';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'pods-capsules'
         JOIN attributes a ON a.slug = 'intensity'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'nespresso-classic-capsules';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = '7'
WHERE p.slug = 'nespresso-classic-capsules' AND a.slug = 'intensity';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'pods-capsules'
         JOIN attributes a ON a.slug = 'roast-level'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'nespresso-classic-capsules';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'medium-roast'
WHERE p.slug = 'nespresso-classic-capsules' AND a.slug = 'roast-level';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'pods-capsules'
         JOIN attributes a ON a.slug = 'capsule-system'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'dolce-gusto-intenso-pods';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'dolce-gusto'
WHERE p.slug = 'dolce-gusto-intenso-pods' AND a.slug = 'capsule-system';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'pods-capsules'
         JOIN attributes a ON a.slug = 'intensity'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'dolce-gusto-intenso-pods';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = '9'
WHERE p.slug = 'dolce-gusto-intenso-pods' AND a.slug = 'intensity';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'pods-capsules'
         JOIN attributes a ON a.slug = 'roast-level'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'dolce-gusto-intenso-pods';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'dark-roast'
WHERE p.slug = 'dolce-gusto-intenso-pods' AND a.slug = 'roast-level';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'pods-capsules'
         JOIN attributes a ON a.slug = 'capsule-system'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'senseo-mild-pads';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'senseo'
WHERE p.slug = 'senseo-mild-pads' AND a.slug = 'capsule-system';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'pods-capsules'
         JOIN attributes a ON a.slug = 'intensity'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'senseo-mild-pads';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = '5'
WHERE p.slug = 'senseo-mild-pads' AND a.slug = 'intensity';
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p
         JOIN categories c ON c.slug = 'pods-capsules'
         JOIN attributes a ON a.slug = 'roast-level'
         JOIN category_attributes ca ON ca.category_id = c.id AND ca.attribute_id = a.id
WHERE p.slug = 'senseo-mild-pads';
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa
         JOIN products p ON p.id = pa.product_id
         JOIN category_attributes ca ON ca.id = pa.category_attribute_id
         JOIN attributes a ON a.id = ca.attribute_id
         JOIN attribute_values av ON av.attribute_id = a.id AND av.slug = 'light-roast'
WHERE p.slug = 'senseo-mild-pads' AND a.slug = 'roast-level';

COMMIT;
