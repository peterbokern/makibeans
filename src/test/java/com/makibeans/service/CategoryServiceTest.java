package com.makibeans.service;

import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceTest {

  /*  1. createRootCategory
    testCreateRootCategoryWithValidName()
    testCreateRootCategoryWithNullOrEmptyName()
    testCreateDuplicateRootCategory()

2. createSubCategory
    testCreateSubcategoryWithValidNameAndParentCategoryId()
    testCreateSubcategoryWithNullOrEmptyName()
    testCreateSubcategoryWithNullParentCategoryId()
    testCreateSubcategoryWithNonExistingParentCategoryId()
    testCreateNonUniqueSubcategory()

3. deleteCategory
    testDeleteCategoryWithValidCategoryId()
    testDeleteCategoryWithNullCategoryId()
    testDeleteCategoryWithNonExistingCategoryId()

4. updateCategory
    testUpdateRootCategoryWithValidCategoryIdAndNewCategoryName()
    testUpdateCategoryWithNullOrEmptyNewCategoryName()
    testUpdateCategoryWithNullCategoryId()
    testUpdateCategoryWithInvalidNewCategoryName()
    testUpdateCategoryWithNonUniqueCategoryName()
    testUpdateCategoryWithCircularReference()

5. Utility Methods
    validateCircularReference:
    testValidateCircularReferenceWithValidHierarchy()
    testValidateCircularReferenceWithCircularReference()
    validateUniqueCategoryNameWithinHierarchy:
    testValidateUniqueCategoryNameWithUniqueName()
    testValidateUniqueCategoryNameWithDuplicateNameInSiblings()
    testValidateUniqueCategoryNameWithDuplicateNameInAncestors()

*/

}