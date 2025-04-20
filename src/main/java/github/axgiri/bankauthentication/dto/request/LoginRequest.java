package github.axgiri.bankauthentication.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotNull(message = "phone number cannot be null")
    private String phoneNumber;

    @NotNull(message = "password cannot be null")
    private String password;
}
