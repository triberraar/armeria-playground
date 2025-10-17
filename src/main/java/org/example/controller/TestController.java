package org.example.controller;

import javax.inject.Named;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.linecorp.armeria.server.annotation.Blocking;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Param;
import com.linecorp.armeria.server.annotation.PathPrefix;
import com.linecorp.armeria.server.docs.DocServiceBuilder;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Blocking
@Named
@PathPrefix("/test")
@Slf4j
public class TestController {
    @Get("/first")
    public String first() {
      log.info("first");
      return "first";
    }

    //this one fails in doc service unless you put content-type */*
    @Get("/second")
    public String second(TestRequest testRequest) {
        log.info("second");
        return "second";
    }

    @Get("/third")
    public String third(@Param Integer first, @Param String second) {
        log.info("third");
        return "third";
    }

    @SneakyThrows
    public static void setupExampleRequests(DocServiceBuilder builder, ObjectMapper objectMapper) {
        builder
                .examplePaths(
                        TestController.class,
                        "first",
                        "/test/first"
                )
                .examplePaths(
                        TestController.class,
                        "second",
                        "/test/second"
                )
                .examplePaths(
                        TestController.class,
                        "third",
                        "/test/third"
                )
                .exampleQueries(
                        TestController.class,
                        "second",
                        "first=1&second=stuff"

                )
                .exampleQueries(
                        TestController.class,
                        "third",
                        "first=1&second=stuff"

                );
    }
}
