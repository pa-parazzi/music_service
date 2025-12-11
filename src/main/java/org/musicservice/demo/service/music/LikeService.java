package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.request.LikeRequest;
import org.musicservice.demo.dto.music.response.LikeResponse;
import org.musicservice.demo.mapper.music.LikeResponseMapper;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Likable;
import org.musicservice.demo.model.music.Like;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.LikeRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;
    private final LikeResponseMapper likeResponseMapper;
    private final UserService userService;
    private final AlbumRepository albumRepository;
    private final SoundRepository soundRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository, LikeResponseMapper likeResponseMapper, UserService userService, AlbumRepository albumRepository, SoundRepository soundRepository) {
        this.likeRepository = likeRepository;
        this.likeResponseMapper = likeResponseMapper;
        this.userService = userService;
        this.albumRepository = albumRepository;
        this.soundRepository = soundRepository;
    }

    public Optional<Like> findOptByUserId(Long userId){
        return likeRepository.findByUserId(userId);
    }

    public Like findByUserId(Long userId){
        return likeRepository.findByUserId(userId).orElseThrow(()-> new UsernameNotFoundException("Сделать кастомное исключение"));
    }

    @Transactional
    public void deleteByUserId(LikeRequest request){
        User user = userService.searchById(request.getUserId());
        Likable target;
        if(request.getTargetType().equals("album")){
            Optional<Album> foundAlbum =  albumRepository.findById(request.getTargetId());
            if(foundAlbum.isPresent()){
                target = foundAlbum.get();
                likeRepository.deleteByUserIdAndTarget(user.getId(), target);
            }
        } else if(request.getTargetType().equals("sound")){
            Optional<Sound> foundSound = soundRepository.findById(request.getTargetId());
            if(foundSound.isPresent()){
                target = foundSound.get();
                likeRepository.deleteByUserIdAndTarget(user.getId(), target);
            }
        }
    }

    @Transactional
    public LikeResponse create(LikeRequest request){
        Like like = new Like();
        User user = userService.searchById(request.getUserId());
        Likable target;
        if(request.getTargetType().equals("album")){
            Optional<Album> foundAlbum =  albumRepository.findById(request.getTargetId());
            if(foundAlbum.isPresent()){
                target = foundAlbum.get();
                like.setTarget(target);
            }
        } else if(request.getTargetType().equals("sound")){
            Optional<Sound> foundSound = soundRepository.findById(request.getTargetId());
            if(foundSound.isPresent()){
                target = foundSound.get();
                like.setTarget(target);
            }
        }
        like.setUser(user);
        Like savedLike = likeRepository.save(like);
        user.setLikes(Collections.singletonList(savedLike));
        return likeResponseMapper.toResponse(savedLike);
    }


}
