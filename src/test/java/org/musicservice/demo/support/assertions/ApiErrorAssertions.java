package org.musicservice.demo.support.assertions;

import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApiErrorAssertions {

    public static void assertApiErrorResponse(ApiErrorResponse errorResponse,
                                              ErrorType errorType, HttpStatus httpStatus){
        assertThat(errorResponse.code()).isEqualTo(errorType.name());
        assertThat(errorResponse.status()).isEqualTo(httpStatus.value());
        assertThat(errorResponse.message()).isNotEmpty();
        assertThat(errorResponse.timestamp()).isPositive();
    }

    public static MvcResult assertBadRequestErrorStructure(ResultActions resultActions) throws Exception {
        return resultActions
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").exists(),
                        jsonPath("$.message").exists(),
                        jsonPath("$.status").exists(),
                        jsonPath("$.timestamp").exists(),
                        jsonPath("$.fieldsError").exists())
                .andReturn();
    }
}
