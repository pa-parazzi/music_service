package org.musicservice.demo.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistrationRequest {

    @Pattern(regexp = "^[A-Za-zА-Яа-яЁё]+[0-9]*$", message = "Имя должно содержать буквы и цифры")
    @NotBlank(message = "Обязательное поле")
    @Size(min = 5, max = 50, message = "Минимальное количество символов 5")
    private String username;

    @NotBlank(message = "Обязательное поле")
    @Size(min = 5, max = 255, message = "Минимальное количество символов 5")
    private String password;

    @NotBlank(message = "Обязательное поле")
    @Email(message = "Не валидный электронный адрес")
    private String email;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "Обязательное поле")
    private LocalDate dateOfBirth;

}
