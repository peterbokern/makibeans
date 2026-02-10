package com.makibeans.product.repository;
// Simple interface representing a pair of attribute key and attribute value key.

// e.g. ("origin", "italy")
public interface AttributeValuePair {
    String getAttributeKey();
    String getValueKey();
}
