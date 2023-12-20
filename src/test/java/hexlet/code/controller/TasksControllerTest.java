package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskDTO;
import hexlet.code.exeption.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    private LabelRepository labelRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskMapper mapper;

    private Task testTask;
    private TaskStatus taskStatus;
    private TaskStatus anotherTaskStatus;
    private User user;
    private User anotherUser;
    private Label label;
    private Label anotherLabel;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserUtils userUtils;

    @BeforeEach
    public void setUp() {
        user = userUtils.getTestUser();
        anotherUser = Instancio.of(modelGenerator.getUserModel()).create();

        taskStatus = taskStatusRepository.findBySlug("to_publish")
                .orElseThrow(() -> new ResourceNotFoundException("status not found"));
        anotherTaskStatus = taskStatusRepository.findBySlug("published")
                .orElseThrow(() -> new ResourceNotFoundException("status not found"));

        label = labelRepository.findByName("feature")
                .orElseThrow(() -> new ResourceNotFoundException("label not found"));
        anotherLabel = labelRepository.findByName("bug")
                .orElseThrow(() -> new ResourceNotFoundException("label not found"));

        userRepository.save(user);
        userRepository.save(anotherUser);

        var labels = new HashSet<Label>(Set.of(label));

        testTask = Instancio.of(modelGenerator.getTaskModel())
                .set(Select.field(Task::getAssignee), null)
                .create();
        testTask.setLabels(labels);
        testTask.setAssignee(user);
        testTask.setTaskStatus(taskStatus);
        taskRepository.save(testTask);
    }

    @Test
    public void testIndex() throws Exception {
        var result = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testIndexWithTitleContains() throws Exception {
        var partOfName = testTask.getName().substring(1);
        var result = mockMvc.perform(get("/api/tasks?titleCont=" + partOfName).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("title").asString().containsIgnoringCase(partOfName))
        );
    }

    @Test
    public void testIndexWithAssigneeId() throws Exception {
        var assigneeId = testTask.getAssignee().getId();
        var result = mockMvc.perform(get("/api/tasks?assigneeId=" + assigneeId).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("assignee_id").isEqualTo(assigneeId))
        );
    }

    @Test
    public void testIndexWithLabelId() throws Exception {
        var result = mockMvc.perform(get("/api/tasks?labelId=1").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
        assertThat(body).contains(String.valueOf(1));
    }

    @Test
    public void testIndexWithStatus() throws Exception {
        var slug = testTask.getTaskStatus().getSlug();
        var result = mockMvc.perform(get("/api/tasks?status=" + slug).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("status").isEqualTo(slug))
        );
    }

    @Test
    public void testIndexComplexCondition() throws Exception {
        var partOfName = testTask.getName().substring(1);
        var assigneeId = testTask.getAssignee().getId();
        var slug = testTask.getTaskStatus().getSlug();
        var request = get("/api/tasks?titleCont=" + partOfName + "&assigneeId=" + assigneeId
                + "&status=" + slug);
        var result = mockMvc.perform(request.with(jwt()))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("title").asString().containsIgnoringCase(partOfName))
                        .and(v -> v.node("assignee_id").isEqualTo(assigneeId))
                        .and(v -> v.node("status").isEqualTo(slug))
        );
    }

    @Test
    public void testShow() throws Exception {
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

    @Transactional
    @Test
    public void testCreate() throws Exception {
        var data = new TaskDTO();
        data.setTitle(JsonNullable.of("New Name"));
        data.setContent(JsonNullable.of("New content"));
        data.setStatus(JsonNullable.of(taskStatus.getSlug()));
        data.setTaskLabelIds(JsonNullable.of(List.of()));
        data.setAssigneeId(JsonNullable.of(user.getId()));

        var request = post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var task = taskRepository.findByName(data.getTitle().get()).orElse(null);

        assertThat(task).isNotNull();
        assertThat(task.getName()).isEqualTo(data.getTitle().get());
        assertThat(task.getDescription()).isEqualTo(data.getContent().get());
        assertThat(task.getTaskStatus().getSlug()).isEqualTo(data.getStatus().get());
        assertThat(task.getAssignee().getId()).isEqualTo(data.getAssigneeId().get());
        assertThat(task.getLabels())
                .extracting(Label::getId)
                .containsExactlyElementsOf(data.getTaskLabelIds().get());
    }

    @Transactional
    @Test
    public void testUpdate() throws Exception {
        var task = Instancio.of(modelGenerator.getTaskModel())
                .create();
        var labels = new HashSet<Label>(Set.of(anotherLabel));
        task.setLabels(labels);
        task.setAssignee(anotherUser);
        task.setTaskStatus(anotherTaskStatus);
        var data = mapper.map(task);


        var request = put("/api/tasks/" + testTask.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());
        var updatedTask = taskRepository.findById(testTask.getId()).orElse(null);
        assertThat(updatedTask).isNotNull();
        assertThat(updatedTask.getName()).isEqualTo(data.getTitle().get());
        assertThat(updatedTask.getDescription()).isEqualTo(data.getContent().get());
        assertThat(updatedTask.getTaskStatus().getSlug()).isEqualTo(data.getStatus().get());
        assertThat(updatedTask.getAssignee().getId()).isEqualTo(data.getAssigneeId().get());
        assertThat(updatedTask.getLabels())
                .extracting(Label::getId)
                .containsExactlyElementsOf(data.getTaskLabelIds().get());
    }

    @Test
    public void testPartialUpdate() throws Exception {
        var data = new HashMap<String, String>();
        var name = "New Task Name";
        data.put("name", name);

        var request = put("/api/tasks/" + testTask.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    /*@Transactional
    @Test
    public void testPartialUpdate() throws Exception {

        var data = new TaskDTO();
        data.setTitle(JsonNullable.of("AnotherName"));
        data.setContent(JsonNullable.of("Another content"));
        data.setStatus(JsonNullable.of(anotherTaskStatus.getSlug()));

        var request = put("/api/tasks/" + testTask.getId()).with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());
        var updatedTask = taskRepository.findById(testTask.getId()).orElse(null);
        assertThat(updatedTask).isNotNull();
        assertThat(updatedTask.getName()).isEqualTo(data.getTitle().get());
        assertThat(updatedTask.getDescription()).isEqualTo(data.getContent().get());
        assertThat(updatedTask.getTaskStatus().getSlug()).isEqualTo(data.getStatus().get());
    }*/

    @Test
    public void testDestroy() throws Exception {

        var request = delete("/api/tasks/{id}", testTask.getId()).with(jwt());
        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(testTask.getId())).isEqualTo(false);
    }

}
