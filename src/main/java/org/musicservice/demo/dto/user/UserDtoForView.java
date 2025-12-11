package org.musicservice.demo.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.dto.image.AvatarDto;

import java.time.LocalDate;

@Getter
@Setter
public class UserDtoForView {


    private Long id;
    private String username;
    private String email;
    private LocalDate dateOfBirth;
    private AvatarDto avatar;

    @JsonIgnore
    private boolean enabled;

    @JsonProperty("status")
    public String getStatus(){
        return enabled ? "Аккаунт активирован" : "Аккаунт не активирован";
    }


}
