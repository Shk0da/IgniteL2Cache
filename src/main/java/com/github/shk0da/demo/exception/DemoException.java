package com.github.shk0da.demo.exception;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class DemoException extends RuntimeException {

    static final long serialVersionUID = -7034897190745766940L;

    private String parameter;
    private String error;
    private HttpStatus httpStatus;
    private ErrorCode errorCode;
    private Map<String, String> parameters = Maps.newHashMap();

    public DemoException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.httpStatus = errorCode.getStatus();
    }

    public DemoException(ErrorCode errorCode, Map<String, String> parameters) {
        this.errorCode = errorCode;
        this.parameters = parameters;
        this.httpStatus = errorCode.getStatus();
    }

    public DemoException(String error) {
        this.error = error;
    }

    public DemoException(String parameter, String error) {
        this.parameter = parameter;
        this.error = error;
    }

    public DemoException(String parameter, String error, HttpStatus httpStatus) {
        this.parameter = parameter;
        this.error = error;
        this.httpStatus = httpStatus;
    }

    public DemoException(String error, HttpStatus httpStatus) {
        this.error = error;
        this.httpStatus = httpStatus;
    }
}
