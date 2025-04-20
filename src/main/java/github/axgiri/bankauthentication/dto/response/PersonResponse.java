package github.axgiri.bankauthentication.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import github.axgiri.bankauthentication.entity.Person;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PersonResponse {
    
    @JsonProperty("id")
    private Long id;

    @JsonProperty("version")
    private Long version;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("created_at")
    private LocalDate createdAt;

    @JsonProperty("updated_at")
    private LocalDate updatedAt;
    
    public static PersonResponse fromEntityToDto(Person person) {
        return new PersonResponse()
            .setId(person.getId())
            .setVersion(person.getVersion())
            .setFirstName(person.getFirstName())
            .setLastName(person.getLastName())
            .setPhoneNumber(person.getPhoneNumber())
            .setCreatedAt(person.getCreatedAt())
            .setUpdatedAt(person.getUpdatedAt());
    }
}
