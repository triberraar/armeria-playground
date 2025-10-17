package org.example.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Named;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.common.MediaTypeNames;
import com.linecorp.armeria.server.annotation.Blocking;
import com.linecorp.armeria.server.annotation.Get;
import com.linecorp.armeria.server.annotation.Param;
import com.linecorp.armeria.server.annotation.PathPrefix;
import com.linecorp.armeria.server.annotation.Produces;
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

    @Get("/zip1")
    @Produces(MediaTypeNames.ZIP)
    public byte[] zip1() {
        var bytes = "sdf".getBytes();
        try (ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(bytesOutputStream);
             ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream,
                                                                   StandardCharsets.UTF_8)) {

            putAndWriteFile(zipOutputStream, "", bytes, "test.txt");
            putAndWriteFile(zipOutputStream, "", bytes, "test1.txt");
            putAndWriteFile(zipOutputStream, "", bytes, "test2.txt");

            zipOutputStream.close();
            return bytesOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Get("/zip2")
    @Produces(MediaTypeNames.ZIP)
    public HttpResponse zip2() {
        var bytes = "sdf".getBytes();
        try (ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream();
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(bytesOutputStream);
             ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream,
                                                                   StandardCharsets.UTF_8)) {

            putAndWriteFile(zipOutputStream, "", bytes, "test.txt");
            putAndWriteFile(zipOutputStream, "", bytes, "test1.txt");
            putAndWriteFile(zipOutputStream, "", bytes, "test2.txt");

            zipOutputStream.close();
            return HttpResponse.of(
                    HttpStatus.OK,
                    MediaType.ZIP,
                    bytesOutputStream.toByteArray()
            );
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private static void putAndWriteFile(ZipOutputStream zipOutputStream, String directoryName,
                                        byte[] bytes, String fileName) throws IOException {
        zipOutputStream.putNextEntry(new ZipEntry(directoryName + fileName));
        zipOutputStream.write(bytes);
        zipOutputStream.closeEntry();
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
