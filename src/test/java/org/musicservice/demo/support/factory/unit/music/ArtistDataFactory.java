package org.musicservice.demo.support.factory.unit.music;

import org.musicservice.demo.dto.music.artist.ArtistResponse;


public class ArtistDataFactory {

    private static final Long ID = 1L;
    private static final String NAME = "Tri Face";

    public static ArtistResponse artistResponse(){
        return new ArtistResponse(ID, NAME);
    }

}
