package com.seoulotakus.takumapbe.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.core.util.Json;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorSpot;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;
}
