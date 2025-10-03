package org.musicservice.demo.model.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.model.image.Avatar;
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
    @NotBlank
    @Size(min = 2, max = 20)
    private String username;

    @Column
    @NotBlank
    @Size(min = 5, max = 100)
    private String password;

    @Column(name = "email")
    @NotBlank
    @Email
    private String email;

    @Column(name = "date_of_birth")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @NotNull
    private LocalDate dateOfBirth;

    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts;

    // Хранит время блокировки аккаунта, null - аккаунт активен
    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    // Флаг, показывает активирован ли аккаунт (через email)
    @Column(name="enabled")
    private boolean enabled;

    // Токен активации аккаунта
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private VerificationToken verificationToken;

    @OneToOne(mappedBy = "user")
    private RefreshToken refreshToken;

    @Enumerated(value = EnumType.STRING)
    private Authority role;

    @OneToOne(mappedBy = "owner")
    private Avatar avatar;

    public User(){}

    public User(String username, String password, String email, LocalDate dateOfBirth, Boolean enabled, Authority role, Avatar avatar) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.enabled = enabled;
        this.role = role;
        this.avatar = avatar;
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
}
