package org.musicservice.demo.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.entity.image.UserAvatar;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime lockTime;

    // Флаг, показывает активирован ли аккаунт (через email)
    @Column(name="enabled")
    private boolean enabled;

    @Enumerated(value = EnumType.STRING)
    private Authority role;

    @OneToOne(mappedBy = "owner", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
    private UserAvatar userAvatar;

    public User(){}

    // Конструктор для регистрации пользователя
    public User(String username, String password, String email, LocalDate dateOfBirth, Authority role){
        this.username = username;
        this.password = password;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.role = role;
    }

    // Учетная запись не заблокирована?
    public boolean isAccountNonLocked(){
        return lockTime == null || lockTime.isBefore(LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
