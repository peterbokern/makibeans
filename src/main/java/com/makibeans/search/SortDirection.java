package com.makibeans.search;

import org.springframework.data.domain.Sort;

public enum SortDirection {
    ASC,
    DESC;

    public Sort.Direction toSpring() {
        return this == DESC ? Sort.Direction.DESC : Sort.Direction.ASC;
    }
}
