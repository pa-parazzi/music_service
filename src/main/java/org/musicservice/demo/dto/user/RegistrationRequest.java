package org.musicservice.demo.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistrationRequest {

    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё]+[0-9]*$", message = "Имя должно буквы латинского, русского алфавита и цифры")
    @NotBlank(message = "Пожалуйста, заполните имя пользоваля")
    @Size(min = 2, max = 50, message = "Минимальное количество символов 2")
    private String username;

    @NotBlank(message = "Пожалуйста, заполните пароль")
    @Size(min = 5, max = 255, message = "Минимальное количество символов 5")
    private String password;

    @NotBlank(message = "Пожалуйста, введите почту")
    @Email(message = "Не валидный email адрес")
    private String email;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "Пожалуйста, заполните дату рождения")
    private LocalDate dateOfBirth;

}
