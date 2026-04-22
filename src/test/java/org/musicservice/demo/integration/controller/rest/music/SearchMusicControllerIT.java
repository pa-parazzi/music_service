package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.support.config.AbstractSpringBootIT;
import org.musicservice.demo.support.fixture.integration.AlbumTestFixture;
import org.musicservice.demo.support.fixture.integration.ArtistTestFixture;
import org.musicservice.demo.support.fixture.integration.PageResponseTestFixture;
import org.musicservice.demo.support.fixture.integration.SoundTestFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.musicservice.demo.support.assertions.AlbumAssertions.assertAlbumsResponse;
import static org.musicservice.demo.support.assertions.ApiErrorAssertions.assertApiErrorResponse;
import static org.musicservice.demo.support.assertions.ApiErrorAssertions.assertApiErrorResponseStructure;
import static org.musicservice.demo.support.assertions.PageAssertions.*;
import static org.musicservice.demo.support.assertions.SoundAssertions.assertSoundsResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({ArtistTestFixture.class, SoundTestFixture.class, AlbumTestFixture.class, PageResponseTestFixture.class})
public class SearchMusicControllerIT extends AbstractSpringBootIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private PageResponseTestFixture pageResponseFixture;
    @Autowired
    private ArtistTestFixture artistFixture;
    @Autowired
    private AlbumTestFixture albumFixture;
    @Autowired
    private SoundTestFixture soundFixture;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE artist, album, album_image, sound RESTART IDENTITY CASCADE");
    }

    private Genre genre;

    @BeforeAll
    void getGenre(){
        genre = genreRepository.findByName(GenreName.ROCK).orElseThrow();
    }

    @Test
    void shouldReturnFirstPageOfArtists() throws Exception {
        String namePrefix = "Muse";
        artistFixture.createArtists(genre, namePrefix);

        RequestBuilder requestBuilder = requestBuilder(searchArtistsUrl, namePrefix, page, size);
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.content[*].id").exists(),
                        jsonPath("$.content[*].name").exists(),
                        jsonPath("$.hasNextPage").exists())
                .andReturn();

        PageResponse<ArtistResponse> response = pageResponseFixture.getPageResponse(result, new TypeReference<>() {});

        List<ArtistResponse> artistResponseList = response.content();
        assertThat(artistResponseList).hasSize(size);
        assertThat(artistResponseList).extracting(ArtistResponse::id).isSorted();
        assertThat(artistResponseList).extracting(ArtistResponse::id).allMatch(Objects::nonNull).doesNotHaveDuplicates();
        assertThat(artistResponseList).extracting(ArtistResponse::name).allMatch(name -> name.startsWith(namePrefix));
        assertThat(response.hasNextPage()).isTrue();
    }

    @Test
    void shouldReturnLastPageOfArtists() throws Exception {
        String namePrefix = "Muse";
        artistFixture.createArtists(genre, namePrefix);

        RequestBuilder requestBuilderFirstPage = requestBuilder(searchArtistsUrl, namePrefix, 0, size);
        MvcResult firstPageResult = mockMvc.perform(requestBuilderFirstPage)
                .andExpect(status().isOk()).andReturn();

        RequestBuilder requestBuilderSecondPage = requestBuilder(searchArtistsUrl, namePrefix, 1, size);
        MvcResult secondPageResult = mockMvc.perform(requestBuilderSecondPage)
                .andExpect(status().isOk()).andReturn();

        PageResponse<ArtistResponse> firstPageResponse = pageResponseFixture
                .getPageResponse(firstPageResult, new TypeReference<>(){});
        PageResponse<ArtistResponse> secondPageResponse = pageResponseFixture
                .getPageResponse(secondPageResult, new TypeReference<>(){});

        List<Long> firstPageArtistsIds = firstPageResponse.content().stream().map(ArtistResponse::id).toList();
        List<Long> secondPageArtistsIds = secondPageResponse.content().stream().map(ArtistResponse::id).toList();

        assertThat(firstPageArtistsIds).allMatch(Objects::nonNull).doesNotHaveDuplicates();
        assertThat(secondPageArtistsIds).allMatch(Objects::nonNull).doesNotHaveDuplicates();

        assertThat(Collections.max(firstPageArtistsIds)).isLessThan(Collections.min(secondPageArtistsIds));
    }

    @Test
    void shouldReturnFirstPageOfAlbums() throws Exception {
        String titlePrefix = "Time";
        String imgKeyNameEndsWith = "album_key";
        albumFixture.albumAggregateWithAlbums(genre, titlePrefix, imgKeyNameEndsWith);

        RequestBuilder requestBuilder = requestBuilder(searchAlbumsUrl, titlePrefix, page, size);
        MvcResult result = assertPageResponseOfAlbumsStructure(mockMvc.perform(requestBuilder));

        PageResponse<AlbumResponse> response = pageResponseFixture.getPageResponse(result, new TypeReference<>() {});
        assertThat(response.hasNextPage()).isTrue();

        List<AlbumResponse> albumResponseList = response.content();
        assertThat(albumResponseList).hasSize(size);
        assertAlbumsResponse(albumResponseList, titlePrefix, imgKeyNameEndsWith);
    }

    @Test
    void shouldReturnLastPageOfAlbums() throws Exception {
        String titlePrefix = "Starboy";
        String imgKeyNameEndsWith = "album_key";
        albumFixture.albumAggregateWithAlbums(genre, titlePrefix, imgKeyNameEndsWith);

        RequestBuilder requestBuilderFirstPage = requestBuilder(searchAlbumsUrl, titlePrefix, page, size);
        MvcResult firstPageResult = mockMvc.perform(requestBuilderFirstPage).andExpect(status().isOk()).andReturn();

        RequestBuilder requestBuilderSecondPage = requestBuilder(searchAlbumsUrl, titlePrefix, page + 1, size);
        MvcResult secondPageResult = mockMvc.perform(requestBuilderSecondPage).andExpect(status().isOk()).andReturn();

        PageResponse<AlbumResponse> firstPageResponse = pageResponseFixture
                .getPageResponse(firstPageResult, new TypeReference<>(){});
        PageResponse<AlbumResponse> secondPageResponse = pageResponseFixture
                .getPageResponse(secondPageResult, new TypeReference<>(){});

        List<Long> firstPageAlbumsIds = firstPageResponse.content().stream().map(AlbumResponse::id).toList();
        List<Long> secondPageAlbumsIds = secondPageResponse.content().stream().map(AlbumResponse::id).toList();

        assertThat(firstPageAlbumsIds).allMatch(Objects::nonNull).doesNotHaveDuplicates();
        assertThat(secondPageAlbumsIds).allMatch(Objects::nonNull).doesNotHaveDuplicates();

        assertThat(Collections.max(firstPageAlbumsIds)).isLessThan(Collections.min(secondPageAlbumsIds));
    }

    @Test
    void shouldReturnFirstPageOfTracks() throws Exception {
        String titlePrefix = "Secrets";
        String keyNameEndsWith = "key";
        soundFixture.soundAggregateWithSounds(genre, titlePrefix, keyNameEndsWith);

        RequestBuilder requestBuilder = requestBuilder(searchTracksUrl, titlePrefix, page, size);
        MvcResult result = assertPageResponseOfSoundsStructure(mockMvc.perform(requestBuilder));

        PageResponse<SoundResponse> response = pageResponseFixture.getPageResponse(result, new TypeReference<>() {});
        assertThat(response.hasNextPage()).isTrue();

        List<SoundResponse> soundResponseList = response.content();
        assertThat(soundResponseList).hasSize(size);
        assertSoundsResponse(soundResponseList, titlePrefix, keyNameEndsWith);
    }

    @Test
    void shouldReturnLastPageOfTracks() throws Exception {
        String titlePrefix = "Attentions";
        String keyNameEndsWith = "key";
        soundFixture.soundAggregateWithSounds(genre, titlePrefix, keyNameEndsWith);

        RequestBuilder requestBuilderFirstPage = requestBuilder(searchTracksUrl, titlePrefix, page, size);
        MvcResult firstPageResult = mockMvc.perform(requestBuilderFirstPage).andExpect(status().isOk()).andReturn();

        RequestBuilder requestBuilderSecondPage = requestBuilder(searchTracksUrl, titlePrefix, page + 1, size);
        MvcResult secondPageResult = mockMvc.perform(requestBuilderSecondPage).andExpect(status().isOk()).andReturn();

        PageResponse<SoundResponse> firstPageResponse = pageResponseFixture
                .getPageResponse(firstPageResult, new TypeReference<>(){});
        PageResponse<SoundResponse> secondPageResponse = pageResponseFixture
                .getPageResponse(secondPageResult, new TypeReference<>(){});

        List<Long> firstPageSoundsIds = firstPageResponse.content().stream().map(SoundResponse::id).toList();
        List<Long> secondPageSoundsIds = secondPageResponse.content().stream().map(SoundResponse::id).toList();

        assertThat(firstPageSoundsIds).allMatch(Objects::nonNull).doesNotHaveDuplicates();
        assertThat(secondPageSoundsIds).allMatch(Objects::nonNull).doesNotHaveDuplicates();

        assertThat(Collections.max(firstPageSoundsIds)).isLessThan(Collections.min(secondPageSoundsIds));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            searchArtistsUrl,
            searchAlbumsUrl,
            searchTracksUrl
    })
    void shouldReturnBadRequest_WhenFragmentIsEmpty(String url) throws Exception {
        String fragment = "    ";

        RequestBuilder requestBuilder = requestBuilder(url, fragment, page, size);
        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                        .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            searchArtistsUrl,
            searchAlbumsUrl,
            searchTracksUrl
    })
    void shouldReturnBadRequest_WhenIncorrectPageValue(String url) throws Exception {
        String fragment = "some fragment";
        int page = -1;

        RequestBuilder requestBuilder = requestBuilder(url, fragment, page, size);
        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            searchArtistsUrl,
            searchAlbumsUrl,
            searchTracksUrl
    })
    void shouldReturnBadRequest_WhenIncorrectSizeValue(String url) throws Exception {
        String fragment = "some fragment";
        int size = 50;

        RequestBuilder requestBuilder = requestBuilder(url, fragment, page, size);
        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            searchArtistsUrl,
            searchAlbumsUrl,
            searchTracksUrl
    })
    void shouldReturnBadRequest_WhenPageParamIsArgumentTypeMismatch(String url) throws Exception {
        String fragment = "some fragment";
        String page = "some page";

        RequestBuilder requestBuilder = get(url, fragment)
                .param("page", page)
                .param("size", String.valueOf(size));

        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            searchArtistsUrl,
            searchAlbumsUrl,
            searchTracksUrl
    })
    void shouldReturnBadRequest_WhenSizeParamIsArgumentTypeMismatch(String url) throws Exception {
        String fragment = "some fragment";
        String size = "fifty";

        RequestBuilder requestBuilder = get(url, fragment)
                .param("page", String.valueOf(page))
                .param("size", size);

        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            searchArtistsUrl,
            searchAlbumsUrl,
            searchTracksUrl
    })
    void shouldReturnBadRequest_WhenPageParamIsMissing(String url) throws Exception {
        String fragment = "some fragment";

        RequestBuilder requestBuilder = get(url, fragment)
                .param("size", String.valueOf(size));

        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            searchArtistsUrl,
            searchAlbumsUrl,
            searchTracksUrl
    })
    void shouldReturnBadRequest_WhenSizeParamIsMissing(String url) throws Exception {
        String fragment = "some fragment";

        RequestBuilder requestBuilder = get(url, fragment)
                .param("page", String.valueOf(page));

        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    private final String searchArtistsUrl = "/api/search/{fragment}/artists";
    private final String searchAlbumsUrl = "/api/search/{fragment}/albums";
    private final String searchTracksUrl = "/api/search/{fragment}/tracks";

    private RequestBuilder requestBuilder(String url, String fragment, int page, int size){
        return get(url, fragment)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size));
    }

    private void assertValidationError(MvcResult result) throws Exception{
        String json = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(json, ApiErrorResponse.class);
        assertApiErrorResponse(errorResponse, ErrorType.VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
        assertThat(errorResponse.fieldsError()).isNotEmpty();
    }
}
