package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.genre.GenreResponse;
import org.musicservice.demo.dto.music.genre.GenresResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.support.config.AbstractSpringBootIT;
import org.musicservice.demo.support.fixture.integration.*;
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

@Import({PageResponseTestFixture.class, SoundTestFixture.class, AlbumTestFixture.class})
public class GenreControllerIT extends AbstractSpringBootIT {

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
    private SoundTestFixture soundFixture;
    @Autowired
    private AlbumTestFixture albumFixture;

    @BeforeEach
    void cleanupDb() {
        jdbcTemplate.execute("TRUNCATE TABLE artist, album, album_image, sound RESTART IDENTITY CASCADE");
    }

    private Genre genre;

    @BeforeAll
    void getGenre(){
        genre = genreRepository.findByName(GenreName.ROCK).orElseThrow();
    }

    @Test
    void shouldReturnValidGenresResponse() throws Exception {
        List<Genre> genres = genreRepository.findAll();
        List<String> genresName = genres.stream().map(genre -> genre.getName().name()).toList();
        List<String> imagesName = genres.stream().map(Genre::getImageName).toList();

        MvcResult result = mockMvc.perform(get("/api/genre"))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.genres[*].id").exists(),
                        jsonPath("$.genres[*].name").exists(),
                        jsonPath("$.genres[*].imageName").exists())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        GenresResponse response = objectMapper.readValue(json, GenresResponse.class);
        List<GenreResponse> genresResponse = response.genres();

        assertThat(genresResponse).hasSize(genres.size());
        assertThat(genresResponse).extracting(GenreResponse::id).allMatch(Objects::nonNull).doesNotHaveDuplicates();
        assertThat(genresResponse).extracting(GenreResponse::name).containsExactlyInAnyOrderElementsOf(genresName);
        assertThat(genresResponse).extracting(GenreResponse::imageName).containsExactlyInAnyOrderElementsOf(imagesName);
    }

    @Test
    void shouldReturnValidGenreResponseById() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/genre/{id}", genre.getId()))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").exists(),
                        jsonPath("$.name").exists(),
                        jsonPath("$.imageName").exists())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        GenreResponse response = objectMapper.readValue(json, GenreResponse.class);

        assertThat(response.id()).isEqualTo(genre.getId());
        assertThat(response.name()).isEqualTo(genre.getName().name());
        assertThat(response.imageName()).isEqualTo(genre.getImageName());
    }

    @Test
    void shouldReturnNotFound_WhenIdIsInvalid() throws Exception {
        RequestBuilder requestBuilder = get("/api/genre/{id}", 26732L);
        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder), status().isNotFound());

        String json = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(json, ApiErrorResponse.class);

        assertApiErrorResponse(errorResponse, ErrorType.MUSIC_GENRE_DOES_NOT_EXIST, HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnFirstPageOfSoundsCorrectly() throws Exception {
        String titlePrefix = "just dance_";
        String keyNameEndsWith = "key";
        soundFixture.soundAggregateWithSounds(genre, titlePrefix, keyNameEndsWith);

        RequestBuilder request = requestBuilder(tracksByGenreUrl, genre.getId(), page, size);
        MvcResult firstPageResult = assertPageResponseOfSoundsStructure(mockMvc.perform(request));

        PageResponse<SoundResponse> response = pageResponseFixture
                .getPageResponse(firstPageResult, new TypeReference<>() {});
        assertThat(response.hasNextPage()).isTrue();

        List<SoundResponse> soundsResponseList = response.content();
        assertThat(soundsResponseList).hasSize(size);
        assertSoundsResponse(soundsResponseList, titlePrefix, keyNameEndsWith);
    }

    @Test
    void shouldReturnSecondPageSoundsWithIdsGreaterThanFirstPage() throws Exception {
        String titlePrefix = "just dance_";
        String keyNameEndsWith = "key";
        soundFixture.soundAggregateWithSounds(genre, titlePrefix, keyNameEndsWith);

        RequestBuilder firstPageRequest = requestBuilder(tracksByGenreUrl, genre.getId(), page, size);
        MvcResult firstPageResult = mockMvc.perform(firstPageRequest).andExpect(status().isOk()).andReturn();

        RequestBuilder secondPageRequest = requestBuilder(tracksByGenreUrl, genre.getId(), page + 1, size);
        MvcResult secondPageResult = mockMvc.perform(secondPageRequest).andExpect(status().isOk()).andReturn();

        PageResponse<SoundResponse> firstPageResponse = pageResponseFixture
                .getPageResponse(firstPageResult, new TypeReference<>() {});
        PageResponse<SoundResponse> secondPageResponse = pageResponseFixture
                .getPageResponse(secondPageResult, new TypeReference<>() {});

        List<Long> firstPageSoundsIds = firstPageResponse.content().stream().map(SoundResponse::id).toList();
        List<Long> secondPageSoundsIds = secondPageResponse.content().stream().map(SoundResponse::id).toList();

        assertThat(Collections.max(firstPageSoundsIds)).isLessThan(Collections.min(secondPageSoundsIds));
    }

    @Test
    void shouldReturnFirstPageOfAlbumsCorrectly() throws Exception {
        String titlePrefix = "Black hole";
        String imgKeyNameEndsWith = "album_key";
        albumFixture.albumAggregateWithAlbums(genre, titlePrefix, imgKeyNameEndsWith);

        RequestBuilder requestBuilder = requestBuilder(albumsByGenreUrl, genre.getId(), page, size);
        MvcResult result = assertPageResponseOfAlbumsStructure(mockMvc.perform(requestBuilder));

        String json = result.getResponse().getContentAsString();
        PageResponse<AlbumResponse> response = objectMapper.readValue(json, new TypeReference<>() {});
        assertThat(response.hasNextPage()).isTrue();

        List<AlbumResponse> albumResponseList = response.content();
        assertThat(albumResponseList).hasSize(size);
        assertAlbumsResponse(albumResponseList, titlePrefix, imgKeyNameEndsWith);
    }

    @Test
    void shouldReturnSecondPageAlbumsWithIdsGreaterThanFirstPage() throws Exception {
        String titlePrefix = "Black hole";
        String imgKeyNameEndsWith = "album_key";
        albumFixture.albumAggregateWithAlbums(genre, titlePrefix, imgKeyNameEndsWith);

        RequestBuilder firstPageRequest = requestBuilder(albumsByGenreUrl, genre.getId(), page, size);
        MvcResult firstPageResult = mockMvc.perform(firstPageRequest).andExpect(status().isOk()).andReturn();

        RequestBuilder secondPageRequest = requestBuilder(albumsByGenreUrl, genre.getId(), page + 1, size);
        MvcResult secondPageResult = mockMvc.perform(secondPageRequest).andExpect(status().isOk()).andReturn();

        PageResponse<AlbumResponse> firstPageResponse = pageResponseFixture.
                getPageResponse(firstPageResult, new TypeReference<>() {});

        PageResponse<AlbumResponse> secondPageResponse = pageResponseFixture
                .getPageResponse(secondPageResult, new TypeReference<>() {});

        List<Long> firstPageSoundsIds = firstPageResponse.content().stream().map(AlbumResponse::id).toList();
        List<Long> secondPageSoundsIds = secondPageResponse.content().stream().map(AlbumResponse::id).toList();

        assertThat(Collections.max(firstPageSoundsIds)).isLessThan(Collections.min(secondPageSoundsIds));
    }

    @ParameterizedTest
    @ValueSource(strings = {albumsByGenreUrl, tracksByGenreUrl})
    void shouldReturnBadRequest_WhenIncorrectPageValue(String url) throws Exception {
        int page = -1;

        RequestBuilder requestBuilder = requestBuilder(url, 1L, page, size);
        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {albumsByGenreUrl, tracksByGenreUrl})
    void shouldReturnBadRequest_WhenIncorrectSizeValue(String url) throws Exception {
        int size = 50;

        RequestBuilder requestBuilder = requestBuilder(url, 1L, page, size);
        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {albumsByGenreUrl, tracksByGenreUrl})
    void shouldReturnBadRequest_WhenPageParamIsArgumentTypeMismatch(String url) throws Exception {
        String page = "some page";

        RequestBuilder requestBuilder = get(url, 1L)
                .param("page", page)
                .param("size", String.valueOf(size));

        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {albumsByGenreUrl, tracksByGenreUrl})
    void shouldReturnBadRequest_WhenSizeParamIsArgumentTypeMismatch(String url) throws Exception {
        int page = 2;
        String size = "fifty";

        RequestBuilder requestBuilder = get(url, 1L)
                .param("page", String.valueOf(page))
                .param("size", size);

        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {albumsByGenreUrl, tracksByGenreUrl})
    void shouldReturnBadRequest_WhenPageParamIsMissing(String url) throws Exception {
        RequestBuilder requestBuilder = get(url, 1L)
                .param("size", String.valueOf(size));

        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {albumsByGenreUrl, tracksByGenreUrl})
    void shouldReturnBadRequest_WhenSizeParamIsMissing(String url) throws Exception {
        RequestBuilder requestBuilder = get(url, 1L)
                .param("page", String.valueOf(page));

        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    private static final String albumsByGenreUrl = "/api/genre/{id}/albums";
    private static final String tracksByGenreUrl = "/api/genre/{id}/tracks";

    private RequestBuilder requestBuilder(String url, Long genreId, int page, int size) {
        return get(url, genreId)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size));
    }

    private void assertValidationError(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(json, ApiErrorResponse.class);
        assertApiErrorResponse(errorResponse, ErrorType.VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
        assertThat(errorResponse.fieldsError()).isNotEmpty();
    }
}
