package org.musicservice.demo.dto.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.Authority.Authority;

@Getter
@Setter
public class AdminDto {

    private String username;
    private String password;

    @JsonIgnore
    private Authority role;

    @JsonProperty("status")
    public String getStatus(){
        if(role.getAuthority().equals("ROLE_ADMIN")){
            return "Администратор";
        }
        return "Пользователь";
    }
}
