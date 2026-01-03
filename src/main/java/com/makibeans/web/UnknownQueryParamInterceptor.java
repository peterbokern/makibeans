package com.makibeans.web;

import com.makibeans.web.exceptions.UnknownQueryParamException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class UnknownQueryParamInterceptor implements HandlerInterceptor {

    private static final Set<String> COMMON = Set.of("page", "size", "sort");

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        // Only validate query params for GET endpoints
        if (!"GET".equalsIgnoreCase(request.getMethod())) return true;

        // Only controller methods have @RequestParam / @ModelAttribute to inspect
        if (!(handler instanceof HandlerMethod handlerMethod)) return true;

        Map<String, String[]> actualParams = request.getParameterMap();
        Set<String> allowedParams = new HashSet<>(COMMON);

        Parameter[] params = handlerMethod.getMethod().getParameters();

        // 1) Allow explicit @RequestParam names
        for (Parameter p : params) {
            RequestParam rp = p.getAnnotation(RequestParam.class);
            if (rp != null) {
                allowedParams.add(resolveRequestParamName(p, rp));
            }
        }

        // 2) Allow fields from the first @ModelAttribute DTO (your filter)
        for (Parameter p : params) {
            if (p.isAnnotationPresent(ModelAttribute.class)) {
                addAllFieldNames(p.getType(), allowedParams);
                break; // assume single filter DTO
            }
        }

        // 3) Reject unknown query parameters
        for (String actual : actualParams.keySet()) {
            if (!allowedParams.contains(actual)) {
                throw new UnknownQueryParamException(actual, allowedParams);
            }
        }

        return true;
    }

    private static String resolveRequestParamName(Parameter p, RequestParam rp) {
        if (!rp.name().isBlank()) return rp.name();
        if (!rp.value().isBlank()) return rp.value();
        return p.getName(); // fallback; best practice is to always set name=""
    }

    // Recursively add all field names from the class and its superclasses
    private static void addAllFieldNames(Class<?> type, Set<String> allowed) {
        for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                allowed.add(f.getName());
            }
        }
    }
}
