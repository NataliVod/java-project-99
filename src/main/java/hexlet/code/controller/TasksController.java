package hexlet.code.controller;

import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tasks")
public class TasksController {
    private final TaskService taskService;
    private final UserRepository userRepository;
    private TaskMapper taskMapper;

    @Operation(summary = "Get specific task by its id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task found"),
        @ApiResponse(responseCode = "404", description = "Task with that id not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorised")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(
            @Parameter(description = "Id of task to be found")
            @PathVariable Long id) {
        TaskDTO taskDTO = taskService.findById(id);
        return ResponseEntity.ok(taskDTO);
    }

    @Operation(summary = "Get list of all tasks")
    @ApiResponse(responseCode = "200", description = "List of all tasks")
    @ApiResponse(responseCode = "401", description = "Unauthorised")
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(
            @Parameter(description = "Task data to search")
            TaskParamsDTO taskData
    ) {
        List<TaskDTO> taskDTOList = taskService.getAll(taskData);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskDTOList.size()))
                .body(taskDTOList);
    }

    @Operation(summary = "Create new task")
    @ApiResponse(responseCode = "201", description = "Task created")
    @ApiResponse(responseCode = "401", description = "Unauthorised")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @Parameter(description = "Task data to save")
            @Valid @RequestBody TaskDTO taskData) {

        Task task = taskService.create(taskData);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.map(task));
    }

    @Operation(summary = "Update task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task updated"),
        @ApiResponse(responseCode = "404", description = "Task with that id not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorised"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @Parameter(description = "Id of label to be updated")
            @PathVariable Long id,
            @Parameter(description = "Task data to update")
            @Valid @RequestBody TaskDTO taskData) {
        Task task = taskService.update(taskData, id);
        return ResponseEntity.ok(taskMapper.map(task));
    }

    @Operation(summary = "Delete task by his id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task deleted"),
        @ApiResponse(responseCode = "404", description = "Task with that id not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorised")
    })
    @DeleteMapping("/{id}")
     public ResponseEntity<Void> deleteTask(
           @Parameter(description = "Id of task to be deleted")
           @PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
