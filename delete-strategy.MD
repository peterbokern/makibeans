## Entity lifecycle & deletion strategy

This section describes how Makibeans handles deletion, restoration, and usage checks for the attribute system and other core lookup entities.
The short version:

- **Global/config objects** (Attribute, AttributeValue, CategoryAttribute, Size, Category, Role)  
  → **soft delete + usage checks**
- **Per-product wiring** (ProductAttribute, ProductAttributeValue, ProductVariant)  
  → **hard delete + cascade**, no usage checks
- **Products** — treated as soft-delete (catalog-level object); its wiring (variants, attributes) are managed according to the rules below.

This keeps the global catalogue safe and auditable, while keeping product-specific wiring simple and disposable.

---

### Overview table

| Entity                  | Delete type       | Service behaviour (delete)                                                                                 | Related entities                                                                                           | Restore? |
|-------------------------|-------------------|-----------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------|----------|
| `Attribute`             | Soft delete       | Mark `deleted = true` after verifying it’s not in use                                                    | `AttributeValue`, `CategoryAttribute`, `ProductAttribute` protect the Attribute via a usage checker       | ✅        |
| `AttributeValue`        | Soft delete       | Mark `deleted = true` after verifying it’s not in use                                                    | `ProductAttributeValue` links protect the value via a usage checker                                       | ✅        |
| `CategoryAttribute`     | Soft delete       | Mark `deleted = true` after verifying it’s not used by any `ProductAttribute`                            | `ProductAttribute` protects CategoryAttribute via existence check                                         | ✅        |
| `Size`                  | Soft delete       | Mark `deleted = true` after verifying it’s not used by any `ProductVariant`                              | `ProductVariant` protects Size via existence check                                                        | ✅        |
| `Category`              | Soft delete       | Mark `deleted = true` after verifying no active products or category-attributes depend on it              | `Product`, `CategoryAttribute`                                                                               | ✅        |
| `Product`               | Soft delete       | Mark `deleted = true`; before delete ensure business rules (orders/archival) and optionally cascade flags | `ProductVariant`, `ProductAttribute`                                                                        | ✅        |
| `ProductVariant`        | Hard delete       | Treated as per-product wiring: delete rows (cascade from product or direct delete)                        | `Product`, `Size`                                                                                           | ❌        |
| `ProductAttribute`      | Hard delete       | Physically delete row; values are deleted by JPA cascade / DB cascade                                    | All child `ProductAttributeValue` rows are removed automatically                                          | ❌        |
| `ProductAttributeValue` | Hard delete       | Physically delete row                                                                                     | Underlying `AttributeValue` stays; uniqueness and “in use” checks live on the AttributeValue side         | ❌        |
| `Role`                  | Soft delete       | Mark `deleted = true` and prevent removal while users still reference the role                           | `User`                                                                                                      | ✅        |
| `User`                  | Soft delete       | Prefer soft delete / disabled flag for auditability; ensure dependent sessions and auth are handled       | `Role`, `Order` etc.                                                                                        | ✅        |

---

### Core concepts

Below are clarifications that expand the rules above and explain why certain entities are soft-deleted while others are hard-deleted.

#### Lookup / catalogue entities (soft delete)

The following entities extend `Auditable` and use the `deleted` flag. These are global configuration or lookup data that other objects (products, variants, wiring) depend on.

- `Attribute`
- `AttributeValue`
- `CategoryAttribute`
- `Size` (and other lookup tables: Color, Material, Brand, etc.)
- `Category`
- `Role`

For these, the service layer should not physically remove rows. Instead:

- `delete(id)`:
    - Loads the entity (`getById`).
    - Returns early if already `deleted = true` (idempotent).
    - Runs an **“in use”** check that queries only active/non-deleted dependents (e.g. `existsBySizeAndDeletedFalse(...)`, `existsByProductAttribute...`).
    - Sets `deleted = true` when safe.
- `restore(id)`:
    - Ensures the entity **is** marked as deleted.
    - Re-validates **uniqueness** constraints (e.g. slug + owning parent, `(category, attribute)` pair, `(attribute, slug)` for values).
    - Sets `deleted = false`.

This workflow gives:

- **Auditability** – history remains in the database.
- **Safety** – you cannot accidentally break products by removing attributes or values that are still in use.
- **Consistent uniqueness rules** – restoring an old record cannot silently create duplicate names/slugs.

##### Size (and other simple lookups)

- Soft-deleted with `size.setDeleted(true)`.
- Before delete:
    - Check `productVariantRepository.existsBySizeAndDeletedFalse(...)` (or `existsBySizeIdAndProductDeletedFalse(...)`) to determine usage in active products.
- Before restore:
    - Ensure name/slug or other uniqueness keys do not conflict with an existing active lookup value.

##### Category

- Soft-deleted with `category.setDeleted(true)`.
- Before delete:
    - Ensure no active products belong to the category (or require a business-logic migration/retagging step), and no active `CategoryAttribute` blocks deletion.
- Before restore:
    - Validate uniqueness (slug) and that any category-scoped constraints still hold.

##### Role / User

- Roles are soft-deleted and protected while any `User` still references them. Prefer disabling a role in the UI and preventing deletion until unassigned.
- Users are usually soft-deleted (or disabled) rather than hard-removed. This preserves audit trails and authentication history. Deleting a user should also handle session revocation, tokens and any privacy-related workflows (GDPR) separately.

---

#### Hard delete (per-product wiring)

The following entities are treated as disposable wiring between products and the global catalogue:

- `ProductAttribute`
- `ProductAttributeValue`
- `ProductVariant`

These are safe to hard delete because the actual catalogued data lives in `Attribute` / `AttributeValue` / `CategoryAttribute` / `Size`.

##### ProductVariant

Represents a concrete SKU for a `Product` (often links to `Size`, `Color`, etc.).

- On delete:
    - `repo.delete(productVariant)` is called directly, or the variant is removed via cascade when the owning `Product` is hard-deleted.
    - Because `Product` is soft-deleted in this project, removing an individual `ProductVariant` should be an explicit hard delete operation (variants are replaceable and not audited as configuration data).
- No restore API is provided by default; recreate if needed.

##### ProductAttribute / ProductAttributeValue

(unchanged) These are wiring rows representing which catalogued attribute/attribute-value a product uses. They can be deleted cheaply and recreated.

---

### Practical guidance for implementation

- Keep **usage checks** focused on non-deleted dependents (e.g. `existsByXAndDeletedFalse(...)`) so deleted catalog entries do not block unrelated operations.
- Use stable unique keys (slug) for public lookups and uniqueness checks; validate slug uniqueness among non-deleted rows during create/update/restore.
- Public controllers and APIs should generally return non-deleted items only (e.g. `findByIdAndDeletedFalse`, `findAll(..., deleted = false)` unless an `includeDeleted` flag is explicitly requested and authorized).
- For entities that are soft deleted, prefer `restore(...)` that validates uniqueness again before flipping `deleted = false`.
- Document the strategy (this file) and add tests covering delete/restore/usage-check scenarios for lookup data and wiring data.

---

### Why this split?

- **Global catalogue & configuration** (what attributes exist, what values they can take, which attributes apply to which categories)  
  must be *safe*, *auditable*, and *protected against accidental removal*  
  → soft delete + usage checks + uniqueness checks on restore.

- **Per-product wiring** (which values a specific product currently uses)  
  changes more often, is less critical from a data-model perspective, and can safely be recreated  
  → hard delete, cascaded, no restore support.

This combination keeps the model:

- **Robust** for long-term growth (you can add more usage details later without breaking the contract),
- **Safe** for configuration mistakes,
- **Practical** for a small business (you can freely tweak product attributes without fighting soft-delete everywhere).
