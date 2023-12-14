package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private JsonNullable<Long> id;

    private JsonNullable<Integer> index;

    private JsonNullable<String> title;

    private JsonNullable<String> content;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;

    private JsonNullable<String> status;

    private JsonNullable<List<Long>> labelIds;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private JsonNullable<Date> createdAt;

}