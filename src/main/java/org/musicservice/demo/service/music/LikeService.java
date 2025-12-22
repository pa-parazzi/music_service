package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.request.LikeRequest;
import org.musicservice.demo.dto.music.request.UserLikesRequest;
import org.musicservice.demo.dto.music.response.LikeResponse;
import org.musicservice.demo.exception.music.LikeNotFoundException;
import org.musicservice.demo.mapper.like.LikeResponseMapper;
import org.musicservice.demo.model.music.Likable;
import org.musicservice.demo.model.music.Like;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.music.LikeRepository;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;
    private final LikeResponseMapper likeResponseMapper;
    private final UserService userService;
    private final AlbumService albumService;
    private final SoundService soundService;

    @Autowired
    public LikeService(LikeRepository likeRepository, LikeResponseMapper likeResponseMapper, UserService userService, AlbumService albumService, SoundService soundService) {
        this.likeRepository = likeRepository;
        this.likeResponseMapper = likeResponseMapper;
        this.userService = userService;
        this.albumService = albumService;
        this.soundService = soundService;
    }

    private Object getTargetTypeByRequest(LikeRequest request){
        Likable target = null;
        if(request.getTargetType().equals("album")){
            target = albumService.searchById(request.getTargetId());
        } else if(request.getTargetType().equals("sound")){
            target = soundService.findById(request.getTargetId());
        }
        return target;
    }

    public List<LikeResponse> findAllByUserRequest(UserLikesRequest request) throws LikeNotFoundException{
        List<Like> likes =  likeRepository.findAllByUserId(request.getUserId());
        if(likes.isEmpty()){
            throw new LikeNotFoundException("Лайков у пользователя нет");
        }
        return likes.stream().map(likeResponseMapper::toResponse).toList();
    }

    public Optional<Like> findOptByUserId(Long userId){
        return likeRepository.findByUserId(userId);
    }

    public Like findByUserId(Long userId){
        return likeRepository.findByUserId(userId).orElseThrow(()-> new LikeNotFoundException("Лайк с таким userId не найден в бд"));
    }

    @Transactional
    public void deleteByUserId(LikeRequest request){
        likeRepository.deleteByUserIdAndTarget(request.getUserId(), getTargetTypeByRequest(request));
    }

    @Transactional
    public LikeResponse create(LikeRequest request){
        Like like = new Like();
        User user = userService.searchById(request.getUserId());
        like.setTarget(getTargetTypeByRequest(request));
        like.setUser(user);
        Like savedLike = likeRepository.save(like);
        user.setLikes(Collections.singletonList(savedLike));
        return likeResponseMapper.toResponse(savedLike);
    }


}
