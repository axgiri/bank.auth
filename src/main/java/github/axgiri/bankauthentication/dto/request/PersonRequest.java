package github.axgiri.bankauthentication.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import github.axgiri.bankauthentication.Enum.RoleEnum;
import github.axgiri.bankauthentication.entity.Person;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PersonRequest {

    @NotNull(message = "first name cannot be null")
    private String firstName;
    
    @NotNull(message = "last name cannot be null")
    private String lastName;

    @NotNull(message = "phone number cannot be null")
    private String phoneNumber;

    @NotNull(message = "password cannot be null")
    private String password;

    private RoleEnum role;

    private boolean isActive;

    public Person toEntity() {
        return new Person()
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPhoneNumber(phoneNumber)
            .setPassword(password)
            .setRoleEnum(role)
            .setCreatedAt(LocalDate.now())
            .setUpdatedAt(LocalDate.now());
    }
}

