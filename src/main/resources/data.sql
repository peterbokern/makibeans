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
-- Seed initial categories
-- ============================
INSERT INTO categories (id, name, description, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 'Coffee Beans', 'All roasted coffee bean products.', NOW(), NOW(), 'system', 'system'),
    (2, 'Coffee Machines', 'Espresso and filter coffee machines.', NOW(), NOW(), 'system', 'system'),
    (3, 'Accessories', 'Cups, filters, and other accessories.', NOW(), NOW(), 'system', 'system'),
    (4, 'Pods & Capsules', 'Coffee pods and compatible capsules.', NOW(), NOW(), 'system', 'system');

-- ============================
-- Seed global attributes
-- ============================
INSERT INTO attributes (id, name, description, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 'Origin', 'Country or region where the coffee beans were grown.', NOW(), NOW(), 'system', 'system'),
    (2, 'Roast Level', 'Indicates how dark the beans were roasted.', NOW(), NOW(), 'system', 'system'),
    (3, 'Flavor Notes', 'Common flavor notes such as chocolate, citrus, or nutty.', NOW(), NOW(), 'system', 'system'),
    (4, 'Grind Size', 'Fine, medium, or coarse grind specification.', NOW(), NOW(), 'system', 'system'),
    (5, 'Brand', 'Manufacturer or brand name.', NOW(), NOW(), 'system', 'system'),
    (6, 'Color', 'Primary product color (for machines/accessories).', NOW(), NOW(), 'system', 'system'),
    (7, 'Material', 'Main material composition (plastic, metal, glass).', NOW(), NOW(), 'system', 'system');
