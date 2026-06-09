package com.sigap.meters.config;


import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignTraceConfig {

    private static final String TRACE_ID = "traceId";

    private static final String HEADER_NAME = "X-Request-Id";

    @Bean
    public RequestInterceptor traceIdRequestInterceptor() {
        return requestTemplate -> {
            String traceId = MDC.get(TRACE_ID);

            if (traceId != null && !traceId.isBlank()) {
                requestTemplate.header(HEADER_NAME, traceId);
            }
        };
    }

}
