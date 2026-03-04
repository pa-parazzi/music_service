package org.musicservice.demo.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.entity.image.UserAvatar;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts;

    // Хранит время блокировки аккаунта, null - аккаунт активен
    @Column(name = "lock_time")
    private Instant lockTime;

    // Флаг, показывает активирован ли аккаунт (через email)
    @Column(name="enabled")
    private boolean enabled;

    @Column(name = "role_name")
    @Enumerated(value = EnumType.STRING)
    private Authority role;

    public User(){}

    // Конструктор для регистрации пользователя
    public User(String username, String password, String email, LocalDate dateOfBirth){
        this.username = username;
        this.password = password;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.role = Authority.USER;
    }

    // Учетная запись не заблокирована?
    public boolean isAccountNonLocked(){
        return lockTime == null || lockTime.isBefore(Instant.now());
    }

}
