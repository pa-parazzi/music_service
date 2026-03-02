package org.musicservice.demo.support.factory.unit.music;

import org.musicservice.demo.dto.image.ImageResponse;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.entity.music.Album;

public class AlbumDataFactory {

    private static final Long ID = 1L;
    private static final String IMAGE_KEY = "album_image";
    private static final String IMAGE_URL = "https://mus-app-img.storage.yandexcloud.net/test/image";
    private static final String ARTIST_NAME = "Muse";
    private static final String ALBUM_TITLE = "Black Holes and Revelations";

    public static AlbumResponse albumResponse(){
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setKey(IMAGE_KEY);
        imageResponse.setUrl(IMAGE_URL);

        ArtistResponse artistResponse = new ArtistResponse(ID, ARTIST_NAME);

        AlbumResponse albumResponse = new AlbumResponse();
        albumResponse.setAlbumId(ID);
        albumResponse.setAlbumImage(imageResponse);
        albumResponse.setArtist(artistResponse);
        albumResponse.setTitle(ALBUM_TITLE);
        return albumResponse;
    }

    public static Album album(){
        Album album =  new Album();
        album.setId(ID);
        album.setTitle(ALBUM_TITLE);
        return album;
    }

}
