package org.musicservice.demo.support.assertions;

import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PageAssertions {

    public static final int page = 0;
    public static final int size = 5;
    public static final int totalElements = 13;

    public static <T> void assertFirstPage(Page<T> pageResponse){
        assertThat(pageResponse.getNumberOfElements()).isEqualTo(size);
        assertThat(pageResponse.hasNext()).isTrue();
        assertThat(pageResponse.getTotalElements()).isEqualTo(totalElements);
        assertThat(pageResponse.getNumber()).isEqualTo(page);
    }

    public static <T> void assertSecondPage(Page<T> pageResponse){
        assertThat(pageResponse.getNumberOfElements()).isEqualTo(size);
        assertThat(pageResponse.hasPrevious()).isTrue();
        assertThat(pageResponse.hasNext()).isTrue();
        assertThat(pageResponse.getTotalElements()).isEqualTo(totalElements);
        assertThat(pageResponse.getNumber()).isEqualTo(page + 1);
    }

    public static <T> void assertLastPage(Page<T> pageResponse){
        assertThat(pageResponse.getNumberOfElements()).isLessThan(size);
        assertThat(pageResponse.hasNext()).isFalse();
        assertThat(pageResponse.getNumber()).isEqualTo(page + 2);
        assertThat(pageResponse.getTotalElements()).isEqualTo(totalElements);
    }

    public static <T> void assertEmptyPage(Page<T> pageResponse){
        assertThat(pageResponse.getContent()).isEmpty();
        assertThat(pageResponse.hasNext()).isFalse();
        assertThat(pageResponse.getNumberOfElements()).isZero();
        assertThat(pageResponse.getTotalElements()).isZero();
        assertThat(pageResponse.getTotalPages()).isZero();
    }

    public static MvcResult assertPageResponseOfAlbumsStructure(ResultActions resultActions) throws Exception{
        return resultActions
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.content[*].id").exists(),
                        jsonPath("$.content[*].title").exists(),
                        jsonPath("$.content[*].image").exists(),
                        jsonPath("$.content[*].artist").exists(),
                        jsonPath("$.hasNextPage").exists())
                .andReturn();
    }

    public static MvcResult assertPageResponseOfSoundsStructure(ResultActions resultActions) throws Exception{
        return resultActions
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.content[*].id").exists(),
                        jsonPath("$.content[*].title").exists(),
                        jsonPath("$.content[*].duration").exists(),
                        jsonPath("$.content[*].key").exists(),
                        jsonPath("$.content[*].url").exists(),
                        jsonPath("$.hasNextPage").exists())
                .andReturn();
    }

}
