package org.musicservice.demo.repository.user;

import org.musicservice.demo.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u left join fetch u.userAvatar where u.username=:username")
    Optional<User> searchByUsernameWithAvatar(String username);

    @Query("select u from User u left join fetch u.userAvatar where u.id=:id")
    Optional<User> searchByIdWithAvatar(Long id);

    // Увеличение значения: int failedLoginAttempts + 1 (неудачные попытки при логине)
    @Modifying
    @Query("update User u set u.failedLoginAttempts = u.failedLoginAttempts + 1 where u.username=:username")
    int incrementFailedAttempts(String username);

    // Выставляет время блокировки: LocalDateTime lockTime, соответственно указаканному времени аргумент,
    // при условии достижения максимального числа maxAttempts и если запись еще не была заблокирована
    @Modifying
    @Query("update User u set u.lockTime=:lockTime where u.username=:username and u.failedLoginAttempts>=:maxAttempts and u.lockTime is null")
    int lockUserIfMaxLoginAttempts(@Param("username")String username, @Param("lockTime") LocalDateTime lockTime, @Param("maxAttempts") int maxAttempts);

    // Обнуляет показатели: неудачные попытки логина, время блокировки;
    // если неудачные попытки были зарегистрированы и значения времени блокировки присутствует
    @Modifying
    @Query("update User u set u.failedLoginAttempts=0, u.lockTime=null where u.username=:username and u.failedLoginAttempts > 0 and u.lockTime is not null")
    int resetFailedLoginAttempts(@Param("username") String username);

    @Modifying
    @Query("update User u set u.enabled=true where u.id=:id")
    int enableUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
