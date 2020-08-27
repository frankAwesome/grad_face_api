package com.face.recognition.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@JsonInclude(NON_NULL)
public class ApiErrorResponse {
    private Date timestamp;
    private Integer status;
    private String error;
    private Map<String, String> errors;
    private String message;
    private Integer databaseStatusCode;
}
