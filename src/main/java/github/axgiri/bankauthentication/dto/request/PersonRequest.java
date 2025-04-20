package github.axgiri.bankauthentication.dto.request;

import java.time.LocalDate;

import github.axgiri.bankauthentication.Enum.RoleEnum;
import github.axgiri.bankauthentication.entity.Person;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PersonRequest {

    @NotNull(message = "first name cannot be null")
    private String firstName;
    
    @NotNull(message = "last name cannot be null")
    private String lastName;

    @NotNull(message = "phone number cannot be null")
    private String phoneNumber;

    @NotNull(message = "email cannot be null")
    private String email;

    @NotNull(message = "password cannot be null")
    private String password;

    private RoleEnum role;

    public Person toEntity() {
        return new Person()
            .setFirstName(firstName)
            .setLastName(lastName)
            .setPhoneNumber(phoneNumber)
            .setEmail(email)
            .setPassword(password)
            .setCreatedAt(LocalDate.now())
            .setUpdatedAt(LocalDate.now());
    }
}

