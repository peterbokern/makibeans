package com.makibeans.web;

import com.makibeans.exceptions.InvalidQueryParamsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

// Interceptor to reject unknown query parameters for endpoints using a @ModelAttribute("params") DTO.
// runs after GlobalBindingAdvice but before controller method
public class UnknownQueryParamInterceptor implements HandlerInterceptor {
    private static final Set<String> GLOBAL_ALLOWED = Set.of("page", "size", "sort", "sortOrder", "search");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod hm)) return true;

        // Find the DTO class used in @ModelAttribute("params") e.g. CategoryAttributeFilter
        Class<?> queryParamsClass = Arrays.stream(hm.getMethodParameters())
                .filter(p -> {
                    var ann = p.getParameterAnnotation(ModelAttribute.class);
                    return ann != null && ("params".equals(ann.value()) || "searchOptions".equals(ann.value())); // only ModelAttribute("params")
                })
                .map(p -> p.getParameter().getType())
                .findFirst().orElse(null);

        if (queryParamsClass == null) return true; // endpoint has no "params" model attribute

        Set<String> allowed = readableWritableProps(queryParamsClass);
        Set<String> incoming = request.getParameterMap().keySet();

        List<String> unknown = incoming.stream()
                .filter(k -> !GLOBAL_ALLOWED.contains(k))
                .filter(k -> !allowed.contains(k))
                .toList();

        if (!unknown.isEmpty()) {
            throw new InvalidQueryParamsException(unknown);
        }
        return true;
    }

    private static Set<String> readableWritableProps(Class<?> type) {
        if (type == null) return java.util.Set.of();
        try {
            return Arrays.stream(java.beans.Introspector.getBeanInfo(type).getPropertyDescriptors())
                    .filter(pd -> pd.getReadMethod() != null && pd.getWriteMethod() != null)
                    .map(java.beans.PropertyDescriptor::getName)
                    .filter(n -> !"class".equals(n))
                    .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new));
        } catch (Exception e) {
            return java.util.Set.of();
        }
    }
}
