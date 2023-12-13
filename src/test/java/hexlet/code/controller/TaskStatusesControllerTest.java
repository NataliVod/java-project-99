package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import hexlet.code.utils.UserUtils;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusMapper mapper;

    private TaskStatus testTaskStatus;

    @Autowired
    private ModelGenerator modelGenerator;

    @BeforeEach
    public void setUp() {
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
    }

    @Test
    public void testIndex() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var result = mockMvc.perform(get("/api/task_statuses").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {

        taskStatusRepository.save(testTaskStatus);

        var request = get("/api/task_statuses/{id}", testTaskStatus.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testTaskStatus.getName()),
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug())
        );
    }

    @Test
    public void testCreate() throws Exception {
        var dto = mapper.map(testTaskStatus);

        var request = post("/api/task_statuses")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var taskStatus = taskStatusRepository.findBySlug(testTaskStatus.getSlug()).get();

        assertThat(taskStatus).isNotNull();
        assertThat(taskStatus.getName()).isEqualTo(testTaskStatus.getName());
        assertThat(taskStatus.getSlug()).isEqualTo(testTaskStatus.getSlug());
    }

    @Test
    public void testUpdate() throws Exception {
        taskStatusRepository.save(testTaskStatus);

        var dto = new TaskStatusDTO();
        String expectedName = "NewName";
        String expectedSlug = "new_slug";
        dto.setName(JsonNullable.of(expectedName));
        dto.setSlug(JsonNullable.of(expectedSlug));


        var request = put("/api/task_statuses/{id}", testTaskStatus.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var task = taskStatusRepository.findById(testTaskStatus.getId()).orElse(null);

        assertThat(task.getName()).isEqualTo(expectedName);
        assertThat(task.getSlug()).isEqualTo(expectedSlug);
    }

    @Test
    public void testPartialUpdate() throws Exception {
        taskStatusRepository.save(testTaskStatus);

        var dto = new TaskStatusDTO();
        String expectedName = "AnotherName";
        dto.setName(JsonNullable.of(expectedName));

        var request = put("/api/task_statuses/{id}", testTaskStatus.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var task = taskStatusRepository.findById(testTaskStatus.getId()).get();

        assertThat(task.getName()).isEqualTo(expectedName);
        assertThat(task.getSlug()).isEqualTo(testTaskStatus.getSlug());

    }

    public void testDestroy() throws Exception {
        taskStatusRepository.save(testTaskStatus);
        var request = delete("/api/task_statuses/{id}", testTaskStatus.getId()).with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isOk());

        assertThat(taskStatusRepository.existsById(testTaskStatus.getId())).isEqualTo(false);
    }
}
