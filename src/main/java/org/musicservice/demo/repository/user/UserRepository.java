package org.musicservice.demo.repository.user;

import org.musicservice.demo.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Modifying
    @Query("update User u set u.enabled=true where u.id=:id")
    void enableUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
