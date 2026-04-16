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
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.musicservice.demo.repository.likes.AlbumLikeRepository;
import org.musicservice.demo.repository.likes.SoundLikeRepository;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.support.config.AbstractSpringBootIT;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.musicservice.demo.support.factory.it.security.WithMockUserPrincipal;
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.musicservice.demo.support.fixture.integration.AlbumTestFixture;
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
public class MusicCollectionControllerIT extends AbstractSpringBootIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SoundLikeRepository soundLikeRepository;
    @Autowired
    private AlbumLikeRepository albumLikeRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private PageResponseTestFixture pageResponseFixture;
    @Autowired
    private SoundTestFixture soundFixture;
    @Autowired
    private AlbumTestFixture albumFixture;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE users, artist, album, album_image," +
                " sound, sound_like, album_like RESTART IDENTITY CASCADE");
    }

    private Genre genre;

    @BeforeAll
    void getGenre(){
        genre = genreRepository.findByName(GenreName.ROCK).orElseThrow();
    }

    @Test
    @WithMockUserPrincipal
    void shouldReturnFirstPageOfCollectionTracks() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount());
        String titlePrefix = "umbrella";
        String keyNameEndsWith = "key";
        List<Sound> sounds = soundFixture.soundAggregateWithSounds(genre, titlePrefix, keyNameEndsWith).sounds();
        sounds.forEach(sound -> soundLikeRepository.save(MusicFactoryIT.soundLike(user, sound)));

        RequestBuilder requestBuilder = requestBuilder(tracksCollectionUrl, page, size);
        MvcResult result = assertPageResponseOfSoundsStructure(mockMvc.perform(requestBuilder));

        PageResponse<SoundResponse> response = pageResponseFixture.getPageResponse(result, new TypeReference<>() {});
        assertThat(response.hasNextPage()).isTrue();

        List<SoundResponse> soundResponseList = response.contentList();
        assertThat(soundResponseList).hasSize(size);
        assertSoundsResponse(soundResponseList, titlePrefix, keyNameEndsWith);
    }

    @Test
    @WithMockUserPrincipal
    void shouldReturnFirstPageTracksWithIdsGreaterThanSecondPage_WhenDefaultOrderDesc() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount());
        String titlePrefix = "umbrella";
        String keyNameEndsWith = "key";
        List<Sound> sounds = soundFixture.soundAggregateWithSounds(genre, titlePrefix, keyNameEndsWith).sounds();
        sounds.forEach(sound -> soundLikeRepository.save(MusicFactoryIT.soundLike(user, sound)));

        RequestBuilder firstRequest = requestBuilder(tracksCollectionUrl, page, size);
        MvcResult firstResult = mockMvc.perform(firstRequest).andExpect(status().isOk()).andReturn();

        RequestBuilder secondRequest = requestBuilder(tracksCollectionUrl, page + 1, size);
        MvcResult secondResult = mockMvc.perform(secondRequest).andExpect(status().isOk()).andReturn();

        PageResponse<SoundResponse> firstPageResponse = pageResponseFixture
                .getPageResponse(firstResult, new TypeReference<>() {});
        PageResponse<SoundResponse> secondPageResponse = pageResponseFixture
                .getPageResponse(secondResult, new TypeReference<>() {});

        List<Long> firstPageSoundIds = firstPageResponse.contentList().stream().map(SoundResponse::id).toList();
        List<Long> secondPageSoundIds = secondPageResponse.contentList().stream().map(SoundResponse::id).toList();

        assertThat(Collections.min(firstPageSoundIds)).isGreaterThan(Collections.max(secondPageSoundIds));
    }

    @Test
    @WithMockUserPrincipal
    void shouldReturnApiErrorResponseWithStatusIsNoContent_WhenUserDoesNotHaveSoundLikes() throws Exception{
        userRepository.save(UserDataFactoryIT.userWithEnabledAccount());

        RequestBuilder requestBuilder = requestBuilder(tracksCollectionUrl, page, size);
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent()).andReturn();

        assertMissingMusicContentError(result);
    }

    @Test
    @WithMockUserPrincipal
    void shouldReturnFirstPageOfCollectionAlbums() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount());
        String titlePrefix = "Tri Face";
        String imgKeyNameEndsWith = "album-key";
        List<Album> albums = albumFixture.albumAggregateWithAlbums(genre, titlePrefix, imgKeyNameEndsWith).albums();
        albums.forEach(album -> albumLikeRepository.save(MusicFactoryIT.albumLike(user, album)));

        RequestBuilder requestBuilder = requestBuilder(albumsCollectionUrl, page, size);
        MvcResult result = assertPageResponseOfAlbumsStructure(mockMvc.perform(requestBuilder));

        PageResponse<AlbumResponse> response = pageResponseFixture.getPageResponse(result, new TypeReference<>() {});
        assertThat(response.hasNextPage()).isTrue();

        List<AlbumResponse> albumResponseList = response.contentList();
        assertThat(albumResponseList).hasSize(size);
        assertAlbumsResponse(albumResponseList, titlePrefix, imgKeyNameEndsWith);
    }

    @Test
    @WithMockUserPrincipal
    void shouldReturnFirstPageAlbumsWithIdsGreaterThanSecondPage_WhenDefaultOrderDesc() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount());
        String titlePrefix = "Tri Face";
        String imgKeyNameEndsWith = "album-key";
        List<Album> albums = albumFixture.albumAggregateWithAlbums(genre, titlePrefix, imgKeyNameEndsWith).albums();
        albums.forEach(album -> albumLikeRepository.save(MusicFactoryIT.albumLike(user, album)));

        RequestBuilder firstRequest = requestBuilder(albumsCollectionUrl, page, size);
        MvcResult firstResult = mockMvc.perform(firstRequest).andExpect(status().isOk()).andReturn();

        RequestBuilder secondRequest = requestBuilder(albumsCollectionUrl, page + 1, size);
        MvcResult secondResult = mockMvc.perform(secondRequest).andExpect(status().isOk()).andReturn();

        PageResponse<AlbumResponse> firstPageResponse = pageResponseFixture
                .getPageResponse(firstResult, new TypeReference<>() {});
        PageResponse<AlbumResponse> secondPageResponse = pageResponseFixture
                .getPageResponse(secondResult, new TypeReference<>() {});

        List<Long> firstPageSoundIds = firstPageResponse.contentList().stream().map(AlbumResponse::id).toList();
        List<Long> secondPageSoundIds = secondPageResponse.contentList().stream().map(AlbumResponse::id).toList();

        assertThat(Collections.min(firstPageSoundIds)).isGreaterThan(Collections.max(secondPageSoundIds));
    }

    @Test
    @WithMockUserPrincipal
    void shouldReturnApiErrorResponseWithStatusIsNoContent_WhenUserDoesNotHaveAlbumLikes() throws Exception{
        userRepository.save(UserDataFactoryIT.userWithEnabledAccount());

        RequestBuilder requestBuilder = requestBuilder(albumsCollectionUrl, page, size);
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent()).andReturn();

        assertMissingMusicContentError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {albumsCollectionUrl, tracksCollectionUrl})
    void shouldReturnBadRequest_WhenIncorrectPageValue(String url) throws Exception {
        int page = -1;

        RequestBuilder requestBuilder = requestBuilder(url, page, size);
        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {albumsCollectionUrl, tracksCollectionUrl})
    void shouldReturnBadRequest_WhenIncorrectSizeValue(String url) throws Exception {
        int size = 50;

        RequestBuilder requestBuilder = requestBuilder(url, page, size);
        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {albumsCollectionUrl, tracksCollectionUrl})
    void shouldReturnBadRequest_WhenPageParamIsArgumentTypeMismatch(String url) throws Exception {
        String page = "some page";

        RequestBuilder requestBuilder = get(url)
                .param("page", page)
                .param("size", String.valueOf(size));

        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {albumsCollectionUrl, tracksCollectionUrl})
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
    @ValueSource(strings = {albumsCollectionUrl, tracksCollectionUrl})
    void shouldReturnBadRequest_WhenPageParamIsMissing(String url) throws Exception {
        RequestBuilder requestBuilder = get(url, 1L)
                .param("size", String.valueOf(size));

        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {albumsCollectionUrl, tracksCollectionUrl})
    void shouldReturnBadRequest_WhenSizeParamIsMissing(String url) throws Exception {
        RequestBuilder requestBuilder = get(url, 1L)
                .param("page", String.valueOf(page));

        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.fieldsError").exists()), status().isBadRequest());

        assertValidationError(result);
    }

    private static final String albumsCollectionUrl = "/api/collection/albums";
    private static final String tracksCollectionUrl = "/api/collection/tracks";

    private RequestBuilder requestBuilder(String url, int page, int size) {
        return get(url)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size));
    }

    private void assertValidationError(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(json, ApiErrorResponse.class);
        assertApiErrorResponse(errorResponse, ErrorType.VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
        assertThat(errorResponse.fieldsError()).isNotEmpty();
    }

    private void assertMissingMusicContentError(MvcResult result) throws Exception{
        String json = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(json, ApiErrorResponse.class);
        assertApiErrorResponse(errorResponse, ErrorType.MISSING_MUSIC_CONTENT, HttpStatus.NO_CONTENT);
    }

}
