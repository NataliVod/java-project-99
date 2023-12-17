package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private JsonNullable<Long> id;

    private JsonNullable<Integer> index;

    @NotBlank
    private JsonNullable<String> title;

    private JsonNullable<String> content;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    @NotNull
    private JsonNullable<String> status;

    private JsonNullable<List<Long>> labelIds;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private JsonNullable<Date> createdAt;

}
