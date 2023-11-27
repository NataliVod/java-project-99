package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    private JsonNullable<String> firstName;

    private JsonNullable<String> lastName;

    @Email
    private JsonNullable<String> email;

    @NotNull
    @Size(min = 3)
    private JsonNullable<String> password;
}
