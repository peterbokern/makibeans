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
INSERT INTO categories (name, description, created_at, updated_at, created_by, updated_by)
VALUES
    ('Coffee Beans', 'All roasted coffee bean products.', NOW(), NOW(), 'system', 'system'),
    ('Coffee Machines', 'Espresso and filter coffee machines.', NOW(), NOW(), 'system', 'system'),
    ('Accessories', 'Cups, filters, and other accessories.', NOW(), NOW(), 'system', 'system'),
    ('Pods & Capsules', 'Coffee pods and compatible capsules.', NOW(), NOW(), 'system', 'system');

-- ============================
-- Seed global attributes
-- ============================
