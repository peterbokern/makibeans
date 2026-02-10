package com.makibeans.search.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Filter {
    String key() default "";
    String path() default "";
    Operation[] type() default { Operation.EQ };

    boolean filterable() default true;   // allows skipping some
    boolean sortable() default true;     // default yes (easy dev)

    enum Operation { EQ, IN, LIKE, BOOL, GTE, LTE }
}

