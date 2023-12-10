package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private List<Long> labelIds = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;

}