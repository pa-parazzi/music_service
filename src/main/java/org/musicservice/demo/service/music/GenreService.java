package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.genre.GenresResponse;
import org.musicservice.demo.entity.music.Genre;
import org.musicservice.demo.repository.music.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public GenresResponse genresNames(){
        List<String> genresList = genreRepository.findAll().stream().map(genre -> genre.getName().name()).toList();
        return new GenresResponse(genresList);
    }
}
