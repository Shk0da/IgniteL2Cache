package com.github.shk0da.demo.model.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BadRequestError implements Error {
    private String parameter;
    private String error;
}
