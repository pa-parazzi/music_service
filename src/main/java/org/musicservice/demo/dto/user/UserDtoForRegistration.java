package org.musicservice.demo.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.validator.constraints.UniqueElements;
import org.musicservice.demo.dto.image.AvatarDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class UserDtoForRegistration {

    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё]+[0-9]*$", message = "Имя может содержать буквы латинского, русского алфавита и цифры")
    @NotBlank(message = "Пожалуйста, заполните имя пользоваля")
    @Size(min = 2, max = 50, message = "Минимальное количество символов 2")
    private String username;

    @NotBlank(message = "Пожалуйста, заполните пароль")
    @Size(min = 5, max = 255, message = "Минимальное количество символов 5")
    private String password;

    @NotBlank(message = "Пожалуйста, введите почту")
    @Email(message = "Не валидный email адрес")
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Пожалуйста, заполните дату рождения")
    private LocalDate dateOfBirth;

    private AvatarDto avatar;
}
