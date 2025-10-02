package org.musicservice.demo.repository.image;

import org.musicservice.demo.model.image.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

}
