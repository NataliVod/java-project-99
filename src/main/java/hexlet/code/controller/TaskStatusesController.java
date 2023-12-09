package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/task_statuses")
public class TaskStatusesController {
    private final TaskStatusService taskStatusService;

    @Operation(summary = "Get specific task status by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status found"),
            @ApiResponse(responseCode = "404", description = "Task status with that id not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> getTaskStatusById(
            @Parameter(description = "Id of task status to be found")
            @PathVariable Long id) {
        TaskStatusDTO taskStatusDTO = taskStatusService.findById(id);
        return ResponseEntity.ok(taskStatusDTO);
    }

    @Operation(summary = "Get list of all task statuses")
    @ApiResponse(responseCode = "200", description = "List of all tasks")
    @GetMapping
    public ResponseEntity<List<TaskStatusDTO>> getAllTaskStatuses() {
        List<TaskStatusDTO> taskStatusDTOList = taskStatusService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatusDTOList.size()))
                .body(taskStatusDTOList);
    }

    @Operation(summary = "Create new task status")
    @ApiResponse(responseCode = "201", description = "Task status created")
    @PostMapping
    public ResponseEntity<TaskStatusDTO> createTaskStatus(
            @Parameter(description = "Task status data to save")
            @Valid @RequestBody TaskStatusDTO taskData) {
        TaskStatusDTO taskStatusDTO = taskStatusService.create(taskData);
        return new ResponseEntity<>(taskStatusDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Update task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status updated"),
            @ApiResponse(responseCode = "404", description = "Task status with that id not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> updateTaskStatus(
            @Parameter(description = "Id of task status to be updated")
            @PathVariable Long id,
            @Parameter(description = "Task status data to update")
            @Valid @RequestBody TaskStatusUpdateDTO taskData) {
        TaskStatusDTO taskStatusDTO = taskStatusService.update(taskData, id);
        return ResponseEntity.ok(taskStatusDTO);
    }

    @Operation(summary = "Delete task status by his id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status deleted"),
            @ApiResponse(responseCode = "404", description = "Task status with that id not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskStatus(
            @Parameter(description = "Id of task status to be deleted")
            @PathVariable Long id) {
        taskStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}