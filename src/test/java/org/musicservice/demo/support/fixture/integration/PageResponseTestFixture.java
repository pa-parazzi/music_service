package org.musicservice.demo.support.fixture.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.test.web.servlet.MvcResult;

@TestComponent
public class PageResponseTestFixture {

    @Autowired
    private ObjectMapper objectMapper;

    public <T> PageResponse<T> getPageResponse(MvcResult result,
                                               TypeReference<PageResponse<T>> typeReference) throws Exception {
        String json = result.getResponse().getContentAsString();
        return objectMapper.readValue(json, typeReference);
    }
}
