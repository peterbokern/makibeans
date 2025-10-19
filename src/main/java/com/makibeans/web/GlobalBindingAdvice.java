package com.makibeans.web;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * Global binding advice to trim all incoming string parameters and convert empty strings to null.
 * This applies to all controllers within the specified base package.
 */
@RestControllerAdvice(basePackages = "com.makibeans.controller")
@Order(1)
public class GlobalBindingAdvice {

    @InitBinder("params")
    public void initBinder(WebDataBinder binder) throws BindException {
        // Trim all incoming string parameters and convert empty strings to null
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}

