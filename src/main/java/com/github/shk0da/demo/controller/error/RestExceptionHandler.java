package com.github.shk0da.demo.controller.error;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.github.shk0da.demo.exception.DemoException;
import com.github.shk0da.demo.exception.ErrorCode;
import com.github.shk0da.demo.model.error.BadRequestError;
import com.github.shk0da.demo.model.error.Error;
import com.github.shk0da.demo.model.error.ParameterizedError;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.UnexpectedTypeException;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCause;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

@Slf4j
@RestControllerAdvice(basePackages = {"com.github.shk0da.demo.controller"})
public class RestExceptionHandler {

    @ResponseBody
    @ExceptionHandler({Exception.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Error> handleException(Exception ex) {
        log.error("{}", getRootCauseMessage(ex));
        return new ResponseEntity<>(ParameterizedError.of(ErrorCode.CODE_203, Maps.newHashMap()), ErrorCode.CODE_203.getStatus());
    }

    @ResponseBody
    @ExceptionHandler({
            JsonMappingException.class,
            InvalidFormatException.class,
            UnexpectedTypeException.class,
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class
    })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Error> handleIllegalArgumentException(Exception ex) {
        String parameter = null;
        String error = getRootCauseMessage(ex);
        Throwable throwable = getRootCause(ex);
        log.error("{}: {}", throwable, error);
        if (throwable instanceof MethodArgumentNotValidException) {
            List<FieldError> errors = ((MethodArgumentNotValidException) throwable).getBindingResult().getFieldErrors();
            Optional<ResponseEntity<Error>> parameterizedError = extractParameterizedError(errors);
            if (parameterizedError.isPresent()) return parameterizedError.get();
            Map<String, String> parameters = errors.stream()
                    .collect(Collectors.toMap(FieldError::getField, DefaultMessageSourceResolvable::getDefaultMessage));
            parameter = String.join(", ", parameters.keySet());
            error = String.join("; ", parameters.values());
        } else if (throwable instanceof InvalidFormatException) {
            parameter = ((InvalidFormatException) throwable).getPath()
                    .stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining(", "));
            return handleCtsException(new DemoException(ErrorCode.CODE_101, ImmutableMap.of(ErrorCode.Constants.PARAMETER_NAME, parameter)));
        } else if (throwable instanceof JsonMappingException) {
            parameter = ((JsonMappingException) throwable).getPath()
                    .stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining(", "));
            return handleCtsException(new DemoException(ErrorCode.CODE_101, ImmutableMap.of(ErrorCode.Constants.PARAMETER_NAME, parameter)));
        } else if (throwable instanceof NumberFormatException) {
            throw new RuntimeException(error);
        } else if (throwable instanceof JsonParseException) {
            return handleCtsException(new DemoException(ErrorCode.CODE_103));
        }
        return handleCtsException(new DemoException(parameter, error));
    }

    @ResponseBody
    @ExceptionHandler({DemoException.class})
    public ResponseEntity<Error> handleCtsException(DemoException ex) {
        // with ErrorCode.class
        if (ex.getErrorCode() != null) {
            return new ResponseEntity<>(ParameterizedError.of(ex.getErrorCode(), ex.getParameters()), ex.getHttpStatus());
        }
        // default
        return new ResponseEntity<>(BadRequestError.builder()
                .parameter(ofNullable(ex.getParameter()).orElse(""))
                .error(ofNullable(ex.getError()).orElse(""))
                .build(), ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.BAD_REQUEST);
    }

    private Optional<ResponseEntity<Error>> extractParameterizedError(List<FieldError> errors) {
        for (FieldError fieldError : errors) {
            Optional<ErrorCode> errorCode = ErrorCode.byCode(fieldError.getDefaultMessage());
            if (errorCode.isPresent()) {
                Map<String, String> parameters = Maps.newHashMap();
                // Parameter name
                parameters.put(ErrorCode.Constants.PARAMETER_NAME, fieldError.getField());
                // Parameter value length
                if (Size.class.getSimpleName().equals(fieldError.getCode())) {
                    parameters.put(ErrorCode.Constants.PARAMETER_VALUE_LENGTH, fieldError.getArguments()[1].toString());
                }
                ErrorCode err = errorCode.get();
                return Optional.of(new ResponseEntity<>(ParameterizedError.of(err, parameters), err.getStatus()));
            }
        }

        return Optional.empty();
    }
}
