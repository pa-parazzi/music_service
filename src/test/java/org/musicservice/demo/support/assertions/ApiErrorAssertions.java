package org.musicservice.demo.support.assertions;

import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiErrorAssertions {

    public static void assertApiErrorResponse(ApiErrorResponse errorResponse,
                                              ErrorType errorType, HttpStatus httpStatus){
        assertThat(errorResponse.code()).isEqualTo(errorType.name());
        assertThat(errorResponse.status()).isEqualTo(httpStatus.value());
        assertThat(errorResponse.message()).isNotEmpty();
        assertThat(errorResponse.timestamp()).isPositive();
    }
}
