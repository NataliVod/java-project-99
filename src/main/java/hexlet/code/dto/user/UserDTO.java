package hexlet.code.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.ToString;
import lombok.Getter;
import lombok.Setter;


import java.util.Date;

@Getter
@Setter
public class UserDTO {

    private Long id;

    private String firstName;

    private String lastName;

    @NotNull
    @Email
    private String email;

    private Date createdAt;
    private Date updatedAt;
}
