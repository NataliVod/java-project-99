package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private Long id;

    private Integer index;

    private String title;

    private String content;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private String status;

    private Date createdAt;
}