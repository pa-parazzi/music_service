package org.musicservice.demo.factory;

import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Artist;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.repository.music.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SoundFactory {

    public List<Sound> createFactorySoundList(Artist artist, Album album){
        List<Sound> soundList = new ArrayList<>();
        Sound sound1 = new Sound("Track 1", 204);
        Sound sound2 = new Sound("Track 2", 189);
        Sound sound3 = new Sound("Track 3", 243);
        soundList.add(sound1);
        soundList.add(sound2);
        soundList.add(sound3);

        soundList.forEach(sound -> {
            sound.setArtist(artist);
            sound.setAlbum(album);
        });

        return soundList;
    }
}
