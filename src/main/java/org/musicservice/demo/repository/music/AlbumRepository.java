package org.musicservice.demo.repository.music;

import org.musicservice.demo.entity.music.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByTitle(String title);

    Optional<Album> searchById(Long id);

    @Query("select a from Album a join fetch a.artist join fetch a.image where a.title like concat(:title, '%')")
    List<Album> findAllByTitleStartingWith(@Param("title") String title);

    // Возвращает список всех альбомов со связями: Исполнитель, Обложка - для главной страницы, где не нужен список песен
    @Query("select a from Album a join fetch a.artist join fetch a.image")
    List<Album> findAllForMainPage();

    // Возвращает альбом по заданному id со связями: Исполнитель, Обложка - для одиночной страницы альбома, список песен не загружает
    @Query("select a from Album a join fetch a.artist join fetch a.image where a.id= :id")
    Optional<Album> findByIdWithArtistAndImage(@Param("id") Long id);

    // Возвращает список всех альбомов по списку их id со связями: Исполнитель, Обложка - для страницы Коллекций альбомов
    @Query("select a from Album a join fetch a.artist join fetch a.image where a.id in :ids")
    List<Album> findAllByIdForCollectionPage(@Param("ids") Iterable<Long> ids);
}
