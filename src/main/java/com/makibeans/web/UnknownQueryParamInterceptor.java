package com.makibeans.web;

import com.makibeans.web.exceptions.UnknownQueryParamException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;

// Interceptor to reject unknown query parameters for endpoints using a @ModelAttribute("params") DTO.
// runs after GlobalBindingAdvice but before controller method
@Component
public class UnknownQueryParamInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws BadRequestException {

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Map<String, String[]> actualParams = request.getParameterMap();
        Set<String> allowedParams = new HashSet<>();

        // 1. Collect @RequestParam names
        for (Parameter p : handlerMethod.getMethod().getParameters()) {
            RequestParam rp = p.getAnnotation(RequestParam.class);
            if (rp != null) {
                allowedParams.add(
                        !rp.name().isEmpty() ? rp.name() : p.getName()
                );
            }

            // 2. Collect @ModelAttribute fields
            if (p.isAnnotationPresent(ModelAttribute.class)) {
                for (Field f : p.getType().getDeclaredFields()) {
                    allowedParams.add(f.getName());
                }
            }
        }

        // 3. Add Spring Data pagination params
        allowedParams.addAll(Set.of("page", "size", "sort"));

        // 4. Compare
        for (String actual : actualParams.keySet()) {
            if (!allowedParams.contains(actual)) {
                throw new UnknownQueryParamException(actual
                );
            }
        }

        return true;
    }
}
