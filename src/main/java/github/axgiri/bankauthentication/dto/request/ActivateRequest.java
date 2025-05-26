package github.axgiri.bankauthentication.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ActivateRequest {

    @NotNull(message = "phone number cannot be null")
    private String phoneNumber;

    @NotNull(message = "otp cannot be null")
    private int otp;
}
