package org.musicservice.demo.factory.like;

import jakarta.persistence.EntityManager;
import org.musicservice.demo.dto.music.request.LikeRequest;
import org.musicservice.demo.dto.music.response.LikeResponse;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Likable;
import org.musicservice.demo.model.music.Like;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.LikeRepository;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.service.music.AlbumService;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@TestConfiguration
public class LikeFactory {

    private final LikeRepository likeRepository;
    private final AlbumService albumService;
    private final UserService userService;

    @Autowired
    public LikeFactory(LikeRepository likeRepository, AlbumService albumService, UserService userService) {
        this.likeRepository = likeRepository;
        this.albumService = albumService;
        this.userService = userService;
    }

    public void cleanLikes(){
        likeRepository.deleteAll();
    }

    @Transactional
    public Like createFactoryLike(LikeRequest request){
        Like like = new Like();
        Likable target = albumService.searchById(request.getTargetId());
        User user = userService.searchById(request.getUserId());
        like.setUser(user);
        like.setTarget(target);
        Like savedLike = likeRepository.save(like);
        user.setLikes(Collections.singletonList(savedLike));
        return savedLike;
    }

}
