package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskDTO;
import hexlet.code.exeption.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import hexlet.code.utils.UserUtils;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
public class TasksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskMapper mapper;

    private Task testTask;

    private TaskStatus anotherTaskStatus;

    private User anotherUser;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserUtils userUtils;

    @BeforeEach
    public void setUp() {
        var user = userUtils.getTestUser();
        anotherUser = Instancio.of(modelGenerator.getUserModel()).create();

        var taskStatus = taskStatusRepository.findBySlug("to_publish")
                .orElseThrow(() -> new ResourceNotFoundException("status not found"));
        anotherTaskStatus = taskStatusRepository.findBySlug("published")
                .orElseThrow(() -> new ResourceNotFoundException("status not found"));

        userRepository.save(user);
        userRepository.save(anotherUser);

        testTask = Instancio.of(modelGenerator.getTaskModel())
                .set(Select.field(Task::getAssignee), null)
                .create();
        testTask.setLabels(Set.of());
        testTask.setAssignee(user);
        testTask.setTaskStatus(taskStatus);
        taskRepository.save(testTask);
    }

    @Test
    public void testIndex() throws Exception {
        //   taskRepository.save(testTask);
        var result = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testShow() throws Exception {

        //   taskRepository.save(testTask);

        var request = get("/api/tasks/{id}", testTask.getId()).with(jwt());
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("index").isEqualTo(testTask.getIndex()),
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug()),
                v -> v.node("assignee_id").isEqualTo(testTask.getAssignee().getId())

        );
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getTaskModel()).create();
        var dto = mapper.map(data);
        dto.setStatus(JsonNullable.of(status().toString()));
        var request = post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var task = taskRepository.findByName(data.getName()).get();

        assertThat(task).isNotNull();
        assertThat(task.getName()).isEqualTo(data.getName());
        assertThat(task.getIndex()).isEqualTo(data.getIndex());
        assertThat(task.getTaskStatus()).isEqualTo(data.getTaskStatus());
        assertThat(task.getAssignee()).isEqualTo(data.getAssignee());
    }

    @Test
    public void testCreateWithWrongAssignee() throws Exception {
        var dto = mapper.map(testTask);
        long newId = 12345L;
        dto.setAssigneeId(JsonNullable.of(newId));

        var request = post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdate() throws Exception {
        var data = Instancio.of(modelGenerator.getTaskModel()).create();

        var request = put("/api/tasks/{id}" + testTask.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var task = taskRepository.findById(testTask.getId()).orElse(null);

        assertThat(task.getName()).isEqualTo(data.getName());
        assertThat(task.getIndex()).isEqualTo(data.getIndex());
        assertThat(task.getDescription()).isEqualTo(data.getDescription());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(data.getTaskStatus());
        assertThat(task.getAssignee().getId()).isEqualTo(data.getAssignee());
    }

    @Test
    public void testPartialUpdate() throws Exception {

        var dto = new TaskDTO();
        dto.setAssigneeId(JsonNullable.of(anotherUser.getId()));
        dto.setStatus(JsonNullable.of(anotherTaskStatus.getSlug()));

        var request = put("/api/tasks/{id}" + testTask.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var task = taskRepository.findById(testTask.getId()).get();

        assertThat(task.getName()).isEqualTo(testTask.getName());
        assertThat(task.getIndex()).isEqualTo(testTask.getIndex());
        assertThat(task.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(task.getTaskStatus()).isEqualTo(testTask.getTaskStatus());
        assertThat(task.getAssignee().getId()).isEqualTo(testTask.getAssignee().getId());
    }

    public void testDestroy() throws Exception {
        taskRepository.save(testTask);
        var request = delete("/api/tasks/{id}", testTask.getId()).with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isOk());

        assertThat(taskRepository.existsById(testTask.getId())).isEqualTo(false);
    }
}