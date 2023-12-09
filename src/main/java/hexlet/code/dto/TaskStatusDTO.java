package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String Slug;

    private Date createdAt;

}