package github.axgiri.bankauthentication.dto.response;

import lombok.Data;

@Data
public class AuthResponse {

    private String token;
    private PersonResponse person;

    public AuthResponse(String token, PersonResponse person) {
        this.token = token;
        this.person = person;
    }
}
