package com.github.shk0da.demo.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class ResponseUtil {

    public static <S> ResponseEntity<S> response(S obj) {
        return (obj != null) ? new ResponseEntity<>(obj, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public static <S> ResponseEntity<S> response(S obj, HttpStatus successStatus, HttpStatus failStatus) {
        return (obj != null) ? new ResponseEntity<>(obj, successStatus) : new ResponseEntity<>(failStatus);
    }
}
