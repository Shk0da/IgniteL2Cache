package com.github.shk0da.demo.model.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.shk0da.demo.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class ParameterizedError implements Error {

    private final String code;
    private final String message;

    public static ParameterizedError of(ErrorCode errorCode, Map<String, String> parameters) {
        String message = errorCode.getMessage();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            message = message.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return new ParameterizedError(errorCode.getCode(), message);
    }

    @Override
    @JsonIgnore
    public String getError() {
        return code;
    }
}
