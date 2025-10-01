package org.musicservice.demo.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDtoForLogin {

    @NotBlank(message = "Пожалуйста, заполните имя пользоваля")
    @Size(min = 2, max = 50, message = "Минимальное количество символов 2")
    private String username;

    @NotBlank(message = "Пожалуйста, заполните пароль")
    @Size(min = 5, max = 255, message = "Минимальное количество символов 5")
    private String password;
}
