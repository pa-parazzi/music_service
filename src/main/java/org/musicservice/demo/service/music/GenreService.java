package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.genre.GenreResponse;
import org.musicservice.demo.dto.music.genre.GenresResponse;
import org.musicservice.demo.entity.music.Genre;
import org.musicservice.demo.exception.music.GenreDoesNotExistException;
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
        List<GenreResponse> genresList = genreRepository.findAll().stream().map(genre -> new GenreResponse(genre.getId(), genre.getName().name())).toList();
        return new GenresResponse(genresList);
    }

    public void checkExistById(Long id){
        if(!genreRepository.existsById(id)) throw new GenreDoesNotExistException("Такой жанр не существует");
    }

    public GenreResponse findGenreNameById(Long id){
        Genre genre = genreRepository.findById(id).orElseThrow(()-> new GenreDoesNotExistException("Такой жанр не существует"));
        return new GenreResponse(genre.getId(), genre.getName().name());
    }
}
