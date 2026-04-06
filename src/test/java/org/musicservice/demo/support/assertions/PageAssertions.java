package org.musicservice.demo.support.assertions;

import org.springframework.data.domain.Page;

import static org.assertj.core.api.Assertions.assertThat;

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
}
