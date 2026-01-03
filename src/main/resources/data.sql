ALTER TABLE users
    ALTER COLUMN is_deleted SET DEFAULT false;
UPDATE users SET is_deleted = false WHERE is_deleted IS NULL;


UPDATE categories SET is_deleted = FALSE WHERE is_deleted IS NULL;
ALTER TABLE categories ALTER COLUMN is_deleted SET DEFAULT FALSE;
ALTER TABLE categories ALTER COLUMN is_deleted SET NOT NULL;

UPDATE attributes SET is_deleted = FALSE WHERE is_deleted IS NULL;
ALTER TABLE attributes ALTER COLUMN is_deleted SET DEFAULT FALSE;
ALTER TABLE attributes ALTER COLUMN is_deleted SET NOT NULL;

-- ============================
-- Seed initial categories (explicit VALUES as requested)
-- ============================
INSERT INTO categories (name, slug, description, created_at, updated_at, created_by, updated_by)
VALUES
    ('Coffee Beans', 'coffee-beans', 'All roasted coffee bean products.', NOW(), NOW(), 'system', 'system'),
    ('Coffee Machines', 'coffee-machines', 'Espresso and filter coffee machines.', NOW(), NOW(), 'system', 'system'),
    ('Accessories', 'accessories', 'Cups, filters, and other accessories.', NOW(), NOW(), 'system', 'system'),
    ('Pods & Capsules', 'pods-capsules', 'Coffee pods and compatible capsules.', NOW(), NOW(), 'system', 'system');
-- ============================

/*-- Roles
INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');
*/
-- Users (basic seeded users)
/*INSERT INTO users (username, email, password, enabled, locked, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES
  ('maki_admin', 'admin@example.com', 'password', true, false, NOW(), NOW(), 'system', 'system', false),
  ('maki_user', 'user@example.com', 'password', true, false, NOW(), NOW(), 'system', 'system', false);

-- Assign roles to users using sub-selects to resolve IDs
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'maki_admin' AND r.name = 'ROLE_ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'maki_admin' AND r.name = 'ROLE_USER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'maki_user' AND r.name = 'ROLE_USER';*/

-- Sizes (lookup values)
INSERT INTO sizes (name, slug, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES
  ('Small', 'small', NOW(), NOW(), 'system', 'system', false),
  ('Medium', 'medium', NOW(), NOW(), 'system', 'system', false),
  ('Large', 'large', NOW(), NOW(), 'system', 'system', false),
  ('250g', '250g', NOW(), NOW(), 'system', 'system', false),
  ('500g', '500g', NOW(), NOW(), 'system', 'system', false);

-- Attributes (catalog)
INSERT INTO attributes (name, description, slug, data_type, input_type, created_at, updated_at, created_by, updated_by, is_deleted)
VALUES
  ('Roast Level', 'Degree of roast for coffee beans', 'roast-level', 'STRING', 'DROPDOWN', NOW(), NOW(), 'system', 'system', false),
  ('Weight (grams)', 'Weight in grams', 'weight-grams', 'NUMBER', 'NUMERIC', NOW(), NOW(), 'system', 'system', false),
  ('Organic', 'Whether the product is certified organic', 'organic', 'BOOLEAN', 'CHECKBOX', NOW(), NOW(), 'system', 'system', false),
  ('Roast Date', 'Date when beans were roasted', 'roast-date', 'DATE', 'DATE_PICKER', NOW(), NOW(), 'system', 'system', false);

-- Attribute values for Roast Level
INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Light', 'light', 0, NOW(), NOW(), 'system', 'system', false FROM attributes a WHERE a.slug = 'roast-level';

INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Medium', 'medium-roast', 1, NOW(), NOW(), 'system', 'system', false FROM attributes a WHERE a.slug = 'roast-level';

INSERT INTO attribute_values (attribute_id, string_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 'Dark', 'dark-roast', 2, NOW(), NOW(), 'system', 'system', false FROM attributes a WHERE a.slug = 'roast-level';

-- Example numeric attribute value (weight)
INSERT INTO attribute_values (attribute_id, numeric_value, slug, sort_order, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT a.id, 250, '250g', 0, NOW(), NOW(), 'system', 'system', false FROM attributes a WHERE a.slug = 'weight-grams';

-- Link attributes to category: Coffee Beans
INSERT INTO category_attributes (category_id, attribute_id, sort_order, required, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT c.id, a.id, 0, true, NOW(), NOW(), 'system', 'system', false
FROM categories c, attributes a
WHERE c.slug = 'coffee-beans' AND a.slug = 'roast-level';

INSERT INTO category_attributes (category_id, attribute_id, sort_order, required, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT c.id, a.id, 1, false, NOW(), NOW(), 'system', 'system', false
FROM categories c, attributes a
WHERE c.slug = 'coffee-beans' AND a.slug = 'weight-grams';

INSERT INTO category_attributes (category_id, attribute_id, sort_order, required, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT c.id, a.id, 2, false, NOW(), NOW(), 'system', 'system', false
FROM categories c, attributes a
WHERE c.slug = 'coffee-beans' AND a.slug = 'organic';

INSERT INTO category_attributes (category_id, attribute_id, sort_order, required, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT c.id, a.id, 3, false, NOW(), NOW(), 'system', 'system', false
FROM categories c, attributes a
WHERE c.slug = 'coffee-beans' AND a.slug = 'roast-date';

-- Subcategories (use parent_category resolved by slug)
INSERT INTO categories (name, slug, description, parent_category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Dark Roast', 'dark-roast', 'Dark roast coffees.', c.id, NOW(), NOW(), 'system', 'system', false FROM categories c WHERE c.slug = 'coffee-beans';

INSERT INTO categories (name, slug, description, parent_category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Light Roast', 'light-roast', 'Light roast coffees.', c.id, NOW(), NOW(), 'system', 'system', false FROM categories c WHERE c.slug = 'coffee-beans';

INSERT INTO categories (name, slug, description, parent_category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Espresso Machines', 'espresso-machines', 'Espresso machines and accessories.', c.id, NOW(), NOW(), 'system', 'system', false FROM categories c WHERE c.slug = 'coffee-machines';

-- Products
INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Ethiopian Yirgacheffe', 'Floral and bright single-origin Ethiopian coffee.', 'ethiopian-yirgacheffe', c.id, NOW(), NOW(), 'system', 'system', false FROM categories c WHERE c.slug = 'coffee-beans';

INSERT INTO products (name, description, slug, category_id, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT 'Colombian Supremo', 'Balanced and nutty Colombian roast.', 'colombian-supremo', c.id, NOW(), NOW(), 'system', 'system', false FROM categories c WHERE c.slug = 'coffee-beans';

-- Product variants (link sizes)
INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 1299, 'ETH-YIRG-250', 50, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'ethiopian-yirgacheffe' AND s.slug = '250g';

INSERT INTO product_variants (product_id, size_id, price_in_cents, sku, stock, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, s.id, 999, 'COL-SUP-250', 80, NOW(), NOW(), 'system', 'system', false
FROM products p, sizes s
WHERE p.slug = 'colombian-supremo' AND s.slug = '250g';

-- Product attributes wiring (attach product to category_attribute)
INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p, category_attributes ca, categories c, attributes a
WHERE p.slug = 'ethiopian-yirgacheffe' AND c.slug = 'coffee-beans' AND a.slug = 'roast-level' AND ca.category_id = c.id AND ca.attribute_id = a.id;

INSERT INTO product_attributes (product_id, category_attribute_id, visible, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT p.id, ca.id, true, NOW(), NOW(), 'system', 'system', false
FROM products p, category_attributes ca, categories c, attributes a
WHERE p.slug = 'colombian-supremo' AND c.slug = 'coffee-beans' AND a.slug = 'roast-level' AND ca.category_id = c.id AND ca.attribute_id = a.id;

-- Product attribute values (link a dropdown attribute value to the product attribute)
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, av.id, NULL, NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa, products p, category_attributes ca, attribute_values av, attributes a, categories c
WHERE p.slug = 'ethiopian-yirgacheffe' AND c.slug = 'coffee-beans' AND a.slug = 'roast-level' AND ca.category_id = c.id AND ca.attribute_id = a.id
  AND pa.product_id = p.id AND pa.category_attribute_id = ca.id AND av.slug = 'medium-roast' AND av.attribute_id = a.id;

-- Example free-text product attribute (raw_value)
INSERT INTO product_attribute_values (product_attribute_id, attribute_value_id, raw_value, created_at, updated_at, created_by, updated_by, is_deleted)
SELECT pa.id, NULL, 'Freshly roasted, single-origin', NOW(), NOW(), 'system', 'system', false
FROM product_attributes pa, products p, category_attributes ca, categories c
WHERE p.slug = 'colombian-supremo' AND c.slug = 'coffee-beans' AND ca.category_id = c.id AND pa.product_id = p.id AND pa.category_attribute_id = ca.id;

-- End of mock data
