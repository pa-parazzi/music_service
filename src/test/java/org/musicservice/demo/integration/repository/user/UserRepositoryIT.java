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

}
