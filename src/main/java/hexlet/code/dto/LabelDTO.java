package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LabelDTO {

    private JsonNullable<Long> id;

    @NotNull
    @Size(min = 3, max = 1000)
    private JsonNullable<String> name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private JsonNullable<Date> createdAt;

}
