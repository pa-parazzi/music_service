package org.musicservice.demo.integration.repository.user;

import org.junit.jupiter.api.Test;
import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByUsernameWithAvatar_ShouldReturnsValidUser(){
        User user = entityManager.persistAndFlush(UserDataFactoryIT.user());
        UserAvatar avatar = entityManager.persistAndFlush(UserDataFactoryIT.userAvatar(user));
        user.setUserAvatar(avatar);
        entityManager.clear();

        User result = repository.findByUsernameWithAvatar(user.getUsername()).orElseThrow();
        assertUserWithAvatar(result, user);
    }

    @Test
    void findByUsernameWithAvatar_ShouldReturnsEmpty_WhenUsernameIsIncorrect(){
        User user = entityManager.persistAndFlush(UserDataFactoryIT.user());
        UserAvatar avatar = entityManager.persistAndFlush(UserDataFactoryIT.userAvatar(user));
        user.setUserAvatar(avatar);
        entityManager.clear();
        String username = "incorrect username";

        assertThat(repository.findByUsernameWithAvatar(username).isEmpty());
    }

    @Test
    void findByIdWithAvatar_ShouldReturnsValidUser(){
        User user = entityManager.persistAndFlush(UserDataFactoryIT.user());
        UserAvatar avatar = entityManager.persistAndFlush(UserDataFactoryIT.userAvatar(user));
        user.setUserAvatar(avatar);
        entityManager.clear();

        User result = repository.findByIdWithAvatar(user.getId()).orElseThrow();
        assertUserWithAvatar(result, user);
    }

    @Test
    void findByIdWithAvatar_ShouldReturnsEmpty_WhenIdIsIncorrect(){
        User user = entityManager.persistAndFlush(UserDataFactoryIT.user());
        UserAvatar avatar = entityManager.persistAndFlush(UserDataFactoryIT.userAvatar(user));
        user.setUserAvatar(avatar);
        entityManager.clear();

        assertThat(repository.findByIdWithAvatar(264L)).isEmpty();
    }

    @Test
    void enableUser_ShouldSuccessEnabledUser(){
        User user = entityManager.persistAndFlush(UserDataFactoryIT.user());
        Long userId = user.getId();
        user.setEnabled(false);
        entityManager.clear();

        repository.enableUser(userId);
        entityManager.flush();
        entityManager.clear();

        User actual = entityManager.find(User.class, userId);
        assertThat(actual.isEnabled()).isTrue();
    }

    @Test
    void enableUser_ShouldNothing_WhenUserIdIsIncorrect(){
        User user = entityManager.persistAndFlush(UserDataFactoryIT.user());
        user.setEnabled(false);
        entityManager.clear();

        repository.enableUser(4878L);
        entityManager.flush();
        entityManager.clear();

        User actual = entityManager.find(User.class, user.getId());
        assertThat(actual.isEnabled()).isFalse();
    }

    private void assertUserWithAvatar(User actual, User expected){
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
        assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        assertThat(actual.getRole()).isEqualTo(expected.getRole());
        assertThat(actual.getUserAvatar().getId()).isEqualTo(expected.getUserAvatar().getId());
        assertThat(actual.getUserAvatar().getKey()).isEqualTo(expected.getUserAvatar().getKey());
    }


}
