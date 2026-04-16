package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.sound.SoundPageResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.support.config.AbstractSpringBootIT;
import org.musicservice.demo.support.fixture.integration.PageResponseTestFixture;
import org.musicservice.demo.support.fixture.integration.SoundAggregate;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.musicservice.demo.support.assertions.ApiErrorAssertions.assertApiErrorResponse;
import static org.musicservice.demo.support.assertions.ApiErrorAssertions.assertApiErrorResponseStructure;
import static org.musicservice.demo.support.assertions.PageAssertions.*;
import static org.musicservice.demo.support.assertions.SoundAssertions.assertSoundsResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SoundTestFixture.class, PageResponseTestFixture.class})
public class SoundControllerIT extends AbstractSpringBootIT {

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

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE artist, album, sound RESTART IDENTITY CASCADE");
    }

    private Genre genre;

    @BeforeAll
    void getGenre(){
        genre = genreRepository.findByName(GenreName.ROCK).orElseThrow();
    }

    @Test
    void shouldReturnSoundPageResponseCorrectly() throws Exception {
        SoundAggregate soundAggregate = soundFixture.soundAggregateWithOneSound(genre);
        Sound sound = soundAggregate.sounds().getFirst();

        MvcResult result = mockMvc.perform(get("/api/sound/{id}", sound.getId()))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.title").exists(),
                        jsonPath("$.duration").exists(),
                        jsonPath("$.key").exists(),
                        jsonPath("$.url").exists(),
                        jsonPath("$.releaseDate").exists(),
                        jsonPath("$.artist").exists(),
                        jsonPath("$.album").exists())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        SoundPageResponse response = objectMapper.readValue(json, SoundPageResponse.class);

        assertThat(response.title()).isEqualTo(sound.getTitle());
        assertThat(response.duration()).isEqualTo(sound.getDuration());
        assertThat(response.key()).isEqualTo(sound.getKey());
        assertThat(response.url()).isNotBlank();
        assertThat(response.releaseDate()).isEqualTo(sound.getReleaseDate());
        assertThat(response.artist().id()).isEqualTo(sound.getArtist().getId());
        assertThat(response.album().id()).isEqualTo(sound.getAlbum().getId());
    }

    @Test
    void shouldReturnApiErrorResponse_WhenSoundIdIsInvalid() throws Exception {
        RequestBuilder requestBuilder = get("/api/sound/{id}", 75463L);
        MvcResult result = assertApiErrorResponseStructure(
                mockMvc.perform(requestBuilder), status().isNotFound());

        assertApiErrorWhenMusicNotFound(result);
    }

    @Test
    void shouldReturnFirstPageOfSoundsByAlbumIdCorrectly() throws Exception{
        String titlePrefix = "bad romance";
        String keyNameEndsWith = "sound_key";
        Album album = soundFixture.soundAggregateWithSounds(genre, titlePrefix, keyNameEndsWith).album();

        RequestBuilder requestBuilder = createRequest(album.getId(), soundsByAlbumIdUrl, page, size);
        MvcResult result = assertPageResponseOfSoundsStructure(mockMvc.perform(requestBuilder));

        PageResponse<SoundResponse> response = pageResponseFixture.getPageResponse(result, new TypeReference<>(){});
        assertThat(response.hasNextPage()).isTrue();

        List<SoundResponse> soundResponseList = response.contentList();
        assertThat(soundResponseList).hasSize(size);
        assertSoundsResponse(soundResponseList, titlePrefix, keyNameEndsWith);
    }

    @Test
    void shouldReturnFirstPageSoundsByAlbumIdWithIdsGreaterThanSecondPage() throws Exception{
        String titlePrefix = "bad romance";
        String keyNameEndsWith = "sound_key";
        Album album = soundFixture.soundAggregateWithSounds(genre, titlePrefix, keyNameEndsWith).album();

        RequestBuilder firstPageRequest = createRequest(album.getId(), soundsByAlbumIdUrl, page, size);
        MvcResult firstPageResult = assertPageResponseOfSoundsStructure(mockMvc.perform(firstPageRequest));

        RequestBuilder secondPageRequest = createRequest(album.getId(), soundsByAlbumIdUrl, page + 1, size);
        MvcResult secondPageResult = assertPageResponseOfSoundsStructure(mockMvc.perform(secondPageRequest));

        PageResponse<SoundResponse> firstPageResponse = pageResponseFixture
                .getPageResponse(firstPageResult, new TypeReference<>(){});
        PageResponse<SoundResponse> secondPageResponse = pageResponseFixture
                .getPageResponse(secondPageResult, new TypeReference<>(){});

        List<Long> firstPageSoundsIds = firstPageResponse.contentList().stream().map(SoundResponse::id).toList();
        List<Long> secondPageSoundsIds = secondPageResponse.contentList().stream().map(SoundResponse::id).toList();

        assertThat(Collections.max(firstPageSoundsIds)).isLessThan(Collections.min(secondPageSoundsIds));
    }

    @Test
    void shouldReturnApiErrorResponse_WhenAlbumIdIsInvalid() throws Exception {
        RequestBuilder requestBuilder = createRequest(982310L, soundsByAlbumIdUrl, page, size);
        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder), status().isNotFound());

        assertApiErrorWhenMusicNotFound(result);
    }

    @Test
    void shouldReturnFirstPageOfSoundsByArtistIdCorrectly() throws Exception{
        String titlePrefix = "poker face";
        String keyNameEndsWith = "sound_key";
        Artist artist = soundFixture.soundAggregateWithSounds(genre, titlePrefix, keyNameEndsWith).artist();

        RequestBuilder requestBuilder = createRequest(artist.getId(), soundsByArtistIdUrl, page, size);
        MvcResult result = assertPageResponseOfSoundsStructure(mockMvc.perform(requestBuilder));

        PageResponse<SoundResponse> response = pageResponseFixture.getPageResponse(result, new TypeReference<>(){});
        assertThat(response.hasNextPage()).isTrue();

        List<SoundResponse> soundResponseList = response.contentList();
        assertThat(soundResponseList).hasSize(size);
        assertSoundsResponse(soundResponseList, titlePrefix, keyNameEndsWith);
    }

    @Test
    void shouldReturnFirstPageSoundsByArtistIdWithIdsGreaterThanSecondPage() throws Exception{
        String titlePrefix = "poker face";
        String keyNameEndsWith = "sound_key";
        Artist artist = soundFixture.soundAggregateWithSounds(genre, titlePrefix, keyNameEndsWith).artist();

        RequestBuilder firstPageRequest = createRequest(artist.getId(), soundsByArtistIdUrl, page, size);
        MvcResult firstPageResult = assertPageResponseOfSoundsStructure(mockMvc.perform(firstPageRequest));

        RequestBuilder secondPageRequest = createRequest(artist.getId(), soundsByArtistIdUrl, page + 1, size);
        MvcResult secondPageResult = assertPageResponseOfSoundsStructure(mockMvc.perform(secondPageRequest));

        PageResponse<SoundResponse> firstPageResponse = pageResponseFixture
                .getPageResponse(firstPageResult, new TypeReference<>(){});
        PageResponse<SoundResponse> secondPageResponse = pageResponseFixture
                .getPageResponse(secondPageResult, new TypeReference<>(){});

        List<Long> firstPageSoundsIds = firstPageResponse.contentList().stream().map(SoundResponse::id).toList();
        List<Long> secondPageSoundsIds = secondPageResponse.contentList().stream().map(SoundResponse::id).toList();

        assertThat(Collections.max(firstPageSoundsIds)).isLessThan(Collections.min(secondPageSoundsIds));
    }

    @Test
    void shouldReturnApiErrorResponse_WhenArtistIdIsInvalid() throws Exception {
        RequestBuilder requestBuilder = createRequest(982310L, soundsByArtistIdUrl, page, size);
        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder), status().isNotFound());

        assertApiErrorWhenMusicNotFound(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {soundsByAlbumIdUrl, soundsByArtistIdUrl})
    void shouldReturnValidationError_WhenIncorrectPageValue(String url) throws Exception {
        int page = -1;

        RequestBuilder requestBuilder = createRequest(1L, url, page, size);
        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {soundsByAlbumIdUrl, soundsByArtistIdUrl})
    void shouldReturnBadRequest_WhenIncorrectSizeValue(String url) throws Exception {
        int size = 81;

        RequestBuilder requestBuilder = createRequest(1L, url, page, size);
        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {soundsByAlbumIdUrl, soundsByArtistIdUrl})
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
    @ValueSource(strings = {soundsByAlbumIdUrl, soundsByArtistIdUrl})
    void shouldReturnBadRequest_WhenSizeParamIsArgumentTypeMismatch(String url) throws Exception {
        String size = "forty";

        RequestBuilder requestBuilder = get(url, 1L)
                .param("page", String.valueOf(page))
                .param("size", size);

        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {soundsByAlbumIdUrl, soundsByArtistIdUrl})
    void shouldReturnBadRequest_WhenPageParamIsMissing(String url) throws Exception {
        RequestBuilder requestBuilder = get(url, 1L)
                .param("size", String.valueOf(size));

        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {soundsByAlbumIdUrl, soundsByArtistIdUrl})
    void shouldReturnBadRequest_WhenSizeParamIsMissing(String url) throws Exception {
        RequestBuilder requestBuilder = get(url, 1L)
                .param("page", String.valueOf(page));

        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    private final String soundsByAlbumIdUrl = "/api/sound/album/{id}";
    private final String soundsByArtistIdUrl = "/api/sound/artist/{id}";

    private RequestBuilder createRequest(Long id, String url, int page, int size){
        return get(url, id)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size));
    }

    private void assertApiErrorWhenMusicNotFound(MvcResult result) throws Exception{
        String json = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(json, ApiErrorResponse.class);
        assertApiErrorResponse(errorResponse, ErrorType.API_ERROR, HttpStatus.NOT_FOUND);
    }

    private void assertValidationError(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(json, ApiErrorResponse.class);
        assertApiErrorResponse(errorResponse, ErrorType.VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
        assertThat(errorResponse.fieldsError()).isNotEmpty();
    }

}
