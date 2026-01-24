package org.musicservice.demo.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.entity.image.UserAvatar;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
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

    // Блокировка аккаунта Spring Security при 3 неудачных логинах
    public boolean isAccountNonLocked(){
        if(lockTime==null){
            return true;
        }else if(lockTime.isBefore(LocalDateTime.now())){
            this.lockTime=null;
            this.failedLoginAttempts=0;
            return true;
        }
        return false;
    }

    public long getRemainingLockSeconds(){
        if(lockTime==null){
            return 0;
        }
        return Duration.between(LocalDateTime.now(), lockTime).toMinutes();
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
