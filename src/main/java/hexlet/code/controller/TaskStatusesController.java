package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/task_statuses")
public class TaskStatusesController {
    private final TaskStatusService taskStatusService;

    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> getTaskStatusById(@PathVariable Long id) {
        TaskStatusDTO taskStatusDTO = taskStatusService.findById(id);
        return ResponseEntity.ok(taskStatusDTO);
    }

    @GetMapping
    public ResponseEntity<List<TaskStatusDTO>> getAllTaskStatuses() {
        List<TaskStatusDTO> taskStatusDTOList = taskStatusService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatusDTOList.size()))
                .body(taskStatusDTOList);
    }

    @PostMapping
    public ResponseEntity<TaskStatusDTO> createTaskStatus(@Valid @RequestBody TaskStatusDTO taskData) {
        TaskStatusDTO taskStatusDTO = taskStatusService.create(taskData);
        return new ResponseEntity<>(taskStatusDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> updateTaskStatus(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateDTO taskData) {
        TaskStatusDTO taskStatusDTO = taskStatusService.update(taskData, id);
        return ResponseEntity.ok(taskStatusDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskStatus(@PathVariable Long id) {
        taskStatusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}