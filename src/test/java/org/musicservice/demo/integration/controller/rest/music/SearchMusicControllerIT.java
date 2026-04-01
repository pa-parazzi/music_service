package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.musicservice.demo.repository.image.AlbumImageRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchMusicControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private AlbumImageRepository albumImageRepository;
    @Autowired
    private SoundRepository soundRepository;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE genre, artist, album, album_image, sound RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldReturnFirstPageOfArtistsAndStatusIsOk() throws Exception {
        String fragment = "Mu";
        int page = 0;
        int size = 6;
        Genre genre = genreRepository.save(MusicFactoryIT.genre());
        for (int i = 0; i < 12; i++) {
            artistRepository.save(new Artist("Mu_" + i, genre));
        }

        RequestBuilder requestBuilder = requestBuilder(searchArtistsUrl, fragment, page, size);
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.contentList[*].id").exists(),
                        jsonPath("$.contentList[*].name").exists(),
                        jsonPath("$.hasNextPage").exists())
                .andReturn();

        PageResponse<ArtistResponse> response = getResponse(result, new TypeReference<>() {});

        List<ArtistResponse> artistResponseList = response.contentList();
        assertThat(artistResponseList).hasSize(size);
        assertThat(artistResponseList).extracting(ArtistResponse::id).isSorted();
        assertThat(artistResponseList).extracting(ArtistResponse::name).allMatch(name -> name.startsWith(fragment));
        assertThat(response.hasNextPage()).isTrue();
    }

    @Test
    void shouldReturnLastPageOfArtistsAndStatusIsOk() throws Exception {
        String fragment = "Mu";
        int size = 6;
        Genre genre = genreRepository.save(MusicFactoryIT.genre());
        for (int i = 0; i < 17; i++) {
            artistRepository.save(new Artist("Mu_" + i, genre));
        }

        RequestBuilder requestBuilderFirstPage = requestBuilder(searchArtistsUrl, fragment, 0, size);
        MvcResult firstPageResult = mockMvc.perform(requestBuilderFirstPage)
                .andExpect(status().isOk()).andReturn();

        RequestBuilder requestBuilderSecondPage = requestBuilder(searchArtistsUrl, fragment, 1, size);
        MvcResult secondPageResult = mockMvc.perform(requestBuilderSecondPage)
                .andExpect(status().isOk()).andReturn();

        RequestBuilder requestBuilderLastPage = requestBuilder(searchArtistsUrl, fragment, 2, size);
        MvcResult lastPageResult = mockMvc.perform(requestBuilderLastPage)
                .andExpect(status().isOk()).andReturn();

        PageResponse<ArtistResponse> firstPageResponse = getResponse(firstPageResult, new TypeReference<>(){});
        PageResponse<ArtistResponse> secondPageResponse = getResponse(secondPageResult, new TypeReference<>(){});
        PageResponse<ArtistResponse> lastPageResponse = getResponse(lastPageResult, new TypeReference<>(){});

        List<Long> firstPageArtistsIds = firstPageResponse.contentList().stream().map(ArtistResponse::id).toList();
        List<Long> secondPageArtistsIds = secondPageResponse.contentList().stream().map(ArtistResponse::id).toList();
        List<Long> lastPageArtistsIds = lastPageResponse.contentList().stream().map(ArtistResponse::id).toList();

        assertLastPageResponse(firstPageResponse, secondPageResponse, lastPageResponse, firstPageArtistsIds, secondPageArtistsIds, lastPageArtistsIds);
    }

    @Test
    void shouldReturnFirstPageOfAlbumsAndStatusIsOk() throws Exception {
        String fragment = "Tr";
        int page = 0;
        int size = 6;
        Genre genre = genreRepository.save(MusicFactoryIT.genre());
        Artist artist = artistRepository.save(MusicFactoryIT.artist(genre));
        for (int i = 0; i < 12; i++) {
            Album album = albumRepository.save(new Album("Tri face_" + i, LocalDate.of(2004, 4, 1), artist, genre));
            album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album)));
        }

        RequestBuilder requestBuilder = requestBuilder(searchAlbumsUrl, fragment, page, size);
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.contentList[*].albumId").exists(),
                        jsonPath("$.contentList[*].albumImage").exists(),
                        jsonPath("$.contentList[*].artist").exists(),
                        jsonPath("$.contentList[*].title").exists(),
                        jsonPath("$.hasNextPage").exists())
                .andReturn();

        PageResponse<AlbumResponse> response = getResponse(result, new TypeReference<>() {});

        List<AlbumResponse> albumResponseList = response.contentList();
        assertThat(albumResponseList).hasSize(size);
        assertThat(albumResponseList).extracting(AlbumResponse::getAlbumId).isSorted();
        assertThat(albumResponseList).extracting(AlbumResponse::getTitle).allMatch(title -> title.startsWith(fragment));
        assertThat(response.hasNextPage()).isTrue();
    }

    @Test
    void shouldReturnLastPageOfAlbumsAndStatusIsOk() throws Exception {
        String fragment = "Ba";
        int size = 6;
        Genre genre = genreRepository.save(MusicFactoryIT.genre());
        Artist artist = artistRepository.save(MusicFactoryIT.artist(genre));
        for (int i = 0; i < 17; i++) {
            albumRepository.save(new Album("Bad Face_" + i, LocalDate.of(2002, 3, 6), artist, genre));
        }

        RequestBuilder requestBuilderFirstPage = requestBuilder(searchAlbumsUrl, fragment, 0, size);
        MvcResult firstPageResult = mockMvc.perform(requestBuilderFirstPage).andExpect(status().isOk()).andReturn();

        RequestBuilder requestBuilderSecondPage = requestBuilder(searchAlbumsUrl, fragment, 1, size);
        MvcResult secondPageResult = mockMvc.perform(requestBuilderSecondPage).andExpect(status().isOk()).andReturn();

        RequestBuilder requestBuilderLastPage = requestBuilder(searchAlbumsUrl, fragment, 2, size);
        MvcResult lastPageResult = mockMvc.perform(requestBuilderLastPage).andExpect(status().isOk()).andReturn();

        PageResponse<AlbumResponse> firstPageResponse = getResponse(firstPageResult, new TypeReference<>(){});
        PageResponse<AlbumResponse> secondPageResponse = getResponse(secondPageResult, new TypeReference<>(){});
        PageResponse<AlbumResponse> lastPageResponse = getResponse(lastPageResult, new TypeReference<>(){});

        List<Long> firstPageAlbumsIds = firstPageResponse.contentList().stream().map(AlbumResponse::getAlbumId).toList();
        List<Long> secondPageAlbumsIds = secondPageResponse.contentList().stream().map(AlbumResponse::getAlbumId).toList();
        List<Long> lastPageAlbumsIds = lastPageResponse.contentList().stream().map(AlbumResponse::getAlbumId).toList();

        assertLastPageResponse(firstPageResponse, secondPageResponse, lastPageResponse, firstPageAlbumsIds, secondPageAlbumsIds, lastPageAlbumsIds);
    }

    @Test
    void shouldReturnFirstPageOfTracksAndStatusIsOk() throws Exception {
        String fragment = "Super";
        int page = 0;
        int size = 6;
        Genre genre = genreRepository.save(MusicFactoryIT.genre());
        Artist artist = artistRepository.save(MusicFactoryIT.artist(genre));
        Album album = albumRepository.save(MusicFactoryIT.album(artist, genre));
        for (int i = 0; i < 12; i++) {
            soundRepository.save(MusicFactoryIT.sound(artist, album, genre));
        }

        RequestBuilder requestBuilder = requestBuilder(searchTracksUrl, fragment, page, size);
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.contentList[*].id").exists(),
                        jsonPath("$.contentList[*].title").exists(),
                        jsonPath("$.contentList[*].duration").exists(),
                        jsonPath("$.contentList[*].key").exists(),
                        jsonPath("$.contentList[*].url").exists(),
                        jsonPath("$.hasNextPage").exists())
                .andReturn();

        PageResponse<SoundResponse> response = getResponse(result, new TypeReference<>() {});

        List<SoundResponse> trackResponseList = response.contentList();
        assertThat(trackResponseList).hasSize(size);
        assertThat(trackResponseList).extracting(SoundResponse::getId).isSorted();
        assertThat(trackResponseList).extracting(SoundResponse::getTitle).allMatch(title -> title.startsWith(fragment));
        assertThat(response.hasNextPage()).isTrue();
    }

    @Test
    void shouldReturnLastPageOfTracksAndStatusIsOk() throws Exception {
        String fragment = "Take";
        int size = 6;
        Genre genre = genreRepository.save(MusicFactoryIT.genre());
        Artist artist = artistRepository.save(MusicFactoryIT.artist(genre));
        Album album = albumRepository.save(MusicFactoryIT.album(artist, genre));
        for (int i = 0; i < 17; i++) {
            soundRepository.save(MusicFactoryIT.sound2(artist, album, genre));
        }

        RequestBuilder requestBuilderFirstPage = requestBuilder(searchTracksUrl, fragment, 0, size);
        MvcResult firstPageResult = mockMvc.perform(requestBuilderFirstPage).andExpect(status().isOk()).andReturn();

        RequestBuilder requestBuilderSecondPage = requestBuilder(searchTracksUrl, fragment, 1, size);
        MvcResult secondPageResult = mockMvc.perform(requestBuilderSecondPage).andExpect(status().isOk()).andReturn();

        RequestBuilder requestBuilderLastPage = requestBuilder(searchTracksUrl, fragment, 2, size);
        MvcResult lastPageResult = mockMvc.perform(requestBuilderLastPage).andExpect(status().isOk()).andReturn();

        PageResponse<SoundResponse> firstPageResponse = getResponse(firstPageResult, new TypeReference<>(){});
        PageResponse<SoundResponse> secondPageResponse = getResponse(secondPageResult, new TypeReference<>(){});
        PageResponse<SoundResponse> lastPageResponse = getResponse(lastPageResult, new TypeReference<>(){});

        List<Long> firstPageTracksIds = firstPageResponse.contentList().stream().map(SoundResponse::getId).toList();
        List<Long> secondPageTracksIds = secondPageResponse.contentList().stream().map(SoundResponse::getId).toList();
        List<Long> lastPageTracksIds = lastPageResponse.contentList().stream().map(SoundResponse::getId).toList();

        assertLastPageResponse(firstPageResponse, secondPageResponse, lastPageResponse, firstPageTracksIds, secondPageTracksIds, lastPageTracksIds);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            searchArtistsUrl,
            searchAlbumsUrl,
            searchTracksUrl
    })
    void shouldReturnBadRequest_WhenFragmentIsEmpty(String url) throws Exception {
        String fragment = "    ";
        int page = 2;
        int size = 10;
        Genre genre = genreRepository.save(MusicFactoryIT.genre());
        Artist artist = artistRepository.save(MusicFactoryIT.artist(genre));
        Album album = albumRepository.save(MusicFactoryIT.album(artist, genre));
        for (int i = 0; i < 23; i++) {
            soundRepository.save(MusicFactoryIT.sound(artist, album, genre));
        }
        for (int i = 0; i < 23; i++) {
            soundRepository.save(MusicFactoryIT.sound2(artist, album, genre));
        }
        RequestBuilder requestBuilder = requestBuilder(url, fragment, page, size);
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").exists(),
                        jsonPath("$.message").exists(),
                        jsonPath("$.status").exists(),
                        jsonPath("$.timestamp").exists(),
                        jsonPath("$.fieldsError").exists())
                .andReturn();

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
        int size = 6;

        RequestBuilder requestBuilder = requestBuilder(url, fragment, page, size);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").exists(),
                        jsonPath("$.message").exists(),
                        jsonPath("$.status").exists(),
                        jsonPath("$.timestamp").exists(),
                        jsonPath("$.fieldsError").exists())
                .andReturn();

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
        int page = 0;
        int size = 50;

        RequestBuilder requestBuilder = requestBuilder(url, fragment, page, size);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").exists(),
                        jsonPath("$.message").exists(),
                        jsonPath("$.status").exists(),
                        jsonPath("$.timestamp").exists(),
                        jsonPath("$.fieldsError").exists())
                .andReturn();

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            searchArtistsUrl,
            searchAlbumsUrl,
            searchTracksUrl
    })
    void shouldReturnBadRequest_WhenPageIsIncorrectParameterType(String url) throws Exception {
        String fragment = "some fragment";
        String page = "some page";
        int size = 10;

        RequestBuilder requestBuilder = get(url, fragment)
                .param("page", page)
                .param("size", String.valueOf(size));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").exists(),
                        jsonPath("$.message").exists(),
                        jsonPath("$.status").exists(),
                        jsonPath("$.timestamp").exists(),
                        jsonPath("$.fieldsError").exists())
                .andReturn();

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            searchArtistsUrl,
            searchAlbumsUrl,
            searchTracksUrl
    })
    void shouldReturnBadRequest_WhenSizeIsIncorrectParameterType(String url) throws Exception {
        String fragment = "some fragment";
        int page = 1;
        String size = "fifty";

        RequestBuilder requestBuilder = get(url, fragment)
                .param("page", String.valueOf(page))
                .param("size", size);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").exists(),
                        jsonPath("$.message").exists(),
                        jsonPath("$.status").exists(),
                        jsonPath("$.timestamp").exists(),
                        jsonPath("$.fieldsError").exists())
                .andReturn();

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            searchArtistsUrl,
            searchAlbumsUrl,
            searchTracksUrl
    })
    void shouldReturnBadRequest_WhenPageIsMissing(String url) throws Exception {
        String fragment = "some fragment";
        int size = 15;

        RequestBuilder requestBuilder = get(url, fragment)
                .param("size", String.valueOf(size));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").exists(),
                        jsonPath("$.message").exists(),
                        jsonPath("$.status").exists(),
                        jsonPath("$.timestamp").exists(),
                        jsonPath("$.fieldsError").exists())
                .andReturn();

        assertValidationError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            searchArtistsUrl,
            searchAlbumsUrl,
            searchTracksUrl
    })
    void shouldReturnBadRequest_WhenSizeIsMissing(String url) throws Exception {
        String fragment = "some fragment";
        int page = 1;

        RequestBuilder requestBuilder = get(url, fragment)
                .param("page", String.valueOf(page));

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpectAll(
                        jsonPath("$.code").exists(),
                        jsonPath("$.message").exists(),
                        jsonPath("$.status").exists(),
                        jsonPath("$.timestamp").exists(),
                        jsonPath("$.fieldsError").exists())
                .andReturn();

        assertValidationError(result);
    }

    private final String searchArtistsUrl = "/api/search/{fragment}/artists";
    private final String searchAlbumsUrl = "/api/search/{fragment}/albums";
    private final String searchTracksUrl = "/api/search/{fragment}/tracks";

    private <T> PageResponse<T> getResponse(MvcResult result, TypeReference<PageResponse<T>> typeReference) throws Exception{
        String json = result.getResponse().getContentAsString();
        return objectMapper.readValue(json, typeReference);
    }

    private RequestBuilder requestBuilder(String url, String fragment, int page, int size){
        return get(url, fragment)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size));
    }

    private <T> void assertLastPageResponse(PageResponse<T> firstPageResponse,
                                            PageResponse<T> secondPageResponse,
                                            PageResponse<T> lastPageResponse,
                                            List<Long> firstPageIds, List<Long> secondPageIds, List<Long> lastPageIds){
        assertThat(firstPageResponse.hasNextPage()).isTrue();
        assertThat(secondPageResponse.hasNextPage()).isTrue();
        assertThat(lastPageResponse.hasNextPage()).isFalse();
        assertThat(lastPageResponse.contentList()).hasSize(5);

        assertThat(Collections.max(firstPageIds)).isLessThan(Collections.min(secondPageIds));
        assertThat(Collections.max(secondPageIds)).isLessThan(Collections.min(lastPageIds));
    }

    private void assertValidationError(MvcResult result) throws Exception{
        String json = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(json, ApiErrorResponse.class);

        assertThat(errorResponse.code()).isEqualTo(ErrorType.VALIDATION_ERROR.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.timestamp()).isPositive();
    }
}
