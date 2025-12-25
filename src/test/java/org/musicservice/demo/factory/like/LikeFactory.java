package org.musicservice.demo.factory.like;

import jakarta.persistence.EntityManager;
import org.musicservice.demo.dto.music.request.LikeRequest;
import org.musicservice.demo.dto.music.response.LikeResponse;
import org.musicservice.demo.factory.user.UserDataFactory;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Likable;
import org.musicservice.demo.model.music.Like;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.LikeRepository;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.service.music.AlbumService;
import org.musicservice.demo.service.music.SoundService;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@TestConfiguration
public class LikeFactory {

    private final LikeRepository likeRepository;
    private final AlbumService albumService;
    private final SoundService soundService;

    @Autowired
    public LikeFactory(LikeRepository likeRepository, AlbumService albumService, SoundService soundService) {
        this.likeRepository = likeRepository;
        this.albumService = albumService;
        this.soundService = soundService;
    }

    public void cleanLikes(){
        likeRepository.deleteAll();
    }

    public List<Like> saveAll(List<Like> likes){
        return likeRepository.saveAll(likes);
    }

    public Like createFactoryLike(User user, Long targetId, String targetType){
        LikeRequest likeRequest = new LikeRequest();
        likeRequest.setUserId(user.getId());
        likeRequest.setTargetType(targetType);
        likeRequest.setTargetId(targetId);
        Like like = new Like();
        Likable target;
        if(targetType.equals("album")){
            target = albumService.searchById(targetId);
            like.setTarget(target);
        } else if(targetType.equals("sound")){
            target = soundService.findById(targetId);
            like.setTarget(target);
        }
        like.setUser(user);
        user.setLikes(Collections.singletonList(like));
        return like;
    }

}
