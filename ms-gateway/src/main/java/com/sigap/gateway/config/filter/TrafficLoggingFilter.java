package com.sigap.gateway.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TrafficLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TrafficLoggingFilter.class);

    private static final String TRACE_ID_MDC_KEY = "traceId";

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String traceId = resolveTraceId(request);
        String targetService = resolveTargetService(request.getRequestURI());
        long startTime = System.currentTimeMillis();

        MDC.put(TRACE_ID_MDC_KEY, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);
        response.setHeader(REQUEST_ID_HEADER, traceId);

        HttpServletRequest requestWithTraceHeaders = new HeaderOverrideRequestWrapper(
                request,
                Map.of(TRACE_ID_HEADER, traceId, REQUEST_ID_HEADER, traceId)
        );

        log.info("Gateway request received traceId={} method={} path={} targetService={} clientIp={}",
                traceId,
                request.getMethod(),
                request.getRequestURI(),
                targetService,
                resolveClientIp(request));

        try {
            filterChain.doFilter(requestWithTraceHeaders, response);
        } finally {
            long durationMs = System.currentTimeMillis() - startTime;

            log.info("Gateway request completed traceId={} method={} path={} targetService={} status={} durationMs={}",
                    traceId,
                    request.getMethod(),
                    request.getRequestURI(),
                    targetService,
                    response.getStatus(),
                    durationMs);

            MDC.remove(TRACE_ID_MDC_KEY);
        }
    }

    private String resolveTraceId(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(TRACE_ID_HEADER))
                .filter(value -> !value.isBlank())
                .or(() -> Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER)).filter(value -> !value.isBlank()))
                .orElseGet(() -> UUID.randomUUID().toString());
    }

    private String resolveTargetService(String requestUri) {
        if (requestUri.startsWith("/ms-auth-server/")) {
            return "ms-auth-server";
        }

        if (requestUri.startsWith("/ms-partner/")) {
            return "ms-partner";
        }

        return "unknown";
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    private static class HeaderOverrideRequestWrapper extends HttpServletRequestWrapper {

        private final Map<String, String> headers;

        HeaderOverrideRequestWrapper(HttpServletRequest request, Map<String, String> headers) {
            super(request);
            this.headers = headers;
        }

        @Override
        public String getHeader(String name) {
            return headers.getOrDefault(name, super.getHeader(name));
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            if (headers.containsKey(name)) {
                List<String> values = new ArrayList<>();
                values.add(headers.get(name));

                Enumeration<String> originalValues = super.getHeaders(name);
                while (originalValues.hasMoreElements()) {
                    values.add(originalValues.nextElement());
                }

                return Collections.enumeration(values);
            }

            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> names = new LinkedHashSet<>();
            Enumeration<String> originalNames = super.getHeaderNames();

            while (originalNames.hasMoreElements()) {
                names.add(originalNames.nextElement());
            }

            names.addAll(headers.keySet());

            return Collections.enumeration(names);
        }
    }
}
