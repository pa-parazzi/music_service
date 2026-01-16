package org.musicservice.demo.repository.user;

import org.musicservice.demo.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u join fetch u.userAvatar where u.username=:username")
    Optional<User> searchByUsername(String username);

    @Query("select u from User u join fetch u.userAvatar where u.id=:id")
    Optional<User> searchById(Long id);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("update User u set u.enabled=true where u.id=:id")
    void enableUser(Long id);

}
