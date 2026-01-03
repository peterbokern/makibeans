-- Add slug column to sizes and populate it from the existing name
-- NOTE: keep this migration small and compatible with common SQL dialects.

ALTER TABLE sizes ADD COLUMN slug varchar(120);

-- Backfill: lowercase and replace spaces with dash. This is a conservative, widely-compatible approach.
UPDATE sizes SET slug = LOWER(REPLACE(name, ' ', '-')) WHERE slug IS NULL;

-- Make slug unique at the DB level to guard against races.
CREATE UNIQUE INDEX ux_sizes_slug ON sizes (slug);

-- If you prefer a NOT NULL constraint, add it after you've verified slugs are populated:
-- ALTER TABLE sizes ALTER COLUMN slug SET NOT NULL;
