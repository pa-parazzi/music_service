package org.musicservice.demo.repository.user;

import org.musicservice.demo.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u left join fetch u.userAvatar where u.username=:username")
    Optional<User> searchByUsernameWithAvatar(String username);

    @Query("select u from User u join fetch u.userAvatar where u.id=:id")
    Optional<User> searchByIdWithAvatar(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> searchById(Long id);

    @Modifying
    @Query("update User u set u.enabled=true where u.id=:id")
    void enableUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
