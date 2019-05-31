package com.github.shk0da.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static com.github.shk0da.demo.exception.ErrorCode.Constants.PARAMETER_NAME;
import static com.github.shk0da.demo.exception.ErrorCode.Constants.PARAMETER_VALUE_LENGTH;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    CODE_100(Constants.CODE_100, "Mandatory parameter [${" + PARAMETER_NAME + "}] not specified", HttpStatus.BAD_REQUEST),
    CODE_101(Constants.CODE_101, "Parameter [${" + PARAMETER_NAME + "}] value is invalid", HttpStatus.BAD_REQUEST),
    CODE_102(Constants.CODE_102, "Resource for parameter [${" + PARAMETER_NAME + "}] is not found", HttpStatus.NOT_FOUND),
    CODE_103(Constants.CODE_103, "JSON can't be parsed", HttpStatus.BAD_REQUEST),
    CODE_122(Constants.CODE_122, "Method is brand specific", HttpStatus.BAD_REQUEST),
    CODE_123(Constants.CODE_123, "Method is account specific", HttpStatus.BAD_REQUEST),
    CODE_124(Constants.CODE_124, "Method is extension specific", HttpStatus.NOT_FOUND),
    CODE_203(Constants.CODE_203, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    CODE_409(Constants.CODE_409, "The parameter [${" + PARAMETER_NAME + "}] cannot be longer than [${" + PARAMETER_VALUE_LENGTH + "}] characters", HttpStatus.BAD_REQUEST),
    ;

    private final String code;
    private final String message;
    private final HttpStatus status;

    public static Optional<ErrorCode> byCode(String code) {
        ErrorCode result = null;
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode().equals(code)) {
                result = errorCode;
                break;
            }
        }
        return Optional.ofNullable(result);
    }

    public static final class Constants {
        // parameters
        public static final String PARAMETER_NAME = "parameterName";
        public static final String PARAMETER_VALUE_LENGTH = "parameterValueLength";

        // codes
        public static final String CODE_100 = "CODE-100";
        public static final String CODE_101 = "CODE-101";
        public static final String CODE_102 = "CODE-102";
        public static final String CODE_103 = "CODE-103";
        public static final String CODE_122 = "CODE-122";
        public static final String CODE_123 = "CODE-123";
        public static final String CODE_124 = "CODE-124";
        public static final String CODE_203 = "CODE-203";
        public static final String CODE_409 = "CODE-409";
    }
}
