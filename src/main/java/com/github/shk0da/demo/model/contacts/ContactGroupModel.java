package com.github.shk0da.demo.model.contacts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.shk0da.demo.domain.ContactGroup;
import com.github.shk0da.demo.exception.DemoException;
import com.github.shk0da.demo.exception.ErrorCode;
import com.github.shk0da.demo.validation.NotBlankValidator;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactGroupModel {

    private Long id;

    @NotNull(message = ErrorCode.Constants.CODE_100)
    @Size(max = 32, message = ErrorCode.Constants.CODE_409)
    private String name;

    @Size(max = 256, message = ErrorCode.Constants.CODE_409)
    private String description;

    public static ContactGroupModel of(ContactGroup contactGroup) {
        return ContactGroupModel.builder()
                .id(contactGroup.getId())
                .name(contactGroup.getName())
                .description(contactGroup.getDescription())
                .build();
    }

    public void patchValidation() {
        if (getName() != null) {
            if (getName().length() > 32) {
                throw new DemoException(ErrorCode.CODE_409, ImmutableMap.of(
                        ErrorCode.Constants.PARAMETER_NAME, "name",
                        ErrorCode.Constants.PARAMETER_VALUE_LENGTH, "32"
                ));
            }
            if (!NotBlankValidator.isValid(getName())) {
                throw new DemoException(ErrorCode.CODE_100, ImmutableMap.of(ErrorCode.Constants.PARAMETER_NAME, "name"));
            }
        }

        if (getDescription() != null && getDescription().length() > 256) {
            throw new DemoException(ErrorCode.CODE_409, ImmutableMap.of(
                    ErrorCode.Constants.PARAMETER_NAME, "description",
                    ErrorCode.Constants.PARAMETER_VALUE_LENGTH, "256"
            ));
        }
    }
}
