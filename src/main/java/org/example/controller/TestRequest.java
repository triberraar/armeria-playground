package org.example.controller;

import com.linecorp.armeria.server.annotation.Param;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TestRequest {
    @Param
    private Integer first;
    @Param
    private String second;
}
