package org.musicservice.demo.repository.user;

import org.musicservice.demo.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> searchByUsername(String username);

    User findByUsername(String username);
}
