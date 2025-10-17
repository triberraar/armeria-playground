package org.example.config;

import javax.annotation.Nullable;

import org.example.controller.TestController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.common.ResponseHeaders;
import com.linecorp.armeria.server.AnnotatedServiceBindingBuilder;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServiceNaming;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.annotation.JacksonRequestConverterFunction;
import com.linecorp.armeria.server.annotation.ResponseConverterFunction;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import com.linecorp.armeria.spring.DocServiceConfigurator;

@Configuration
public class ServiceConfiguration {
    private final ObjectMapper mapper;
    private final JacksonRequestConverterFunction requestConverter;
    private final ResponseConverter responseConverter;

    public ServiceConfiguration(
                                ObjectMapper objectMapper) {
        mapper = objectMapper;
        requestConverter = new JacksonRequestConverterFunction(objectMapper);
        responseConverter = new ResponseConverter(objectMapper);
    }

    @Bean
    public ArmeriaServerConfigurator testControllerBean(
            TestController  testController
    ) {
        return serverBuilder -> configureServerBuilder(serverBuilder)
                .build(testController);
    }

    @Bean
    DocServiceConfigurator docServiceBean() {
        return builder -> {
            TestController.setupExampleRequests(builder, mapper);
        };
    }

    private AnnotatedServiceBindingBuilder configureServerBuilder(
            ServerBuilder serverBuilder
    ) {
        return serverBuilder.annotatedService()
                            .decorator(LoggingService.newDecorator())
                            .defaultServiceNaming(ServiceNaming.simpleTypeName())
                            .requestConverters(requestConverter)
                            .responseConverters(responseConverter);
    }

    static final class ResponseConverter implements ResponseConverterFunction {

        private final ObjectMapper objectMapper;

        ResponseConverter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public HttpResponse convertResponse(ServiceRequestContext ctx, ResponseHeaders headers,
                                            @Nullable Object result, HttpHeaders trailers) throws Exception {
            headers = headers.toBuilder().contentType(MediaType.JSON_UTF_8).build();

            final HttpData data = HttpData.wrap(objectMapper.writeValueAsBytes(result));

            return HttpResponse.of(headers, data, trailers);
        }
    }
}
